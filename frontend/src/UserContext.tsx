import React, { createContext, useContext, useCallback, useEffect, useMemo, useState } from 'react';
import api from './api';

type UserProfile = any; // потім типізуєш нормально

type UserContextType = {
  profile: UserProfile | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: unknown;
  refreshProfile: () => Promise<void>;
  clearUser: () => void;
};

const UserContext = createContext<UserContextType | undefined>(undefined);

export function UserProvider({ children }: { children: React.ReactNode }) {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<unknown>(null);

  const clearUser = useCallback(() => {
    setProfile(null);
    setError(null);
    setLoading(false);
  }, []);

  const refreshProfile = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get('/users/profile/me');
      setProfile(res.data);
    } catch (e) {
      // якщо 401 — інтерцептор вже редіректить.
      // тут просто збережемо "нема юзера" і не падаємо
      setProfile(null);
      setError(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // В app-зоні (де є Layout) одразу пробуємо підтягнути me
    refreshProfile();
  }, [refreshProfile]);

  const isAuthenticated = !!profile;

  const value = useMemo(
    () => ({ profile, isAuthenticated, loading, error, refreshProfile, clearUser }),
    [profile, isAuthenticated, loading, error, refreshProfile, clearUser]
  );

  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
}

export function useUser() {
  const ctx = useContext(UserContext);
  if (!ctx) throw new Error('useUser must be used within UserProvider');
  return ctx;
}