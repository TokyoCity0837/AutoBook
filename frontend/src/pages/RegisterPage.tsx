import { useState } from 'react'
import { Link } from 'react-router-dom'
import '../assets/styles/authPage.css'
import React from 'react'

function GoogleIcon() {
    return (
        <svg width="20" height="20" viewBox="0 0 24 24">
            <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
            <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
            <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05" />
            <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
        </svg>
    )
}

function FacebookIcon() {
    return (
        <svg width="20" height="20" viewBox="0 0 24 24" fill="#1877F2">
            <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
        </svg>
    )
}

function StrengthBar({ password }: { password: string }) {
    // pass check in front, and after in backend
    const getStrength = () => {
        let score = 0
        if (password.length >= 8) score++
        if (/[A-Z]/.test(password)) score++
        if (/[0-9]/.test(password)) score++
        if (/[^A-Za-z0-9]/.test(password)) score++
        return score
    }
    const strength = getStrength()
    const labels = ['', 'Weak', 'Fair', 'Good', 'Strong']
    const colors = ['', '#ef4444', '#f97316', '#eab308', '#22c55e']

    if (!password) return null
    return (
        <div className="strengthWrap">
            <div className="strengthBars">
                {[1, 2, 3, 4].map(i => (
                    <div key={i} className="strengthBar"
                        style={{ background: i <= strength ? colors[strength] : 'rgba(255,255,255,0.1)' }} />
                ))}
            </div>
            <span className="strengthLabel" style={{ color: colors[strength] }}>{labels[strength]}</span>
        </div>
    )
}

export default function RegisterPage() {
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirm, setConfirm] = useState('')
    const [showPass, setShowPass] = useState(false)
    const [agree, setAgree] = useState(false)
    const [loading, setLoading] = useState(false)

    const passwordMatch = confirm === '' || password === confirm

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!passwordMatch || !agree) return
        setLoading(true)
        // POST /api/register  { username, email, password }
        setTimeout(() => setLoading(false), 1200)
    }

    const EyeBtn = ({ show, toggle }: { show: boolean; toggle: () => void }) => (
        <button type="button" className="authEye" onClick={toggle}>
            {show
                ? <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /><line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>
                : <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /><circle cx="12" cy="12" r="3" stroke="currentColor" strokeWidth="2" /></svg>
            }
        </button>
    )

    return (
        <div className="authWrap">
            <div className="authBg" />

            <div className="authCard">
                <div className="authLogo">AutoBook</div>
                <h1 className="authTitle">Create account</h1>
                <p className="authSub">Join the writing community</p>

                <form className="authForm" onSubmit={handleSubmit}>
                    <div className="authField">
                        <label className="authLabel">Username</label>
                        <input className="authInput" type="text" placeholder="your_nickname"
                            value={username} onChange={e => setUsername(e.target.value)}
                            autoComplete="username" required />
                    </div>

                    <div className="authField">
                        <label className="authLabel">Email</label>
                        <input className="authInput" type="email" placeholder="you@example.com"
                            value={email} onChange={e => setEmail(e.target.value)}
                            autoComplete="email" required />
                    </div>

                    <div className="authField">
                        <label className="authLabel">Password</label>
                        <div className="authInputWrap">
                            <input className="authInput" type={showPass ? 'text' : 'password'}
                                placeholder="Min. 8 characters"
                                value={password} onChange={e => setPassword(e.target.value)}
                                autoComplete="new-password" required />
                            <EyeBtn show={showPass} toggle={() => setShowPass(s => !s)} />
                        </div>
                        <StrengthBar password={password} />
                    </div>

                    <div className="authField">
                        <label className="authLabel">Confirm password</label>
                        <div className="authInputWrap">
                            <input
                                className={`authInput${!passwordMatch ? ' inputError' : ''}`}
                                type={showPass ? 'text' : 'password'}
                                placeholder="Repeat password"
                                value={confirm} onChange={e => setConfirm(e.target.value)}
                                autoComplete="new-password" required />
                        </div>
                        {!passwordMatch && <span className="authError">Passwords don't match</span>}
                    </div>

                    <label className="authCheckbox">
                        <input type="checkbox" checked={agree} onChange={e => setAgree(e.target.checked)} />
                        <span className="checkMark" />
                        <span className="checkText">
                            I agree to the <Link to="/terms" className="authSwitchLink">Terms</Link> and{' '}
                            <Link to="/privacy" className="authSwitchLink">Privacy Policy</Link>
                        </span>
                    </label>

                    <button className={`authBtn${loading ? ' loading' : ''}`} type="submit"
                        disabled={loading || !agree || !passwordMatch}>
                        {loading ? <span className="authSpinner" /> : 'Create account'}
                    </button>
                </form>

                <div className="authDivider"><span>or sign up with</span></div>

                <div className="authSocial">
                    <button className="authSocialBtn" type="button" aria-label="Google"><GoogleIcon /></button>
                    <button className="authSocialBtn" type="button" aria-label="Facebook"><FacebookIcon /></button>
                </div>

                <p className="authSwitch">
                    Already have an account? <Link to="/login" className="authSwitchLink">Sign in</Link>
                </p>
            </div>
        </div>
    )
}