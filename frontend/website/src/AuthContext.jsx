import React, { createContext, useContext, useEffect, useState } from 'react';
import {
  registerUser,
  loginUser,
  validateToken as validateTokenRequest
} from './api'; // скорректируйте путь, если у вас иначе

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);



export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [username, setUsername] = useState(() => localStorage.getItem('username') || null);
  const [loading, setLoading] = useState(true);

  // Проверяем валидность токена при монтировании
  useEffect(() => {
    const checkToken = async () => {
      if (!token) {
        setLoading(false);
        return;
      }
      try {
        await validateTokenRequest({ token });
        setLoading(false);
      } catch (e) {
        // если токен не валидный — выходим
        console.warn('Token validation failed:', e);
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        setToken(null);
        setUsername(null);
        setLoading(false);
      }
    };
    checkToken();
  }, [token]);

  const login = (newToken, user) => {
    localStorage.setItem('token', newToken);
    localStorage.setItem('username', user);
    setToken(newToken);
    setUsername(user);
  };

  const register = async (user, pass, phone) => {
    // 1) создаём пользователя
    await registerUser(user, pass, phone);
    // 2) сразу логинимся и сохраняем токен
    const { token: newToken } = await loginUser(user, pass);
    login(newToken, user);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setToken(null);
    setUsername(null);
  };

  return (
    <AuthContext.Provider value={{ token, username, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
