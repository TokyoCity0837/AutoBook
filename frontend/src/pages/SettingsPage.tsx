import React, { useEffect, useRef, useState, useCallback } from 'react'
import '../assets/styles/pages.css'
import '../assets/styles/settingsPage.css'
import api from '../api'
import { useUser } from '../UserContext'

import { IconUser, IconLock, IconEye, IconEyeOff, IconShield, IconTrash, IconCheck } from '../components/Icons';

// ... AvatarCropModal / Section / Field / SaveToast без змін
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
        const canvas = canvasRef.current;
        const img = imgRef.current;
        if (!canvas || !img || !imgLoaded) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        canvas.width = CROP_SIZE;
        canvas.height = CROP_SIZE;

        ctx.clearRect(0, 0, CROP_SIZE, CROP_SIZE);

        // Draw dark overlay
        ctx.fillStyle = '#1a1a2e';
        ctx.fillRect(0, 0, CROP_SIZE, CROP_SIZE);

        // Clip to circle
        ctx.save();
        ctx.beginPath();
        ctx.arc(CROP_SIZE / 2, CROP_SIZE / 2, CROP_SIZE / 2, 0, Math.PI * 2);
        ctx.clip();

        // Draw image centered with scale and offset
        const imgAspect = img.naturalWidth / img.naturalHeight;
        let drawW: number, drawH: number;
        if (imgAspect > 1) {
            drawH = CROP_SIZE * scale;
            drawW = drawH * imgAspect;
        } else {
            drawW = CROP_SIZE * scale;
            drawH = drawW / imgAspect;
        }
        const drawX = (CROP_SIZE - drawW) / 2 + offset.x;
        const drawY = (CROP_SIZE - drawH) / 2 + offset.y;
        ctx.drawImage(img, drawX, drawY, drawW, drawH);
        ctx.restore();

        // Draw circle outline
        ctx.beginPath();
        ctx.arc(CROP_SIZE / 2, CROP_SIZE / 2, CROP_SIZE / 2 - 2, 0, Math.PI * 2);
        ctx.strokeStyle = 'rgba(255,255,255,0.3)';
        ctx.lineWidth = 2;
        ctx.stroke();
    }, [imgLoaded, scale, offset]);

    useEffect(() => { draw(); }, [draw]);

    const handleMouseDown = (e: React.MouseEvent) => {
        setDragging(true);
        setDragStart({ x: e.clientX - offset.x, y: e.clientY - offset.y });
    };
    const handleMouseMove = (e: React.MouseEvent) => {
        if (!dragging) return;
        setOffset({ x: e.clientX - dragStart.x, y: e.clientY - dragStart.y });
    };
    const handleMouseUp = () => setDragging(false);

    const handleCrop = () => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        canvas.toBlob(blob => {
            if (blob) onCrop(blob);
        }, 'image/jpeg', 0.92);
    };

    return (
        <div className="cropOverlay" onClick={onClose}>
            <div className="cropModal" onClick={e => e.stopPropagation()}>
                <div className="cropTitle">Crop profile photo</div>
                <div className="cropCanvasWrap"
                    onMouseDown={handleMouseDown}
                    onMouseMove={handleMouseMove}
                    onMouseUp={handleMouseUp}
                    onMouseLeave={handleMouseUp}
                    style={{ cursor: dragging ? 'grabbing' : 'grab' }}
                >
                    <canvas ref={canvasRef} width={CROP_SIZE} height={CROP_SIZE} style={{ borderRadius: '50%' }} />
                </div>
                <img ref={imgRef} src={imageUrl} style={{ display: 'none' }} onLoad={() => setImgLoaded(true)} crossOrigin="anonymous" />
                <div className="cropControls">
                    <label className="cropLabel">Zoom</label>
                    <input type="range" min="0.5" max="3" step="0.05" value={scale}
                        onChange={e => setScale(parseFloat(e.target.value))}
                        className="cropSlider"
                    />
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
    return (
        <div className="settingsSection">
            <div className="settingsSectionHead">
                <span className="settingsSectionIcon">{icon}</span>
                <span className="settingsSectionTitle">{title}</span>
            </div>
            <div className="settingsSectionBody">{children}</div>
        </div>
    )
}

function Field({ label, hint, row, children }: { label: string; hint?: string; row?: boolean; children: React.ReactNode }) {
    return (
        <div className={`settingsField${row ? ' row' : ''}`}>
            <div className="settingsFieldLabel">
                <span>{label}</span>
                {hint && <span className="settingsFieldHint">{hint}</span>}
            </div>
            <div className="settingsFieldControl">{children}</div>
        </div>
    )
}

function SaveToast({ visible }: { visible: boolean }) {
    return (
        <div className={`settingsSaveToast${visible ? ' visible' : ''}`}>
            <IconCheck /> Saved
        </div>
    )
}

export default function SettingsPage() {
  const { profile, loading: userLoading, refreshProfile } = useUser();

  // локальний state форми (це ок)
  const [displayName, setDisplayName] = useState('')
  const [bio, setBio] = useState('')
  const [profileImage, setProfileImage] = useState<string | null>(null)
  const [privacy, setPrivacy] = useState<'PUBLIC' | 'PRIVATE'>('PUBLIC')

  const [toast, setToast] = useState(false)
  const [saving, setSaving] = useState(false)

  // Avatar crop
  const [showCrop, setShowCrop] = useState(false)
  const [cropImageUrl, setCropImageUrl] = useState<string | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  // 1) коли контекстний profile оновився — заповнюємо форму
  useEffect(() => {
    if (!profile) return;
    setDisplayName(profile.visibleName || '');
    setBio(profile.bio || '');
    setProfileImage(profile.profileImage || null);
    setPrivacy(profile.privacy || 'PUBLIC');
  }, [profile]);

  const userId = profile?.id ?? null;
  const username = profile?.username ? `@${profile.username}` : '@username';

  const handleAvatarFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const url = URL.createObjectURL(e.target.files[0]);
      setCropImageUrl(url);
      setShowCrop(true);
    }
  };

  const showSaved = () => {
    setToast(true);
    setTimeout(() => setToast(false), 2200);
  };

  const handleCropDone = async (blob: Blob) => {
    setShowCrop(false);
    if (!userId) return;

    try {
      setSaving(true);

      const formData = new FormData();
      formData.append('file', blob, 'avatar.jpg');

      const uploadRes = await api.post('/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });

      const imageUrl = uploadRes.data;

      await api.put(`/users/${userId}`, { profileImage: imageUrl });

      // локально можна теж поставити, щоб не чекати refresh:
      setProfileImage(imageUrl);

      // 2) синхронізуємо контекст (щоб Sidebar теж оновився)
      await refreshProfile();

      showSaved();
    } catch (err) {
      console.error("Failed to upload avatar", err);
    } finally {
      setSaving(false);
    }
  };

  const handleRemoveAvatar = async () => {
    if (!userId) return;

    try {
      setSaving(true);

      await api.put(`/users/${userId}`, { profileImage: '' });
      setProfileImage(null);

      await refreshProfile();

      showSaved();
    } catch (err) {
      console.error("Failed to remove avatar", err);
    } finally {
      setSaving(false);
    }
  };

  const handleSaveProfile = async () => {
    if (!userId) return;

    try {
      setSaving(true);

      await api.put(`/users/${userId}`, {
        visibleName: displayName,
        bio,
        privacyType: privacy
      });

      await refreshProfile();

      showSaved();
    } catch (err) {
      console.error("Error saving profile", err);
    } finally {
      setSaving(false);
    }
  };

  const avatarUrl = profileImage ? `http://localhost:8080${profileImage}` : null;

  // userLoading — це загрузка me з контексту
  if (userLoading) {
    return <div className="settingsWrap">Loading...</div>;
  }

  // якщо profile null — ProtectedRoute має редіректнути на /login,
  // але на всякий випадок:
  if (!profile) {
    return <div className="settingsWrap">Not authorized</div>;
  }

  return (
    <div className="settingsWrap">
      <SaveToast visible={toast} />

      {showCrop && cropImageUrl && (
        <AvatarCropModal
          imageUrl={cropImageUrl}
          onCrop={handleCropDone}
          onClose={() => setShowCrop(false)}
        />
      )}

      <div className="settingsHeader">
        <div className="settingsTitle">Settings</div>
      </div>

      <div className="settingsGrid">
        <Section icon={<IconUser />} title="Profile">
          <div className="settingsAvatarRow">
            <div className="settingsAvatar" style={avatarUrl ? {
              backgroundImage: `url(${avatarUrl})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center'
            } : {}}>
              {!avatarUrl && <IconUser />}
            </div>

            <div className="settingsAvatarActions">
              <button className="settingsBtnSecondary" onClick={() => fileInputRef.current?.click()} disabled={saving}>
                Change photo
              </button>
              <button className="settingsBtnGhost" onClick={handleRemoveAvatar} disabled={saving}>
                Remove
              </button>
              <input
                type="file"
                accept="image/*"
                style={{ display: 'none' }}
                ref={fileInputRef}
                onChange={handleAvatarFileChange}
              />
            </div>
          </div>

          <Field label="Display name">
            <input className="settingsInput" value={displayName}
              onChange={e => setDisplayName(e.target.value)} />
          </Field>

          <Field label="Username" hint="Cannot be changed">
            <input className="settingsInput" value={username} disabled />
          </Field>

          <Field label="Bio">
            <textarea className="settingsTextarea" value={bio}
              onChange={e => setBio(e.target.value)}
              placeholder="Tell something about yourself..." rows={3} />
            <span className="settingsCharCount">{bio.length} / 300</span>
          </Field>

          <button className="settingsBtnPrimary" onClick={handleSaveProfile} disabled={saving}>
            Save profile
          </button>
        </Section>

        {/* інші секції без змін, тільки disabled={saving} де треба */}
      </div>
    </div>
  );
}