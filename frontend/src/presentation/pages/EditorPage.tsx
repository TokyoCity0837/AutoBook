import { useEditor, EditorContent } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import Placeholder from '@tiptap/extension-placeholder'
import { Extension } from '@tiptap/core'
import { TextStyleKit } from '@tiptap/extension-text-style'
import TextAlign from '@tiptap/extension-text-align'
import CharacterCount from '@tiptap/extension-character-count'
import { PaginationPlus, PAGE_SIZES } from 'tiptap-pagination-plus'
import { useState, useRef, useEffect, useCallback } from 'react'
import { Document, Paragraph, TextRun, HeadingLevel, Packer, AlignmentType } from 'docx'
import { saveAs } from 'file-saver'
import { pdf, Document as PdfDocument, Page, Text, StyleSheet } from '@react-pdf/renderer'
import { useParams } from 'react-router-dom'
import Heading from '@tiptap/extension-heading'
import '../../assets/styles/editorPage.css'
import apiClient from '../../data/api/apiClient'

// ─── Types ────────────────────────────────────────────────────────────────────

interface ChapterAnchor {
    anchorId: string
    title: string
    orderIndex: number
}

interface BookData {
    id: number
    title: string
    description: string
    genre: string
    privacy: string
    coverImage: string
    font?: string
    fontSize?: number
    lineHeight?: string
    paraStyle?: number
}

// ─── Constants ────────────────────────────────────────────────────────────────

const FONTS = [
    { label: 'Georgia', value: 'Georgia, serif' },
    { label: 'Times New Roman', value: '"Times New Roman", serif' },
    { label: 'Garamond', value: 'Garamond, serif' },
    { label: 'Inter', value: 'Inter, sans-serif' },
    { label: 'Courier New', value: '"Courier New", monospace' },
]
const SIZES = [11, 12, 13, 14, 15, 16, 18, 20, 24, 28, 32]
const LINE_HEIGHTS = [
    { label: 'Single (1.0)', value: '1.0' },
    { label: 'Compact (1.3)', value: '1.3' },
    { label: 'Normal (1.5)', value: '1.5' },
    { label: 'Book (1.8)', value: '1.8' },
    { label: 'Double (2.0)', value: '2.0' },
]
const PARA_STYLES = [
    { label: 'No indent', firstIndent: '0', spacing: '0.8em' },
    { label: 'First indent', firstIndent: '2em', spacing: '0' },
    { label: 'Spaced', firstIndent: '0', spacing: '1.4em' },
    { label: 'Book style', firstIndent: '1.5em', spacing: '0' },
]

const A4_CONFIG = {
    pageHeight: 1122, pageWidth: 794,
    pageGap: 40, pageGapBorderSize: 0,
    pageGapBorderColor: '#2a2e3a', pageBreakBackground: '#2a2e3a',
    marginTop: 76, marginBottom: 76, marginLeft: 68, marginRight: 68,
    contentMarginTop: 0, contentMarginBottom: 0,
    pageHeaderHeight: 28, pageFooterHeight: 0,
    headerRight: 'Page {page}', headerLeft: '', footerRight: '', footerLeft: '',
}

const AUTOSAVE_DELAY = 2000

// ─── AnchorHeading — extends Heading to preserve data-anchor-id ───────────────

const AnchorHeading = Heading.extend({
    addAttributes() {
        return {
            ...this.parent?.(),
            'data-anchor-id': {
                default: null,
                parseHTML: element => element.getAttribute('data-anchor-id'),
                renderHTML: attributes => {
                    if (!attributes['data-anchor-id']) return {}
                    return { 'data-anchor-id': attributes['data-anchor-id'] }
                },
            },
        }
    },
})

// ─── Tab indent ───────────────────────────────────────────────────────────────

const TabIndent = Extension.create({
    name: 'tabIndent',
    addKeyboardShortcuts() {
        return {
            Tab: () => {
                this.editor.chain().focus().insertContent('    ').run()
                return true
            },
        }
    },
})

// ─── Helpers ──────────────────────────────────────────────────────────────────

function splitHtmlByAnchors(html: string): { anchorId: string; html: string }[] {
    const parser = new DOMParser()
    const doc = parser.parseFromString(html, 'text/html')
    const nodes = Array.from(doc.body.childNodes)

    const chunks: { anchorId: string; html: string }[] = []
    let currentAnchorId: string | null = null
    let currentNodes: Node[] = []

    const flush = () => {
        if (currentAnchorId !== null && currentNodes.length > 0) {
            const div = document.createElement('div')
            currentNodes.forEach(n => div.appendChild(n.cloneNode(true)))
            chunks.push({ anchorId: currentAnchorId, html: div.innerHTML })
        }
    }

    for (const node of nodes) {
        if (
            node.nodeType === Node.ELEMENT_NODE &&
            (node as Element).tagName === 'H1' &&
            (node as Element).hasAttribute('data-anchor-id')
        ) {
            flush()
            currentAnchorId = (node as Element).getAttribute('data-anchor-id')!
            currentNodes = [node]
        } else {
            currentNodes.push(node)
        }
    }
    flush()
    return chunks
}

function mergeChaptersToHtml(chapters: { content: string }[]): string {
    return chapters.map(c => c.content || '').join('')
}

// Read chapter anchors directly from live editor state (never stale)
function readAnchorsFromEditor(editor: any): ChapterAnchor[] {
    const anchors: ChapterAnchor[] = []
    let order = 0
    editor.state.doc.forEach((node: any) => {
        if (node.type.name === 'heading' && node.attrs.level === 1) {
            const anchorId = node.attrs['data-anchor-id']
            if (anchorId) {
                anchors.push({
                    anchorId,
                    title: node.textContent || `Chapter ${order + 1}`,
                    orderIndex: order++,
                })
            }
        }
    })
    return anchors
}

// ─── SVG Icons ────────────────────────────────────────────────────────────────

const I = {
    Bold: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M6 4h8a4 4 0 0 1 0 8H6V4Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /><path d="M6 12h9a4 4 0 0 1 0 8H6v-8Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    Italic: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="19" y1="4" x2="10" y2="4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="14" y1="20" x2="5" y2="20" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="15" y1="4" x2="9" y2="20" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    Underline: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M6 3v7a6 6 0 0 0 12 0V3" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="4" y1="21" x2="20" y2="21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    Strike: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="4" y1="12" x2="20" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><path d="M9 5a4 4 0 0 1 7 2.7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><path d="M9 19a4 4 0 0 0 7-2.7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    H1: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M4 12h8M4 6v12M12 6v12M17 10l2-2v8" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    H2: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M4 12h8M4 6v12M12 6v12M15 9.5a2.5 2.5 0 0 1 5 0c0 2-5 4.5-5 4.5h5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    H3: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M4 12h8M4 6v12M12 6v12M15 9.5a2.5 2.5 0 0 1 4 2c0 1-1 1.5-2 2 1 .5 2.5 1 2.5 2.5A2.5 2.5 0 0 1 15 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    AlignL: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="3" y1="6" x2="21" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="3" y1="12" x2="15" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="3" y1="18" x2="18" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    AlignC: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="3" y1="6" x2="21" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="6" y1="12" x2="18" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="4" y1="18" x2="20" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    AlignR: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="3" y1="6" x2="21" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="9" y1="12" x2="21" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="6" y1="18" x2="21" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    AlignJ: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="3" y1="6" x2="21" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="3" y1="12" x2="21" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="3" y1="18" x2="21" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    BulletList: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="8" y1="6" x2="21" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="8" y1="12" x2="21" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="8" y1="18" x2="21" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><circle cx="3" cy="6" r="1" fill="currentColor" /><circle cx="3" cy="12" r="1" fill="currentColor" /><circle cx="3" cy="18" r="1" fill="currentColor" /></svg>,
    OrderedList: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="10" y1="6" x2="21" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="10" y1="12" x2="21" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="10" y1="18" x2="21" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><path d="M4 6h1v4M4 10h2M4 15.5a1.5 1.5 0 0 1 3 0c0 1-3 3-3 3h3" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    Quote: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M3 21c3 0 7-1 7-8V5c0-1.25-.756-2.017-2-2H4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2 1 0 1 0 1 1v1c0 1-1 2-2 2s-1 .008-1 1.031V20c0 1 0 1 1 1z" stroke="currentColor" strokeWidth="2" /><path d="M15 21c3 0 7-1 7-8V5c0-1.25-.757-2.017-2-2h-4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2h.75c0 2.25.25 4-2.75 4v3c0 1 0 1 1 1z" stroke="currentColor" strokeWidth="2" /></svg>,
    HRule: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><line x1="3" y1="12" x2="21" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    Undo: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M3 7v6h6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /><path d="M3 13A9 9 0 1 0 6 6.3L3 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    Redo: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M21 7v6h-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /><path d="M21 13A9 9 0 1 1 18 6.3L21 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    ChevronDown: () => <svg width="11" height="11" viewBox="0 0 24 24" fill="none"><path d="M6 9l6 6 6-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    Plus: () => <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><path d="M12 5v14M5 12h14" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    Trash: () => <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><polyline points="3 6 5 6 21 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /><path d="M19 6l-1 14H6L5 6M10 11v6M14 11v6M9 6V4h6v2" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    Check: () => <svg width="13" height="13" viewBox="0 0 24 24" fill="none"><polyline points="20 6 9 17 4 12" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" /></svg>,
    Focus: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M3 9V6a1 1 0 0 1 1-1h3M3 15v3a1 1 0 0 0 1 1h3M21 9V6a1 1 0 0 0-1-1h-3M21 15v3a1 1 0 0 1-1 1h-3" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    Chapters: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="7" height="18" rx="1" stroke="currentColor" strokeWidth="2" /><line x1="14" y1="7" x2="21" y2="7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="14" y1="12" x2="21" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /><line x1="14" y1="17" x2="18" y2="17" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
    PanelRight: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" strokeWidth="2" /><line x1="15" y1="3" x2="15" y2="21" stroke="currentColor" strokeWidth="2" /></svg>,
    Export: () => <svg width="15" height="15" viewBox="0 0 24 24" fill="none"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /><polyline points="7 10 12 15 17 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /><line x1="12" y1="15" x2="12" y2="3" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>,
}

// ─── Export Menu ──────────────────────────────────────────────────────────────

function ExportMenu({ editor, bookTitle, fontSize, lineHeight }: {
    editor: any; bookTitle: string; fontSize: number; lineHeight: string
}) {
    const [open, setOpen] = useState(false)
    const ref = useRef<HTMLDivElement>(null)

    useEffect(() => {
        const fn = (e: MouseEvent) => {
            if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false)
        }
        document.addEventListener('mousedown', fn)
        return () => document.removeEventListener('mousedown', fn)
    }, [])

    const exportPDF = async () => {
        setOpen(false)
        if (!editor) return
        const blocks = Array.from(new DOMParser().parseFromString(editor.getHTML(), 'text/html').body.children)
        const pt = Math.round(fontSize * 0.75)
        const styles = StyleSheet.create({
            page: { paddingTop: 76, paddingBottom: 76, paddingLeft: 68, paddingRight: 68, fontFamily: 'Times-Roman', fontSize: pt, lineHeight: parseFloat(lineHeight) },
            h1: { fontSize: pt * 1.9, fontFamily: 'Times-Bold', marginBottom: 32 },
            h2: { fontSize: pt * 1.45, fontFamily: 'Times-Bold', marginBottom: 10 },
            h3: { fontSize: pt * 1.15, fontFamily: 'Times-Bold', marginBottom: 8 },
            p: { marginBottom: 0 },
        })
        const renderBlock = (block: Element, i: number) => {
            const tag = block.tagName.toLowerCase()
            const text = block.textContent ?? ''
            const align = (block as HTMLElement).style.textAlign || 'left'
            if (tag === 'h1') return <Text key={i} style={{ ...styles.h1, textAlign: align as any }}>{text}</Text>
            if (tag === 'h2') return <Text key={i} style={{ ...styles.h2, textAlign: align as any }}>{text}</Text>
            if (tag === 'h3') return <Text key={i} style={{ ...styles.h3, textAlign: align as any }}>{text}</Text>
            return <Text key={i} style={{ ...styles.p, textAlign: align as any }}>{text}</Text>
        }
        try {
            const blob = await pdf(<PdfDocument><Page size="A4" style={styles.page}>{blocks.map(renderBlock)}</Page></PdfDocument>).toBlob()
            saveAs(blob, `${bookTitle}.pdf`)
        } catch (err) { console.error('PDF error:', err) }
    }

    const exportDOCX = async () => {
        setOpen(false)
        if (!editor) return
        const blocks = Array.from(new DOMParser().parseFromString(editor.getHTML(), 'text/html').body.children)
        const pt = Math.round(fontSize * 0.75)
        const headingMap: Record<string, any> = { h1: HeadingLevel.HEADING_1, h2: HeadingLevel.HEADING_2, h3: HeadingLevel.HEADING_3 }
        const alignMap = { left: AlignmentType.LEFT, center: AlignmentType.CENTER, right: AlignmentType.RIGHT, justify: AlignmentType.JUSTIFIED } as const
        const docChildren = blocks.map(block => {
            const tag = block.tagName.toLowerCase()
            const text = block.textContent ?? ''
            if (headingMap[tag]) return new Paragraph({ text, heading: headingMap[tag], spacing: { after: 200 } })
            const runs: TextRun[] = Array.from(block.childNodes).map(node => {
                if (node.nodeType === Node.TEXT_NODE) return new TextRun({ text: node.textContent ?? '', size: pt * 2 })
                const el = node as HTMLElement
                const elTag = el.tagName?.toLowerCase()
                return new TextRun({
                    text: el.textContent ?? '',
                    bold: elTag === 'strong' || elTag === 'b',
                    italics: elTag === 'em' || elTag === 'i',
                    underline: elTag === 'u' ? {} : undefined,
                    strike: elTag === 's' || elTag === 'del',
                    font: el.style?.fontFamily?.split(',')[0].replace(/"/g, '').trim() || undefined,
                    size: pt * 2,
                })
            })
            const textAlign = (block as HTMLElement).style.textAlign || 'left'
            return new Paragraph({
                children: runs.length ? runs : [new TextRun({ text, size: pt * 2 })],
                alignment: alignMap[textAlign as keyof typeof alignMap] ?? AlignmentType.LEFT,
                spacing: { line: Math.round(parseFloat(lineHeight) * 240), after: 0 },
            })
        })
        const blob = await Packer.toBlob(new Document({
            sections: [{ properties: { page: { size: { width: 11906, height: 16838 }, margin: { top: 1440, bottom: 1440, left: 1361, right: 1361 } } }, children: docChildren }],
        }))
        saveAs(blob, `${bookTitle}.docx`)
    }

    const exportTXT = () => {
        setOpen(false)
        const blob = new Blob([editor?.getText() ?? ''], { type: 'text/plain' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a'); a.href = url; a.download = `${bookTitle}.txt`; a.click()
        URL.revokeObjectURL(url)
    }

    return (
        <div className="tbDropdown" ref={ref}>
            <button className="tbDropdownBtn exportBtn" onClick={() => setOpen(o => !o)} title="Export">
                <I.Export /><span className="exportLabel">Export</span><I.ChevronDown />
            </button>
            {open && (
                <div className="tbDropdownMenu exportMenu">
                    <div className="exportItem" onClick={exportPDF}><span className="exportItemIcon">📄</span><div className="exportItemTitle">PDF</div></div>
                    <div className="exportItem" onClick={exportDOCX}><span className="exportItemIcon">📝</span><div className="exportItemTitle">Word (.docx)</div></div>
                    <div className="exportItem" onClick={exportTXT}><span className="exportItemIcon">📃</span><div className="exportItemTitle">Plain text (.txt)</div></div>
                </div>
            )}
        </div>
    )
}

// ─── Dropdown ─────────────────────────────────────────────────────────────────

function Dropdown({ label, options, onSelect, width }: {
    label: string; options: { label: string; value: string }[]
    onSelect: (v: string) => void; width?: number
}) {
    const [open, setOpen] = useState(false)
    const ref = useRef<HTMLDivElement>(null)
    useEffect(() => {
        const fn = (e: MouseEvent) => { if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false) }
        document.addEventListener('mousedown', fn)
        return () => document.removeEventListener('mousedown', fn)
    }, [])
    return (
        <div className="tbDropdown" ref={ref}>
            <button className="tbDropdownBtn" style={width ? { minWidth: width } : {}} onClick={() => setOpen(o => !o)}>
                <span>{label}</span><I.ChevronDown />
            </button>
            {open && (
                <div className="tbDropdownMenu" style={width ? { minWidth: width } : {}}>
                    {options.map(o => (
                        <div key={o.value} className="tbDropdownItem" onClick={() => { onSelect(o.value); setOpen(false) }}>{o.label}</div>
                    ))}
                </div>
            )}
        </div>
    )
}

// ─── Toolbar ──────────────────────────────────────────────────────────────────

function Toolbar({ editor, font, fontSize, lineHeight, paraStyle, focusMode,
    setFontSize, setLineHeight, setParaStyle, setFocusMode }: {
        editor: any; font: string; fontSize: number; lineHeight: string; paraStyle: number; focusMode: boolean
        setFontSize: (v: number) => void; setLineHeight: (v: string) => void
        setParaStyle: (v: number) => void; setFocusMode: (v: boolean) => void
    }) {
    if (!editor) return null
    const D = () => <div className="toolbarDivider" />
    const Btn = ({ active = false, onClick, disabled = false, title, children }: any) => (
        <button className={`toolbarBtn${active ? ' active' : ''}`} onClick={onClick} disabled={disabled} title={title}>{children}</button>
    )
    const selectionFont = editor.getAttributes('textStyle')?.fontFamily || font
    const fontLabel = FONTS.find(f => f.value === selectionFont)?.label ?? selectionFont.split(',')[0].replace(/"/g, '').trim()

    return (
        <div className="editorToolbar">
            {/* Font applies inline to selected text only — does NOT change document font */}
            <Dropdown label={fontLabel} options={FONTS} onSelect={v => editor.chain().focus().setFontFamily(v).run()} width={130} />
            <Dropdown label={`${fontSize}px`} options={SIZES.map(s => ({ label: `${s}px`, value: `${s}` }))} onSelect={v => setFontSize(+v)} width={68} />
            <Dropdown label={`↕ ${lineHeight}`} options={LINE_HEIGHTS} onSelect={setLineHeight} width={120} />
            <Dropdown label={`¶ ${PARA_STYLES[paraStyle].label}`} options={PARA_STYLES.map((p, i) => ({ label: p.label, value: `${i}` }))} onSelect={v => setParaStyle(+v)} width={120} />
            <D />
            <Btn active={editor.isActive('bold')} onClick={() => editor.chain().focus().toggleBold().run()} title="Bold"><I.Bold /></Btn>
            <Btn active={editor.isActive('italic')} onClick={() => editor.chain().focus().toggleItalic().run()} title="Italic"><I.Italic /></Btn>
            <Btn active={editor.isActive('underline')} onClick={() => editor.chain().focus().toggleUnderline().run()} title="Underline"><I.Underline /></Btn>
            <Btn active={editor.isActive('strike')} onClick={() => editor.chain().focus().toggleStrike().run()} title="Strike"><I.Strike /></Btn>
            <D />
            <Btn active={editor.isActive('heading', { level: 1 })} onClick={() => editor.chain().focus().toggleHeading({ level: 1 }).run()} title="H1"><I.H1 /></Btn>
            <Btn active={editor.isActive('heading', { level: 2 })} onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()} title="H2"><I.H2 /></Btn>
            <Btn active={editor.isActive('heading', { level: 3 })} onClick={() => editor.chain().focus().toggleHeading({ level: 3 }).run()} title="H3"><I.H3 /></Btn>
            <D />
            <Btn active={editor.isActive({ textAlign: 'left' })} onClick={() => editor.chain().focus().setTextAlign('left').run()} title="Left"><I.AlignL /></Btn>
            <Btn active={editor.isActive({ textAlign: 'center' })} onClick={() => editor.chain().focus().setTextAlign('center').run()} title="Center"><I.AlignC /></Btn>
            <Btn active={editor.isActive({ textAlign: 'right' })} onClick={() => editor.chain().focus().setTextAlign('right').run()} title="Right"><I.AlignR /></Btn>
            <Btn active={editor.isActive({ textAlign: 'justify' })} onClick={() => editor.chain().focus().setTextAlign('justify').run()} title="Justify"><I.AlignJ /></Btn>
            <D />
            <Btn active={editor.isActive('bulletList')} onClick={() => editor.chain().focus().toggleBulletList().run()} title="Bullet list"><I.BulletList /></Btn>
            <Btn active={editor.isActive('orderedList')} onClick={() => editor.chain().focus().toggleOrderedList().run()} title="Ordered list"><I.OrderedList /></Btn>
            <Btn active={editor.isActive('blockquote')} onClick={() => editor.chain().focus().toggleBlockquote().run()} title="Quote"><I.Quote /></Btn>
            <Btn onClick={() => editor.chain().focus().setHorizontalRule().run()} title="Divider"><I.HRule /></Btn>
            <D />
            <Btn onClick={() => editor.chain().focus().undo().run()} disabled={!editor.can().undo()} title="Undo"><I.Undo /></Btn>
            <Btn onClick={() => editor.chain().focus().redo().run()} disabled={!editor.can().redo()} title="Redo"><I.Redo /></Btn>
            <D />
            <Btn active={focusMode} onClick={() => setFocusMode(!focusMode)} title="Focus mode"><I.Focus /></Btn>
        </div>
    )
}

// ─── Editable label ───────────────────────────────────────────────────────────

function EditableLabel({ value, onChange, className }: { value: string; onChange: (v: string) => void; className?: string }) {
    const [editing, setEditing] = useState(false)
    const [draft, setDraft] = useState(value)
    const ref = useRef<HTMLInputElement>(null)
    useEffect(() => { if (editing) ref.current?.select() }, [editing])
    useEffect(() => { if (!editing) setDraft(value) }, [value, editing])
    const commit = () => { onChange(draft.trim() || value); setEditing(false) }
    if (editing) return (
        <input ref={ref} className={`editableLabelInput ${className ?? ''}`} value={draft}
            onChange={e => setDraft(e.target.value)} onBlur={commit}
            onKeyDown={e => { if (e.key === 'Enter') commit(); if (e.key === 'Escape') { setDraft(value); setEditing(false) } }} />
    )
    return <span className={`editableLabel ${className ?? ''}`} onDoubleClick={() => { setDraft(value); setEditing(true) }}>{value}</span>
}

// ─── Chapters Sidebar ─────────────────────────────────────────────────────────

function ChaptersSidebar({ chapters, activeAnchorId, onScrollTo, onAdd, onRename, onDelete }: {
    chapters: ChapterAnchor[]
    activeAnchorId: string | null
    onScrollTo: (anchorId: string) => void
    onAdd: () => void
    onRename: (anchorId: string, title: string) => void
    onDelete: (anchorId: string) => void
}) {
    return (
        <div className="chaptersSidebar">
            <div className="chaptersTitle">Chapters</div>
            <div className="chaptersList">
                {chapters.map(ch => (
                    <div
                        key={ch.anchorId}
                        className={`chapterItem${activeAnchorId === ch.anchorId ? ' active' : ''}`}
                        onClick={() => onScrollTo(ch.anchorId)}
                    >
                        <div className="chapterItemLeft">
                            <EditableLabel value={ch.title} onChange={t => onRename(ch.anchorId, t)} />
                        </div>
                        <div className="chapterActions">
                            <button className="chapterActionBtn danger" title="Delete" onClick={e => { e.stopPropagation(); onDelete(ch.anchorId) }}><I.Trash /></button>
                        </div>
                    </div>
                ))}
                <button className="addChapterBtn" onClick={onAdd}><I.Plus /> Add chapter</button>
            </div>
        </div>
    )
}

// ─── Right Panel ──────────────────────────────────────────────────────────────

function RightPanel({ wordCount, charCount }: { wordCount: number; charCount: number }) {
    const [tab, setTab] = useState<'stats' | 'edits'>('stats')
    const readingMinutes = Math.max(1, Math.round(wordCount / 200))
    return (
        <div className="editorRightPanel">
            <div className="rightPanelTabs">
                <button className={`rightPanelTab${tab === 'stats' ? ' active' : ''}`} onClick={() => setTab('stats')}>Stats</button>
                <button className={`rightPanelTab${tab === 'edits' ? ' active' : ''}`} onClick={() => setTab('edits')}>Edits</button>
            </div>
            <div className="rightPanelContent">
                {tab === 'stats' ? (
                    <div className="vcStats">
                        <div className="vcStatItem"><span className="vcStatLabel">Words</span><span className="vcStatValue">{wordCount}</span></div>
                        <div className="vcStatItem"><span className="vcStatLabel">Characters</span><span className="vcStatValue">{charCount}</span></div>
                        <div className="vcStatItem"><span className="vcStatLabel">Reading time</span><span className="vcStatValue">~{readingMinutes} min</span></div>
                    </div>
                ) : (
                    <div className="editsList">
                        <div className="updateWrap"><div className="updateText">Suggest changing opening paragraph for better hook</div></div>
                        <div className="updateWrap"><div className="updateText">Grammar fix in second sentence</div></div>
                    </div>
                )}
            </div>
        </div>
    )
}

// ─── Main ─────────────────────────────────────────────────────────────────────

export default function EditorPage() {
    const { id } = useParams<{ id: string }>()

    const [bookData, setBookData] = useState<BookData | null>(null)
    const [bookTitle, setBookTitle] = useState('Loading...')
    const [font, setFont] = useState('Georgia, serif')
    const [fontSize, setFontSize] = useState(15)
    const [lineHeight, setLineHeight] = useState('1.8')
    const [paraStyle, setParaStyle] = useState(0)

    const [focusMode, setFocusMode] = useState(false)
    const [showChapters, setShowChapters] = useState(false)
    const [showRight, setShowRight] = useState(false)
    const [zoom, setZoom] = useState(100)
    const [saveStatus, setSaveStatus] = useState<'saved' | 'saving' | 'unsaved'>('saved')

    const [chapters, setChapters] = useState<ChapterAnchor[]>([])
    const [activeAnchorId, setActiveAnchorId] = useState<string | null>(null)

    const chapterDbIds = useRef<Record<string, number>>({})
    const autosaveTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
    const centerRef = useRef<HTMLDivElement>(null)
    const [zoomBarLeft, setZoomBarLeft] = useState('50%')

    // Always-fresh refs to avoid stale closures in performSave
    const bookDataRef = useRef<BookData | null>(null)
    const bookTitleRef = useRef('Loading...')
    const fontRef = useRef('Georgia, serif')
    const fontSizeRef = useRef(15)
    const lineHeightRef = useRef('1.8')
    const paraStyleRef = useRef(0)
    const editorRef = useRef<any>(null)

    useEffect(() => { bookDataRef.current = bookData }, [bookData])
    useEffect(() => { bookTitleRef.current = bookTitle }, [bookTitle])
    useEffect(() => { fontRef.current = font }, [font])
    useEffect(() => { fontSizeRef.current = fontSize }, [fontSize])
    useEffect(() => { lineHeightRef.current = lineHeight }, [lineHeight])
    useEffect(() => { paraStyleRef.current = paraStyle }, [paraStyle])

    // ── Editor ────────────────────────────────────────────────────────────
    const editor = useEditor({
        extensions: [
            StarterKit.configure({ heading: false }),  // disable default heading
            AnchorHeading.configure({ levels: [1, 2, 3] }),  // use ours with data-anchor-id
            Placeholder.configure({ placeholder: 'Start writing your chapter…' }),
            TextStyleKit,
            TabIndent,
            TextAlign.configure({ types: ['heading', 'paragraph'] }),
            CharacterCount,
            PaginationPlus.configure(A4_CONFIG),
        ],
        content: '',
        onUpdate: ({ editor }) => {
            setSaveStatus('unsaved')
            setChapters(readAnchorsFromEditor(editor))
            scheduleAutosave()
        },
        onSelectionUpdate: ({ editor }) => {
            const { $anchor } = editor.state.selection
            let found: string | null = null
            editor.state.doc.forEach((node: any, offset: number) => {
                if (
                    node.type.name === 'heading' &&
                    node.attrs.level === 1 &&
                    node.attrs['data-anchor-id'] &&
                    offset <= $anchor.pos
                ) {
                    found = node.attrs['data-anchor-id']
                }
            })
            setActiveAnchorId(found)
        },
    })

    useEffect(() => { editorRef.current = editor }, [editor])

    // ── Autosave ──────────────────────────────────────────────────────────
    const scheduleAutosave = useCallback(() => {
        if (autosaveTimer.current) {
            clearTimeout(autosaveTimer.current);
        }
        autosaveTimer.current = setTimeout(performSave, AUTOSAVE_DELAY);
    }, []);

    const performSave = useCallback(async () => {
        const currentEditor = editorRef.current
        if (!id || !currentEditor) return

        setSaveStatus('saving')

        // Save book metadata
        const bd = bookDataRef.current
        if (bd) {
            try {
                await apiClient.put(`/books/${id}`, {
                    title: bookTitleRef.current,
                    description: bd.description,
                    genre: bd.genre,
                    privacy: bd.privacy,
                    coverImage: bd.coverImage,
                    font: fontRef.current,
                    fontSize: fontSizeRef.current,
                    lineHeight: lineHeightRef.current,
                    paraStyle: paraStyleRef.current,
                })
            } catch (e) { console.error('Failed to save book', e) }
        }

        // Split and save chapters
        const html = currentEditor.getHTML()
        const chunks = splitHtmlByAnchors(html)
        const liveAnchors = readAnchorsFromEditor(currentEditor)

        for (const chunk of chunks) {
            const dbId = chapterDbIds.current[chunk.anchorId]
            const title = liveAnchors.find(a => a.anchorId === chunk.anchorId)?.title ?? 'Chapter'

            if (dbId) {
                try {
                    await apiClient.put(`/chapters/${dbId}`, { title, content: chunk.html })
                } catch (e) { console.error(`Failed to update chapter ${dbId}`, e) }
            } else {
                try {
                    const res = await apiClient.post(`/chapters/book/${id}`, { title, content: chunk.html })
                    chapterDbIds.current[chunk.anchorId] = res.data.id
                } catch (e) { console.error('Failed to create chapter', e) }
            }
        }

        setSaveStatus('saved')
    }, [id])

    // ── Load ──────────────────────────────────────────────────────────────
    useEffect(() => {
        if (!id || !editor) return
        let cancelled = false

        apiClient.get(`/books/${id}`).then((res: any) => {
            if (cancelled) return
            const b: BookData = res.data
            setBookData(b)
            setBookTitle(b.title)
            if (b.font) setFont(b.font)
            if (b.fontSize) setFontSize(b.fontSize)
            if (b.lineHeight) setLineHeight(b.lineHeight)
            if (b.paraStyle !== undefined) setParaStyle(b.paraStyle)
        }).catch(console.error)

        apiClient.get(`/chapters/book/${id}`).then(async (res: any) => {
            if (cancelled) return
            const raw: { id: number; title: string; content: string }[] = res.data ?? []

            if (raw.length === 0) {
                const anchorId = `chapter-${Date.now()}`
                const html = `<h1 data-anchor-id="${anchorId}">Chapter 1</h1><p></p>`
                editor.commands.setContent(html)
                setChapters(readAnchorsFromEditor(editor))
                try {
                    const r = await apiClient.post(`/chapters/book/${id}`, { title: 'Chapter 1', content: html })
                    if (!cancelled) chapterDbIds.current[anchorId] = r.data.id
                } catch (e) { console.error('Failed to create initial chapter', e) }
                return
            }

            // Ensure every chapter's H1 has data-anchor-id before merging
            const processed = raw.map(ch => {
                const doc = new DOMParser().parseFromString(ch.content || '', 'text/html')
                let h1 = doc.body.querySelector('h1')
                let anchorId: string

                if (h1?.hasAttribute('data-anchor-id')) {
                    anchorId = h1.getAttribute('data-anchor-id')!
                } else {
                    anchorId = `chapter-${ch.id}`
                    if (!h1) {
                        h1 = doc.createElement('h1')
                        h1.textContent = ch.title
                        doc.body.insertBefore(h1, doc.body.firstChild)
                    }
                    h1.setAttribute('data-anchor-id', anchorId)
                }

                chapterDbIds.current[anchorId] = ch.id
                return { content: doc.body.innerHTML }
            })

            const merged = mergeChaptersToHtml(processed)
            editor.commands.setContent(merged)

            // Read AFTER setContent — editor has parsed the new HTML
            setChapters(readAnchorsFromEditor(editor))
        }).catch(console.error)

        return () => { cancelled = true }
    }, [id, editor])

    // ── Page size ─────────────────────────────────────────────────────────
    useEffect(() => {
        if (!editor) return
        editor.chain().focus().updatePageSize(PAGE_SIZES.A4).run()
    }, [editor])

    // ── Add chapter ───────────────────────────────────────────────────────
    const handleAddChapter = useCallback(() => {
        if (!editor) return
        const anchorId = `chapter-${Date.now()}`
        const num = readAnchorsFromEditor(editor).length + 1
        editor.chain().focus('end').insertContent(`<h1 data-anchor-id="${anchorId}">Chapter ${num}</h1><p></p>`).run()
        setTimeout(() => {
            document.querySelector(`[data-anchor-id="${anchorId}"]`)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }, 100)
    }, [editor])

    // ── Scroll to chapter ─────────────────────────────────────────────────
    const handleScrollTo = useCallback((anchorId: string) => {
        const el = document.querySelector(`[data-anchor-id="${anchorId}"]`)
        if (el) {
            el.scrollIntoView({ behavior: 'smooth', block: 'start' })
            setActiveAnchorId(anchorId)
        }
    }, [])

    // ── Rename chapter ────────────────────────────────────────────────────
    const handleRenameChapter = useCallback((anchorId: string, newTitle: string) => {
        if (!editor) return
        const { state, view } = editor
        state.doc.forEach((node: any, offset: number) => {
            if (node.type.name === 'heading' && node.attrs.level === 1 && node.attrs['data-anchor-id'] === anchorId) {
                const from = offset + 1
                const to = offset + node.nodeSize - 1
                const tr = state.tr.replaceWith(from, to, newTitle ? state.schema.text(newTitle) : [])
                view.dispatch(tr)
            }
        })
    }, [editor])

    // ── Delete chapter ────────────────────────────────────────────────────
    const handleDeleteChapter = useCallback(async (anchorId: string) => {
        if (!editor) return
        if (readAnchorsFromEditor(editor).length <= 1) return

        const { state, view } = editor
        let deleteFrom = -1
        let deleteTo = -1

        state.doc.forEach((node: any, offset: number) => {
            if (node.type.name === 'heading' && node.attrs.level === 1 && node.attrs['data-anchor-id'] === anchorId) {
                deleteFrom = offset
            } else if (deleteFrom !== -1 && deleteTo === -1 && node.type.name === 'heading' && node.attrs.level === 1) {
                deleteTo = offset
            }
        })

        if (deleteFrom !== -1) {
            view.dispatch(state.tr.delete(deleteFrom, deleteTo !== -1 ? deleteTo : state.doc.content.size))
        }

        const dbId = chapterDbIds.current[anchorId]
        if (dbId) {
            try {
                await apiClient.delete(`/chapters/${dbId}`)
                delete chapterDbIds.current[anchorId]
            } catch (e) { console.error('Failed to delete chapter', e) }
        }
    }, [editor])

    // ── Ctrl+Scroll zoom ──────────────────────────────────────────────────
    useEffect(() => {
        const handleWheel = (e: Event) => {
            const wheel = e as WheelEvent
            if (!wheel.ctrlKey) return
            wheel.preventDefault()
            setZoom(z => Math.min(200, Math.max(30, z + (wheel.deltaY > 0 ? -5 : 5))))
        }
        const center = document.querySelector('.editorCenter')
        center?.addEventListener('wheel', handleWheel, { passive: false })
        return () => center?.removeEventListener('wheel', handleWheel)
    }, [])

    // ── Zoom bar position ─────────────────────────────────────────────────
    useEffect(() => {
        const recalc = () => {
            if (!centerRef.current) return
            const rect = centerRef.current.getBoundingClientRect()
            setZoomBarLeft(`${rect.left + rect.width / 2}px`)
        }
        recalc()
        window.addEventListener('resize', recalc)
        return () => window.removeEventListener('resize', recalc)
    }, [showChapters, showRight])

    useEffect(() => {
        return () => {
            if (autosaveTimer.current) {
                clearTimeout(autosaveTimer.current);
            }
        };
    }, []);

    const ps = PARA_STYLES[paraStyle] || PARA_STYLES[0]
    const wordCount = editor?.storage.characterCount?.words() ?? 0
    const charCount = editor?.storage.characterCount?.characters() ?? 0
    const activeChapterTitle = chapters.find(c => c.anchorId === activeAnchorId)?.title ?? ''

    return (
        <div className={`editorPageWrap${focusMode ? ' focusMode' : ''}`}>
            {!focusMode && (
                <div className="editorTopBar">
                    <input
                        className="editorBookTitle"
                        value={bookTitle}
                        onChange={e => setBookTitle(e.target.value)}
                        onBlur={() => performSave()}
                        spellCheck={false}
                    />
                    <button className={`toolbarBtn panelToggleBtn${showChapters ? ' active' : ''}`} onClick={() => setShowChapters(v => !v)} title="Toggle chapters"><I.Chapters /></button>
                    <button className={`toolbarBtn panelToggleBtn${showRight ? ' active' : ''}`} onClick={() => setShowRight(v => !v)} title="Toggle right panel"><I.PanelRight /></button>

                    <Toolbar
                        editor={editor}
                        font={font}
                        fontSize={fontSize}
                        lineHeight={lineHeight}
                        paraStyle={paraStyle}
                        focusMode={focusMode}
                        setFontSize={setFontSize}
                        setLineHeight={setLineHeight}
                        setParaStyle={setParaStyle}
                        setFocusMode={setFocusMode}
                    />

                    <div className="topBarRight">
                        <ExportMenu editor={editor} bookTitle={bookTitle} fontSize={fontSize} lineHeight={lineHeight} />
                        <button
                            className={`editorSaveBtn${saveStatus === 'saved' ? ' saved' : ''}`}
                            onClick={() => performSave()}
                            disabled={saveStatus === 'saving'}
                        >
                            {saveStatus === 'saving' ? 'Saving…' : saveStatus === 'saved' ? <><I.Check /> Saved</> : 'Save'}
                        </button>
                    </div>
                </div>
            )}

            <div className="editorBody">
                {!focusMode && showChapters && (
                    <ChaptersSidebar
                        chapters={chapters}
                        activeAnchorId={activeAnchorId}
                        onScrollTo={handleScrollTo}
                        onAdd={handleAddChapter}
                        onRename={handleRenameChapter}
                        onDelete={handleDeleteChapter}
                    />
                )}

                <div className="editorCenter" ref={centerRef}>
                    {focusMode && (
                        <div className="focusModeBar">
                            <button className="focusExitBtn" onClick={() => setFocusMode(false)}>Exit focus mode</button>
                        </div>
                    )}
                    <div
                        className="editorPaper"
                        style={{
                            fontFamily: font,
                            fontSize: `${fontSize}px`,
                            lineHeight,
                            '--para-indent': ps.firstIndent,
                            '--para-spacing': ps.spacing,
                            transform: `scale(${zoom / 100})`,
                            transformOrigin: 'top center',
                            marginBottom: zoom < 100 ? `${(zoom / 100 - 1) * 100}%` : '0',
                        } as any}
                    >
                        <EditorContent editor={editor} className="tiptapEditor" />
                    </div>

                    <div className="zoomBar" style={{ left: zoomBarLeft }}>
                        <button className="zoomBtn" onClick={() => setZoom(z => Math.max(30, z - 10))}>−</button>
                        <input className="zoomSlider" type="range" min={30} max={200} step={5} value={zoom} onChange={e => setZoom(+e.target.value)} />
                        <button className="zoomBtn" onClick={() => setZoom(z => Math.min(200, z + 10))}>+</button>
                        <span className="zoomLabel">{zoom}%</span>
                        <button className="zoomBtn zoomReset" onClick={() => setZoom(100)} title="Reset">↺</button>
                    </div>
                </div>

                {!focusMode && showRight && <RightPanel wordCount={wordCount} charCount={charCount} />}
            </div>

            {!focusMode && (
                <div className="editorStatusBar">
                    <span>{wordCount} words · {charCount} chars</span>
                    <span>{activeChapterTitle}</span>
                    <span className={saveStatus === 'unsaved' ? 'unsaved' : ''}>
                        {saveStatus === 'saving' ? 'Saving…' : saveStatus === 'saved' ? 'Saved' : '● Unsaved changes'}
                    </span>
                </div>
            )}
        </div>
    )
}