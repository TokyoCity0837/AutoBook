import React, { useState, useRef } from 'react';
import { postRepository, storageRepository } from '../../../data/repositories';
import { userRepository } from '../../../data/repositories';
import { MEDIA_BASE_URL } from '../../../shared/constants/config';
import { useUser } from '../../../shared/contexts/UserContext';
import '../../../assets/styles/Posts.css';

function IconAddPhoto() {
    return (
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M7 6H17C18.1046 6 19 6.89543 19 8V16C19 17.1046 18.1046 18 17 18H7C5.89543 18 5 17.1046 5 16V8C5 6.89543 5.89543 6 7 6Z" stroke="rgba(255,255,255,0.7)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            <path d="M5 13L9 9L15 15L19 11" stroke="rgba(255,255,255,0.7)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            <circle cx="9.5" cy="10.5" r="1.5" fill="rgba(255,255,255,0.7)" />
        </svg>
    );
}

interface CreationPostProps {
    onPostCreated?: () => void;
}

export function CreationPost({ onPostCreated }: CreationPostProps) {
    const [isExpanded, setIsExpanded] = useState(false);
    const [textValue, setTextValue] = useState('');
    const [loading, setLoading] = useState(false);
    const [authorName, setAuthorName] = useState('...');
    const [uploadedImageUrl, setUploadedImageUrl] = useState<string | null>(null);
    const [uploadingImage, setUploadingImage] = useState(false);
    const fileInputRef = useRef<HTMLInputElement>(null);
    const { profileMe } = useUser();

    const avatarUrl = profileMe?.profileImage ? `${MEDIA_BASE_URL}${profileMe.profileImage}` : null;

    React.useEffect(() => {
        userRepository.getMe()
            .then(u => setAuthorName(u.visibleName))
            .catch(() => setAuthorName('Unknown'));
    }, []);

    const handlePublish = async () => {
        if (!textValue.trim() && !uploadedImageUrl) return;
        setLoading(true);
        try {
            await postRepository.create({
                content: textValue || ' ',
                postType: 'FEED',
                imageUrl: uploadedImageUrl
            });
            setTextValue('');
            setUploadedImageUrl(null);
            setIsExpanded(false);
            if (onPostCreated) onPostCreated();
        } catch (err) {
            console.error("Error creating post", err);
            alert("Failed to publish post.");
        } finally {
            setLoading(false);
        }
    };

    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            setUploadingImage(true);
            setIsExpanded(true);
            try {
                const url = await storageRepository.upload(e.target.files[0]);
                setUploadedImageUrl(url);
            } catch (err) {
                console.error("Image upload failed", err);
                alert("Failed to upload image.");
            } finally {
                setUploadingImage(false);
            }
        }
    };

    return (
        <div
            className={`NewPost ${isExpanded ? 'expanded' : ''}`}
            tabIndex={-1}
            onBlur={(e) => {
                if (!e.currentTarget.contains(e.relatedTarget)) {
                    if (textValue.trim() === '') setIsExpanded(false);
                }
            }}
        >
            <div className="newPostTop">
                <div
                className="ProfileImage"
                style={avatarUrl ? {
                    backgroundImage: `url(${avatarUrl})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center'
                } : {}}/>
                <div className="Nickname">{authorName}</div>
            </div>
            <textarea
                value={textValue}
                onChange={e => setTextValue(e.target.value)}
                maxLength={500}
                placeholder="What's on your mind?"
                className="addPostBar"
                onFocus={() => setIsExpanded(true)}
            />
            {(isExpanded || uploadedImageUrl || uploadingImage) && (
                <div style={{ display: 'flex', flexDirection: 'column', width: '100%', gap: '10px' }}>
                    {uploadingImage && <div style={{ color: '#ff7700', fontSize: '13px', padding: '10px' }}>Uploading Image...</div>}
                    {uploadedImageUrl && (
                        <div style={{ position: 'relative', width: '120px', borderRadius: '8px', overflow: 'hidden' }}>
                            <img src={`${MEDIA_BASE_URL}${uploadedImageUrl}`} alt="preview" style={{ width: '100%', display: 'block' }} />
                            <button
                                onClick={() => setUploadedImageUrl(null)}
                                style={{ position: 'absolute', top: 5, right: 5, background: 'rgba(0,0,0,0.5)', color: 'white', border: 'none', borderRadius: '50%', cursor: 'pointer', width: '24px', height: '24px' }}>
                                ×
                            </button>
                        </div>
                    )}
                    <div className="newPostActions" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                            <button className="iconActionBtn" onClick={() => fileInputRef.current?.click()} disabled={uploadingImage}>
                                <IconAddPhoto />
                            </button>
                            <input type="file" accept="image/*" style={{ display: 'none' }} ref={fileInputRef} onChange={handleFileChange} />
                        </div>
                        <button className="publishBtn" onClick={handlePublish} disabled={loading || uploadingImage}>
                            {loading ? 'Publishing...' : 'Publish'}
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
