import { useState } from 'react'
import '../assets/styles/pages.css'
import '../assets/styles/settingsPage.css'
import React from 'react'

import { IconUser, IconLock, IconEye, IconEyeOff, IconShield, IconTrash, IconCheck } from '../components/Icons';



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
    const [displayName, setDisplayName] = useState('Andrii Dosyn')
    const [bio, setBio] = useState('Passionate reader and writer.')

    const [currentPass, setCurrentPass] = useState('')
    const [newPass, setNewPass] = useState('')
    const [confirmPass, setConfirmPass] = useState('')
    const [showCurrent, setShowCurrent] = useState(false)
    const [showNew, setShowNew] = useState(false)

    const [privacy, setPrivacy] = useState<'PUBLIC' | 'PRIVATE'>('PUBLIC')

    const [toast, setToast] = useState(false)
    const showToast = () => { setToast(true); setTimeout(() => setToast(false), 2200) }

    const passMatch = confirmPass === '' || newPass === confirmPass
    const passReady = currentPass.length > 0 && newPass.length >= 8 && passMatch && confirmPass.length > 0

    const passStrength = newPass.length === 0 ? 0
        : newPass.length < 5 ? 1
            : newPass.length < 8 ? 2
                : 3

    return (
        <div className="settingsWrap">
            <SaveToast visible={toast} />

            <div className="settingsHeader">
                <div className="settingsTitle">Settings</div>
            </div>

            <div className="settingsGrid">

                <Section icon={<IconUser />} title="Profile">
                    <div className="settingsAvatarRow">
                        <div className="settingsAvatar" />
                        <div className="settingsAvatarActions">
                            <button className="settingsBtnSecondary">Change photo</button>
                            <button className="settingsBtnGhost">Remove</button>
                        </div>
                    </div>

                    <Field label="Display name">
                        <input className="settingsInput" value={displayName}
                            onChange={e => setDisplayName(e.target.value)} />
                    </Field>

                    <Field label="Username" hint="Cannot be changed">
                        <input className="settingsInput" value="@dosi#1837132" disabled />
                    </Field>

                    <Field label="Bio">
                        <textarea className="settingsTextarea" value={bio}
                            onChange={e => setBio(e.target.value)}
                            placeholder="Tell something about yourself..." rows={3} />
                        <span className="settingsCharCount">{bio.length} / 300</span>
                    </Field>

                    <button className="settingsBtnPrimary" onClick={showToast}>Save profile</button>
                </Section>

                <Section icon={<IconLock />} title="Password">
                    <Field label="Current password">
                        <div className="settingsInputWrap">
                            <input className="settingsInput" type={showCurrent ? 'text' : 'password'}
                                value={currentPass} onChange={e => setCurrentPass(e.target.value)}
                                placeholder="••••••••" />
                            <button className="settingsEye" onClick={() => setShowCurrent(s => !s)}>
                                {showCurrent ? <IconEyeOff /> : <IconEye />}
                            </button>
                        </div>
                    </Field>

                    <Field label="New password" hint="Min. 8 characters">
                        <div className="settingsInputWrap">
                            <input className="settingsInput" type={showNew ? 'text' : 'password'}
                                value={newPass} onChange={e => setNewPass(e.target.value)}
                                placeholder="New password" />
                            <button className="settingsEye" onClick={() => setShowNew(s => !s)}>
                                {showNew ? <IconEyeOff /> : <IconEye />}
                            </button>
                        </div>
                        {newPass.length > 0 && (
                            <div className="settingsPassStrength">
                                {[1, 2, 3].map(i => (
                                    <div key={i} className={`settingsPassBar${passStrength >= i ? ` s${passStrength}` : ''}`} />
                                ))}
                                <span className="settingsPassLabel">
                                    {passStrength === 1 ? 'Weak' : passStrength === 2 ? 'Fair' : 'Strong'}
                                </span>
                            </div>
                        )}
                    </Field>

                    <Field label="Confirm password">
                        <input className={`settingsInput${!passMatch ? ' error' : ''}`}
                            type={showNew ? 'text' : 'password'}
                            value={confirmPass} onChange={e => setConfirmPass(e.target.value)}
                            placeholder="Repeat new password" />
                        {!passMatch && <span className="settingsError">Passwords don't match</span>}
                    </Field>

                    <button className="settingsBtnPrimary" disabled={!passReady} onClick={showToast}>
                        Change password
                    </button>
                </Section>

                {/*  Privacy  */}
                <Section icon={<IconShield />} title="Privacy">
                    <Field label="Account visibility">
                        <div className="settingsSegment">
                            <button className={`settingsSegmentBtn${privacy === 'PUBLIC' ? ' active' : ''}`}
                                onClick={() => setPrivacy('PUBLIC')}>Public</button>
                            <button className={`settingsSegmentBtn${privacy === 'PRIVATE' ? ' active' : ''}`}
                                onClick={() => setPrivacy('PRIVATE')}>Private</button>
                        </div>
                        <p className="settingsHintText">
                            {privacy === 'PUBLIC'
                                ? 'Anyone can find and view your profile.'
                                : 'Only approved followers can see your content.'}
                        </p>
                    </Field>


                    <button className="settingsBtnPrimary" onClick={showToast}>Save privacy</button>
                </Section>

                {/*  Danger  */}
                <Section icon={<IconTrash />} title="Danger zone">
                    <div className="settingsDangerBlock">
                        <div>
                            <div className="settingsDangerTitle">Delete account</div>
                            <div className="settingsDangerDesc">
                                Permanently removes your account and all data. Cannot be undone.
                            </div>
                        </div>
                        <button className="settingsBtnDanger">Delete account</button>
                    </div>
                </Section>

            </div>
        </div>
    )
}