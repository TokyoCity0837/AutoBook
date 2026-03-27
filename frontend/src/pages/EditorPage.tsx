import { useEditor, EditorContent } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import Placeholder from '@tiptap/extension-placeholder'
import { Extension } from '@tiptap/core'
import { TextStyleKit } from '@tiptap/extension-text-style'
import TextAlign from '@tiptap/extension-text-align'
import CharacterCount from '@tiptap/extension-character-count'
import { PaginationPlus, PAGE_SIZES } from 'tiptap-pagination-plus'
import { useState, useRef, useEffect } from 'react'
import { Document, Paragraph, TextRun, HeadingLevel, Packer, AlignmentType } from 'docx'
import { saveAs } from 'file-saver'
import { pdf, Document as PdfDocument, Page, Text, StyleSheet } from '@react-pdf/renderer'
import '../assets/styles/editorPage.css'
import { Update } from './BookPage'


//  Types 
interface SubChapter { id: number; title: string }
interface Chapter { id: number; title: string; sub: SubChapter[]; content: string }

//  Constants 
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

const A4 = {
    pageHeight: 1122, pageWidth: 794,
    pageGap: 40, pageGapBorderSize: 0,
    pageGapBorderColor: '#2a2e3a', pageBreakBackground: '#2a2e3a',
    marginTop: 76, marginBottom: 76, marginLeft: 68, marginRight: 68,
    contentMarginTop: 0, contentMarginBottom: 0,
    pageHeaderHeight: 28, pageFooterHeight: 0,
    headerRight: 'Page {page}', headerLeft: '', footerRight: '', footerLeft: '',
}

// Tab indent extension
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

// SVG Icons 
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

//  Export menu 
function ExportMenu({ editor, bookTitle, font, fontSize, lineHeight }: {
    editor: any; bookTitle: string; font: string; fontSize: number; lineHeight: string
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

        const htmlContent = editor.getHTML()
        const parser = new DOMParser()
        const doc = parser.parseFromString(htmlContent, 'text/html')
        const blocks = Array.from(doc.body.children)

        const fontSizePt = Math.round(fontSize * 0.75)

        const styles = StyleSheet.create({
            page: {
                paddingTop: 76,
                paddingBottom: 76,
                paddingLeft: 68,
                paddingRight: 68,
                fontFamily: 'Times-Roman',
                fontSize: fontSizePt,
                lineHeight: parseFloat(lineHeight),
            },
            h1: { fontSize: fontSizePt * 1.9, fontFamily: 'Times-Bold', marginBottom: 32 },
            h2: { fontSize: fontSizePt * 1.45, fontFamily: 'Times-Bold', marginBottom: 10 },
            h3: { fontSize: fontSizePt * 1.15, fontFamily: 'Times-Bold', marginBottom: 8 },
            p: { marginBottom: 0, textIndent: 0 },
            bold: { fontFamily: 'Times-Bold' },
            italic: { fontFamily: 'Times-Italic' },
        })

        const renderBlock = (block: Element, index: number) => {
            const tag = block.tagName.toLowerCase()
            const text = block.textContent ?? ''
            const el = block as HTMLElement

            const textAlign = el.style.textAlign ||
                el.getAttribute('data-text-align') ||
                'left'

            const alignMap: Record<string, any> = {
                left: 'left',
                center: 'center',
                right: 'right',
                justify: 'justify',
            }
            const align = alignMap[textAlign] ?? 'left'

            if (tag === 'h1') return (
                <Text key={index} style={{ ...styles.h1, textAlign: align }}>{text}</Text>
            )
            if (tag === 'h2') return (
                <Text key={index} style={{ ...styles.h2, textAlign: align }}>{text}</Text>
            )
            if (tag === 'h3') return (
                <Text key={index} style={{ ...styles.h3, textAlign: align }}>{text}</Text>
            )

            return (
                <Text key={index} style={{ ...styles.p, textAlign: align }}>{text}</Text>
            )
        }

        const MyDoc = (
            <PdfDocument>
                <Page size="A4" style={styles.page}>
                    {blocks.map((block, i) => renderBlock(block, i))}
                </Page>
            </PdfDocument>
        )

        try {
            const blob = await pdf(MyDoc).toBlob()
            saveAs(blob, `${bookTitle}.pdf`)
        } catch (err) {
            console.error('PDF error:', err)
            alert(`Помилка: ${err}`)
        }
    }



    const exportDOCX = async () => {
        setOpen(false)
        if (!editor) return

        const htmlContent = editor.getHTML()

        const parser = new DOMParser()
        const doc = parser.parseFromString(htmlContent, 'text/html')
        const blocks = Array.from(doc.body.children)

        const fontSizePt = Math.round(fontSize * 0.75)

        const docChildren = blocks.map(block => {
            const tag = block.tagName.toLowerCase()
            const text = block.textContent ?? ''

            const headingMap: Record<string, typeof HeadingLevel[keyof typeof HeadingLevel]> = {
                h1: HeadingLevel.HEADING_1,
                h2: HeadingLevel.HEADING_2,
                h3: HeadingLevel.HEADING_3,
            }
            if (headingMap[tag]) {
                return new Paragraph({
                    text,
                    heading: headingMap[tag],
                    spacing: { after: 200 },
                })
            }

            const runs: TextRun[] = []
            const inlineNodes = Array.from(block.childNodes)

            if (inlineNodes.length === 0) {
                runs.push(new TextRun({
                    text,
                    font: font.split(',')[0].replace(/"/g, '').trim(),
                    size: fontSizePt * 2,
                }))
            } else {
                inlineNodes.forEach(node => {
                    if (node.nodeType === Node.TEXT_NODE) {
                        runs.push(new TextRun({
                            text: node.textContent ?? '',
                            font: font.split(',')[0].replace(/"/g, '').trim(),
                            size: fontSizePt * 2,
                        }))
                    } else if (node.nodeType === Node.ELEMENT_NODE) {
                        const el = node as HTMLElement
                        const elTag = el.tagName.toLowerCase()
                        runs.push(new TextRun({
                            text: el.textContent ?? '',
                            bold: elTag === 'strong' || elTag === 'b',
                            italics: elTag === 'em' || elTag === 'i',
                            underline: elTag === 'u' ? {} : undefined,
                            strike: elTag === 's' || elTag === 'del',
                            font: font.split(',')[0].replace(/"/g, '').trim(),
                            size: fontSizePt * 2,
                        }))
                    }
                })
            }

            const alignMap = {
                left: AlignmentType.LEFT,
                center: AlignmentType.CENTER,
                right: AlignmentType.RIGHT,
                justify: AlignmentType.JUSTIFIED,
            } as const

            const textAlign = (block as HTMLElement).style.textAlign || 'left'

            return new Paragraph({
                children: runs,
                alignment: alignMap[textAlign as keyof typeof alignMap] ?? AlignmentType.LEFT,
                spacing: {
                    line: Math.round(parseFloat(lineHeight) * 240),
                    after: 0,
                },
            })
        })

        const wordDoc = new Document({
            sections: [{
                properties: {
                    page: {
                        size: { width: 11906, height: 16838 },
                        margin: { top: 1440, bottom: 1440, left: 1361, right: 1361 },
                    },
                },
                children: docChildren,
            }],
        })

        const blob = await Packer.toBlob(wordDoc)
        saveAs(blob, `${bookTitle}.docx`)
    }

    const exportTXT = () => {
        setOpen(false)
        const text = editor?.getText() ?? ''
        const blob = new Blob([text], { type: 'text/plain' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url; a.download = `${bookTitle}.txt`; a.click()
        URL.revokeObjectURL(url)
    }

    return (
        <div className="tbDropdown" ref={ref}>
            <button className="tbDropdownBtn exportBtn" onClick={() => setOpen(o => !o)} title="Export">
                <I.Export /><span className="exportLabel">Export</span><I.ChevronDown />
            </button>
            {open && (
                <div className="tbDropdownMenu exportMenu">
                    <div className="exportItem" onClick={exportPDF}>
                        <span className="exportItemIcon">📄</span>
                        <div>
                            <div className="exportItemTitle">PDF</div>
                        </div>
                    </div>
                    <div className="exportItem" onClick={exportDOCX}>
                        <span className="exportItemIcon">📝</span>
                        <div>
                            <div className="exportItemTitle">Word (.docx)</div>
                        </div>
                    </div>
                    <div className="exportItem" onClick={exportTXT}>
                        <span className="exportItemIcon">📃</span>
                        <div>
                            <div className="exportItemTitle">Plain text (.txt)</div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

//  Dropdown 
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
                        <div key={o.value} className="tbDropdownItem" onClick={() => { onSelect(o.value); setOpen(false) }}>
                            {o.label}
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}

//  Toolbar 
function Toolbar({ editor, font, fontSize, lineHeight, paraStyle, focusMode,
    setFont, setFontSize, setLineHeight, setParaStyle, setFocusMode }: {
        editor: any; font: string; fontSize: number; lineHeight: string; paraStyle: number; focusMode: boolean
        setFont: (v: string) => void; setFontSize: (v: number) => void; setLineHeight: (v: string) => void
        setParaStyle: (v: number) => void; setFocusMode: (v: boolean) => void
    }) {
    if (!editor) return null
    const D = () => <div className="toolbarDivider" />
    const Btn = ({ active = false, onClick, disabled = false, title, children }: any) => (
        <button className={`toolbarBtn${active ? ' active' : ''}`} onClick={onClick} disabled={disabled} title={title}>{children}</button>
    )
    return (
        <div className="editorToolbar">
            <Dropdown label={FONTS.find(f => f.value === font)?.label ?? 'Georgia'} options={FONTS}
                onSelect={v => { setFont(v); editor.chain().focus().setFontFamily(v).run() }} width={130} />
            <Dropdown label={`${fontSize}px`} options={SIZES.map(s => ({ label: `${s}px`, value: `${s}` }))}
                onSelect={v => setFontSize(+v)} width={68} />
            <Dropdown label={`↕ ${lineHeight}`} options={LINE_HEIGHTS} onSelect={setLineHeight} width={120} />
            <Dropdown label={`¶ ${PARA_STYLES[paraStyle].label}`}
                options={PARA_STYLES.map((p, i) => ({ label: p.label, value: `${i}` }))}
                onSelect={v => setParaStyle(+v)} width={120} />
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

//  Editable label 
function EditableLabel({ value, onChange, className }: { value: string; onChange: (v: string) => void; className?: string }) {
    const [editing, setEditing] = useState(false)
    const [draft, setDraft] = useState(value)
    const ref = useRef<HTMLInputElement>(null)
    useEffect(() => { if (editing) ref.current?.select() }, [editing])
    const commit = () => { onChange(draft.trim() || value); setEditing(false) }
    if (editing) return (
        <input ref={ref} className={`editableLabelInput ${className ?? ''}`} value={draft}
            onChange={e => setDraft(e.target.value)} onBlur={commit}
            onKeyDown={e => { if (e.key === 'Enter') commit(); if (e.key === 'Escape') { setDraft(value); setEditing(false) } }} />
    )
    return <span className={`editableLabel ${className ?? ''}`} onDoubleClick={() => { setDraft(value); setEditing(true) }}>{value}</span>
}

//  Chapters sidebar 
function ChaptersSidebar({ chapters, activeId, onSelect, onUpdate }: {
    chapters: Chapter[]; activeId: number; onSelect: (id: number) => void; onUpdate: (chs: Chapter[]) => void
}) {
    const [expanded, setExpanded] = useState<Set<number>>(new Set())

    const toggleExpand = (id: number) => {
        setExpanded(prev => {
            const next = new Set(prev)
            if (next.has(id)) next.delete(id)
            else next.add(id)
            return next
        })
    }

    const addChapter = () => {
        const id = Date.now()
        onUpdate([...chapters, { id, title: `Chapter ${chapters.length + 1}`, sub: [], content: '' }])
        onSelect(id)
    }
    const addSub = (chId: number) => {
        onUpdate(chapters.map(ch => ch.id === chId
            ? { ...ch, sub: [...ch.sub, { id: Date.now(), title: `${ch.title}.${ch.sub.length + 1}` }] } : ch))
        setExpanded(prev => new Set(prev).add(chId))
    }
    const renameChapter = (chId: number, t: string) => onUpdate(chapters.map(ch => ch.id === chId ? { ...ch, title: t } : ch))
    const renameSub = (chId: number, subId: number, t: string) => onUpdate(chapters.map(ch => ch.id === chId
        ? { ...ch, sub: ch.sub.map(s => s.id === subId ? { ...s, title: t } : s) } : ch))
    const deleteChapter = (chId: number) => {
        const rest = chapters.filter(ch => ch.id !== chId)
        onUpdate(rest)
        if (activeId === chId && rest.length > 0) onSelect(rest[0].id)
    }
    const deleteSub = (chId: number, subId: number) => onUpdate(chapters.map(ch => ch.id === chId
        ? { ...ch, sub: ch.sub.filter(s => s.id !== subId) } : ch))

    return (
        <div className="chaptersSidebar">
            <div className="chaptersTitle">Chapters</div>
            <div className="chaptersList">
                {chapters.map(ch => {
                    const isExpanded = expanded.has(ch.id)
                    return (
                        <div key={ch.id} className="chapterGroup">
                            <div className={`chapterItem${activeId === ch.id ? ' active' : ''}`}>
                                <div className="chapterItemLeft">
                                    <button
                                        className="chapterExpandBtn"
                                        onClick={() => toggleExpand(ch.id)}
                                        style={{ transform: isExpanded ? 'rotate(0deg)' : 'rotate(-90deg)', transition: 'transform 0.2s', background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', display: 'flex' }}
                                    >
                                        <I.ChevronDown />
                                    </button>
                                    <div onClick={() => onSelect(ch.id)} style={{ flex: 1 }}>
                                        <EditableLabel value={ch.title} onChange={t => renameChapter(ch.id, t)} />
                                    </div>
                                </div>
                                <div className="chapterActions">
                                    <button className="chapterActionBtn" title="Add subchapter" onClick={() => addSub(ch.id)}><I.Plus /></button>
                                    <button className="chapterActionBtn danger" title="Delete" onClick={() => deleteChapter(ch.id)}><I.Trash /></button>
                                </div>
                            </div>
                            {isExpanded && ch.sub.map(s => (
                                <div key={s.id} className="subChapterItem" onClick={() => onSelect(s.id)}>
                                    <div style={{ paddingLeft: '24px', flex: 1, color: activeId === s.id ? 'var(--text-primary)' : 'inherit' }}>
                                        <EditableLabel value={s.title} onChange={t => renameSub(ch.id, s.id, t)} className="subLabel" />
                                    </div>
                                    <button className="chapterActionBtn danger small" onClick={(e) => { e.stopPropagation(); deleteSub(ch.id, s.id); }}><I.Trash /></button>
                                </div>
                            ))}
                        </div>
                    )
                })}
                <button className="addChapterBtn" onClick={addChapter}><I.Plus /> Add chapter</button>
            </div>
        </div>
    )
}

//  Right panel 
function RightPanel({ wordCount, charCount }: { wordCount: number; charCount: number }) {
    const [tab, setTab] = useState<'vc' | 'edits'>('edits')
    const readingMinutes = Math.max(1, Math.round(wordCount / 200))
    return (
        <div className="editorRightPanel">
            <div className="rightPanelTabs">
                <button className={`rightPanelTab${tab === 'vc' ? ' active' : ''}`} onClick={() => setTab('vc')}>VC builder</button>
                <button className={`rightPanelTab${tab === 'edits' ? ' active' : ''}`} onClick={() => setTab('edits')}>Edits</button>
            </div>
            <div className="rightPanelContent">
                {tab === 'vc' ? (
                    <div className="vcStats">
                        <div className="vcStatItem"><span className="vcStatLabel">Words</span><span className="vcStatValue">{wordCount}</span></div>
                        <div className="vcStatItem"><span className="vcStatLabel">Characters</span><span className="vcStatValue">{charCount}</span></div>
                        <div className="vcStatItem"><span className="vcStatLabel">Reading time</span><span className="vcStatValue">~{readingMinutes} min</span></div>
                        <div className="vcStatItem"><span className="vcStatLabel">Last saved</span><span className="vcStatValue">Just now</span></div>
                        <div className="vcEmpty">Version history will appear here after first save</div>
                    </div>
                ) : (
                    <div className="editsList">
                        <Update text="Suggest changing opening paragraph for better hook" />
                        <Update text="Grammar fix in second sentence" />
                        <Update text="Consider rephrasing this section" />
                    </div>
                )}
            </div>
        </div>
    )
}

//  Main 
const INITIAL_CHAPTERS: Chapter[] = [
    { id: 1, title: 'Chapter 1', sub: [{ id: 11, title: 'Chapter 1.1' }, { id: 12, title: 'Chapter 1.2' }], content: '' },
    { id: 2, title: 'Chapter 2', sub: [{ id: 21, title: 'Chapter 2.1' }], content: '' },
]

export default function EditorPage() {
    const [chapters, setChapters] = useState<Chapter[]>(INITIAL_CHAPTERS)
    const [activeId, setActiveId] = useState(INITIAL_CHAPTERS[0].id)
    const [bookTitle, setBookTitle] = useState('Fourth wing')
    const [font, setFont] = useState('Georgia, serif')
    const [fontSize, setFontSize] = useState(15)
    const [lineHeight, setLineHeight] = useState('1.8')
    const [paraStyle, setParaStyle] = useState(0)
    const [focusMode, setFocusMode] = useState(false)
    const [showChapters, setShowChapters] = useState(false)
    const [showRight, setShowRight] = useState(false)
    const [zoom, setZoom] = useState(100)
    const [saved, setSaved] = useState(true)
    const contentRef = useRef<Record<number, string>>({})

    const activeChapter = chapters.find(c => c.id === activeId)
    const ps = PARA_STYLES[paraStyle]

    const editor = useEditor({
        extensions: [
            StarterKit,
            Placeholder.configure({ placeholder: 'Start writing your chapter…' }),
            TextStyleKit,
            TabIndent,
            TextAlign.configure({ types: ['heading', 'paragraph'] }),
            CharacterCount,
            PaginationPlus.configure(A4),
        ],
        content: '',
        onUpdate: ({ editor }) => {
            contentRef.current[activeId] = editor.getHTML()
            setSaved(false)
        },
    })

    const centerRef = useRef<HTMLDivElement>(null)
    const [zoomBarLeft, setZoomBarLeft] = useState('50%')

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
        if (!editor) return
        editor.chain().focus().updatePageSize(PAGE_SIZES.A4).run()
    }, [editor])

    useEffect(() => {
        if (!editor) return
        editor.commands.setContent(contentRef.current[activeId] ?? '', {})
    }, [activeId])

    useEffect(() => {
        const handleWheel = (e: Event) => {
            const wheel = e as WheelEvent

            if (!wheel.ctrlKey) return
            wheel.preventDefault()

            setZoom(z => {
                const delta = wheel.deltaY > 0 ? -5 : 5
                return Math.min(200, Math.max(30, z + delta))
            })
        }

        const center = document.querySelector('.editorCenter')
        center?.addEventListener('wheel', handleWheel, { passive: false })

        return () => center?.removeEventListener('wheel', handleWheel)
    }, [])

    const handleSave = () => {
        console.log('Save:', chapters.map(ch => ({ ...ch, content: contentRef.current[ch.id] ?? '' })))
        setSaved(true)
    }

    const wordCount = editor?.storage.characterCount?.words() ?? 0
    const charCount = editor?.storage.characterCount?.characters() ?? 0

    return (
        <div className={`editorPageWrap${focusMode ? ' focusMode' : ''}`}>
            {!focusMode && (
                <div className="editorTopBar">
                    <input
                        className="editorBookTitle"
                        value={bookTitle}
                        onChange={e => setBookTitle(e.target.value)}
                        spellCheck={false}
                    />
                    <button
                        className={`toolbarBtn panelToggleBtn${showChapters ? ' active' : ''}`}
                        onClick={() => setShowChapters(v => !v)}
                        title="Toggle chapters"
                    ><I.Chapters /></button>
                    <button
                        className={`toolbarBtn panelToggleBtn${showRight ? ' active' : ''}`}
                        onClick={() => setShowRight(v => !v)}
                        title="Toggle right panel"
                    ><I.PanelRight /></button>

                    <Toolbar editor={editor} font={font} fontSize={fontSize}
                        lineHeight={lineHeight} paraStyle={paraStyle} focusMode={focusMode}
                        setFont={setFont} setFontSize={setFontSize}
                        setLineHeight={setLineHeight} setParaStyle={setParaStyle}
                        setFocusMode={setFocusMode} />

                    <div className="topBarRight">
                        <ExportMenu editor={editor} bookTitle={bookTitle} font={font} fontSize={fontSize} lineHeight={lineHeight} />
                        <button className={`editorSaveBtn${saved ? ' saved' : ''}`} onClick={handleSave}>
                            {saved ? <><I.Check /> Saved</> : 'Save'}
                        </button>
                    </div>
                </div>
            )}

            <div className="editorBody">
                {!focusMode && showChapters && (
                    <ChaptersSidebar chapters={chapters} activeId={activeId}
                        onSelect={setActiveId} onUpdate={setChapters} />
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
                            marginBottom: zoom < 100
                                ? `${(zoom / 100 - 1) * 100}%`
                                : '0',
                        } as any}
                    >
                        <EditorContent editor={editor} className="tiptapEditor" />
                    </div>

                    <div className="zoomBar" style={{ left: zoomBarLeft }}>
                        <button className="zoomBtn" onClick={() => setZoom(z => Math.max(30, z - 10))}>−</button>
                        <input className="zoomSlider" type="range" min={30} max={200} step={5}
                            value={zoom} onChange={e => setZoom(+e.target.value)} />
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
                    <span>{activeChapter?.title}</span>
                    <span className={saved ? '' : 'unsaved'}>{saved ? 'Auto-saved' : '● Unsaved changes'}</span>
                </div>
            )}
        </div>
    )
}