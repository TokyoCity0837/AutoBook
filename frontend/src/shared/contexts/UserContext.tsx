import React, { createContext, useContext, useCallback, useEffect, useMemo, useState } from 'react';
import { userRepository } from '../../data/repositories';
import type { UserProfile } from '../../domain/models';

type UserContextType = {
  profileMe: UserProfile | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: unknown;
  refreshProfile: () => Promise<void>;
  clearUser: () => void;
};

const UserContext = createContext<UserContextType | undefined>(undefined);

export function UserProvider({ children }: { children: React.ReactNode }) {
  const [profileMe, setProfileMe] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<unknown>(null);

  const clearUser = useCallback(() => {
    setProfileMe(null);
    setError(null);
    setLoading(false);
  }, []);

  const refreshProfile = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await userRepository.getMe();
      setProfileMe(data);
    } catch (e) {
      setProfileMe(null);
      setError(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refreshProfile();
  }, [refreshProfile]);

  const isAuthenticated = !!profileMe;

  const value = useMemo(
    () => ({ profileMe, isAuthenticated, loading, error, refreshProfile, clearUser }),
    [profileMe, isAuthenticated, loading, error, refreshProfile, clearUser]
  );

  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
}

export function useUser() {
  const ctx = useContext(UserContext);
  if (!ctx) throw new Error('useUser must be used within UserProvider');
  return ctx;
}
