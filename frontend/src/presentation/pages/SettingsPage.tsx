import React, { useState, useEffect, useRef, useCallback } from 'react'
import { useUser } from '../../shared/contexts/UserContext'
import { userRepository, storageRepository } from '../../data/repositories'
import { MEDIA_BASE_URL } from '../../shared/constants/config'
import { IconUser, IconShield, IconTrash, IconCheck } from '../components/ui/Icons'
import '../../assets/styles/pages.css'
import '../../assets/styles/settingsPage.css'
import { DefaultAvatar } from '../components/user/UserInfoForPost'

function AvatarCropModal({ imageUrl, onCrop, onClose }: { imageUrl: string, onCrop: (blob: Blob) => void, onClose: () => void }) {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const imgRef = useRef<HTMLImageElement>(null);
    const [scale, setScale] = useState(1);
    const [offset, setOffset] = useState({ x: 0, y: 0 });
    const [dragging, setDragging] = useState(false);
    const [dragStart, setDragStart] = useState({ x: 0, y: 0 });
    const [imgLoaded, setImgLoaded] = useState(false);
    
    const CROP_SIZE = 280;

    const draw = useCallback(() => {
        const canvas = canvasRef.current; const img = imgRef.current;
        if (!canvas || !img || !imgLoaded) return;
        const ctx = canvas.getContext('2d'); if (!ctx) return;
        canvas.width = CROP_SIZE; canvas.height = CROP_SIZE;
        ctx.clearRect(0, 0, CROP_SIZE, CROP_SIZE);
        ctx.fillStyle = '#1a1a2e'; ctx.fillRect(0, 0, CROP_SIZE, CROP_SIZE);
        ctx.save();
        ctx.beginPath(); ctx.arc(CROP_SIZE/2, CROP_SIZE/2, CROP_SIZE/2, 0, Math.PI*2); ctx.clip();
        const imgAspect = img.naturalWidth / img.naturalHeight;
        let drawW: number, drawH: number;
        if (imgAspect > 1) { drawH = CROP_SIZE * scale; drawW = drawH * imgAspect; }
        else { drawW = CROP_SIZE * scale; drawH = drawW / imgAspect; }
        ctx.drawImage(img, (CROP_SIZE - drawW)/2 + offset.x, (CROP_SIZE - drawH)/2 + offset.y, drawW, drawH);
        ctx.restore();
        ctx.beginPath(); ctx.arc(CROP_SIZE/2, CROP_SIZE/2, CROP_SIZE/2-2, 0, Math.PI*2);
        ctx.strokeStyle = 'rgba(255,255,255,0.3)'; ctx.lineWidth = 2; ctx.stroke();
    }, [imgLoaded, scale, offset]);
    useEffect(() => { draw(); }, [draw]);

    const handleMouseDown = (e: React.MouseEvent) => { setDragging(true); setDragStart({ x: e.clientX - offset.x, y: e.clientY - offset.y }); };
    const handleMouseMove = (e: React.MouseEvent) => { if (!dragging) return; setOffset({ x: e.clientX - dragStart.x, y: e.clientY - dragStart.y }); };
    const handleMouseUp = () => setDragging(false);
    const handleCrop = () => { canvasRef.current?.toBlob(blob => { if (blob) onCrop(blob); }, 'image/jpeg', 0.92); };

    return (
        <div className="cropOverlay" onClick={onClose}>
            <div className="cropModal" onClick={e => e.stopPropagation()}>
                <div className="cropTitle">Crop profileMe photo</div>
                <div className="cropCanvasWrap" onMouseDown={handleMouseDown} onMouseMove={handleMouseMove} onMouseUp={handleMouseUp} onMouseLeave={handleMouseUp} style={{ cursor: dragging ? 'grabbing' : 'grab' }}>
                    <canvas ref={canvasRef} width={CROP_SIZE} height={CROP_SIZE} style={{ borderRadius: '50%' }} />
                </div>
                <img ref={imgRef} src={imageUrl} style={{ display: 'none' }} onLoad={() => setImgLoaded(true)} crossOrigin="anonymous" />
                <div className="cropControls">
                    <label className="cropLabel">Zoom</label>
                    <input type="range" min="0.5" max="3" step="0.05" value={scale} onChange={e => setScale(parseFloat(e.target.value))} className="cropSlider" />
                </div>
                <div className="cropActions">
                    <button className="settingsBtnSecondary" onClick={onClose}>Cancel</button>
                    <button className="settingsBtnPrimary" onClick={handleCrop}>Save photo</button>
                </div>
            </div>
        </div>
    );
}

function Section({ icon, title, children }: { icon: React.ReactNode; title: string; children: React.ReactNode }) {
    return (<div className="settingsSection"><div className="settingsSectionHead"><span className="settingsSectionIcon">{icon}</span><span className="settingsSectionTitle">{title}</span></div><div className="settingsSectionBody">{children}</div></div>);
}
function Field({ label, hint, children }: { label: string; hint?: string; children: React.ReactNode }) {
    return (<div className="settingsField"><div className="settingsFieldLabel"><span>{label}</span>{hint && <span className="settingsFieldHint">{hint}</span>}</div><div className="settingsFieldControl">{children}</div></div>);
}
function SaveToast({ visible }: { visible: boolean }) {
    return <div className={`settingsSaveToast${visible ? ' visible' : ''}`}><IconCheck /> Saved</div>;
}

export default function SettingsPage() {
    const { profileMe, loading: userLoading, refreshProfile } = useUser();
    const [displayName, setDisplayName] = useState('');
    const [bio, setBio] = useState('');
    const [profileImage, setProfileImage] = useState<string | null>(null);
    const [privacy, setPrivacy] = useState<'PUBLIC' | 'PRIVATE'>('PUBLIC');
    const [toast, setToast] = useState(false);
    const [saving, setSaving] = useState(false);
    const [showCrop, setShowCrop] = useState(false);
    const [cropImageUrl, setCropImageUrl] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [username, setUsername] = useState('');
    const [usernameAvailable, setUsernameAvailable] = useState<boolean | null>(null);
    const [usernameChecking, setUsernameChecking] = useState(false);

    useEffect(() => {
        if (!profileMe) return;
        setUsername(profileMe.username || '');
        setDisplayName(profileMe.visibleName || '');
        setBio(profileMe.bio || '');
        setProfileImage(profileMe.profileImage || null);
        setPrivacy(profileMe.privacy || 'PUBLIC');
    }, [profileMe]);

    useEffect(() => {
        if (!username || username === profileMe?.username) {
            setUsernameAvailable(null);
            return;
        }
        setUsernameChecking(true);
        const timer = setTimeout(async () => {
            try {
                const results = await userRepository.search(username);
                const taken = results.some((u: any) => u.username === username);
                setUsernameAvailable(!taken);
            } catch {
                setUsernameAvailable(null);
            } finally {
                setUsernameChecking(false);
            }
        }, 500);
        return () => clearTimeout(timer);
    }, [username, profileMe?.username]);

    const userId = profileMe?.id ?? null;
    // const username = profileMe?.username ? `@${profileMe.username}` : '@username';
    const showSaved = () => { setToast(true); setTimeout(() => setToast(false), 2200); };

    const handleAvatarFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files?.[0]) { setCropImageUrl(URL.createObjectURL(e.target.files[0])); setShowCrop(true); }
    };

    const handleCropDone = async (blob: Blob) => {
        setShowCrop(false); if (!userId) return;
        setSaving(true);
        try {
            const imageUrl = await storageRepository.upload(blob, 'avatar.jpg');
            await userRepository.update(userId, { profileImage: imageUrl });
            setProfileImage(imageUrl);
            await refreshProfile();
            showSaved();
        } catch (err) { console.error("Failed to upload avatar", err); }
        finally { setSaving(false); }
    };

    const handleRemoveAvatar = async () => {
        if (!userId) return; setSaving(true);
        try { await userRepository.update(userId, { profileImage: '' }); setProfileImage(null); await refreshProfile(); showSaved(); }
        catch (err) { console.error(err); } finally { setSaving(false); }
    };

    const handleSaveProfile = async () => {
        if (!userId) return;
        setSaving(true);
        try {
            await userRepository.update(userId, {
                visibleName: displayName,
                bio,
                privacyType: privacy,
                username: username !== profileMe?.username ? username : undefined,
            });
            await refreshProfile();
            showSaved();
        } catch (err: any) {
            if (err.response?.status === 409) alert('Username is already taken');
            console.error(err);
        } finally {
            setSaving(false);
        }
    };
    

    const avatarUrl = profileImage ? `${MEDIA_BASE_URL}${profileImage}` : null;
    if (userLoading) return <div className="settingsWrap">Loading...</div>;
    if (!profileMe) return <div className="settingsWrap">Not authorized</div>;


    const inputStyle = username !== profileMe?.username
    ? {
        borderColor: usernameChecking ? 'rgba(255,255,255,0.2)'
            : usernameAvailable === true ? '#4ade80'
            : usernameAvailable === false ? '#f87171' : undefined
      }
    : {};

    return (
        <div className="settingsWrap">
            <SaveToast visible={toast} />
            {showCrop && cropImageUrl && <AvatarCropModal imageUrl={cropImageUrl} onCrop={handleCropDone} onClose={() => setShowCrop(false)} />}
            <div className="settingsHeader"><div className="settingsTitle">Settings</div></div>
            <div className="settingsGrid">
                <Section icon={<IconUser />} title="Profile">
                    <div className="settingsAvatarRow">
                        <div className="settingsAvatar">
                            {avatarUrl ? (
                                <div
                                    className="profileImage"
                                    style={{
                                        backgroundImage: `url(${avatarUrl})`,
                                        backgroundSize: 'cover',
                                        backgroundPosition: 'center'
                                    }}
                                />
                            ) : (
                                <DefaultAvatar name={profileMe?.visibleName ? profileMe?.visibleName : ''} size={64} />
                            )}
                        </div>
                        <div className="settingsAvatarActions">
                            <button className="settingsBtnSecondary" onClick={() => fileInputRef.current?.click()} disabled={saving}>Change photo</button>
                            <button className="settingsBtnGhost" onClick={handleRemoveAvatar} disabled={saving}>Remove</button>
                            <input type="file" accept="image/*" style={{ display: 'none' }} ref={fileInputRef} onChange={handleAvatarFileChange} />
                        </div>
                    </div>
                    <Field label="Display name"><input className="settingsInput" value={displayName} onChange={e => setDisplayName(e.target.value)} /></Field>
                    <Field label="Username" hint="Unique identifier">
                        <div style={{ position: 'relative' }}>
                            <input
                                className="settingsInput"
                                value={username}
                                onChange={e => setUsername(e.target.value.toLowerCase().replace(/[^a-z0-9_]/g, ''))}
                                placeholder="username"
                                style={inputStyle}
                            />
                            {username !== profileMe?.username && (
                                <span style={{
                                    position: 'absolute', right: '12px', top: '50%',
                                    transform: 'translateY(-50%)', fontSize: '13px',
                                    color: usernameChecking ? 'rgba(255,255,255,0.4)'
                                        : usernameAvailable === true ? '#4ade80'
                                        : usernameAvailable === false ? '#f87171' : 'transparent'
                                }}>
                                    {usernameChecking ? '...'
                                        : usernameAvailable === true ? 'Available'
                                        : usernameAvailable === false ? 'Taken' : ''}
                                </span>
                            )}
                        </div>
                        <span className="settingsFieldHint" style={{ marginTop: '4px', display: 'block' }}>
                            Only letters, numbers and underscores
                        </span>
                    </Field>
                    <Field label="Bio">
                        <textarea className="settingsTextarea" value={bio} onChange={e => setBio(e.target.value)} placeholder="Tell something about yourself..." rows={3} />
                        <span className="settingsCharCount">{bio.length} / 300</span>
                    </Field>
                    <button
                        className="settingsBtnPrimary"
                        onClick={handleSaveProfile}
                        disabled={saving || usernameAvailable === false}>
                        Save
                    </button>
                </Section>

                <Section icon={<IconShield />} title="Privacy">
                    <Field label="Account visibility">
                        <div className="settingsSegment">
                            <button className={`settingsSegmentBtn${privacy === 'PUBLIC' ? ' active' : ''}`} onClick={() => setPrivacy('PUBLIC')}>Public</button>
                            <button className={`settingsSegmentBtn${privacy === 'PRIVATE' ? ' active' : ''}`} onClick={() => setPrivacy('PRIVATE')}>Private</button>
                        </div>
                    </Field>
                    <button className="settingsBtnPrimary" onClick={handleSaveProfile} disabled={saving}>Save privacy</button>
                </Section>

                <Section icon={<IconTrash />} title="Danger zone">
                    <div className="settingsDangerBlock">
                        <div>
                            <div className="settingsDangerTitle">Delete account</div>
                            <div className="settingsDangerDesc">Permanently removes your account and all data.</div>
                        </div>
                        <button className="settingsBtnDanger">Delete account</button>
                    </div>
                </Section>
            </div>
        </div>
    );
}
