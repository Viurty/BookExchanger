import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../AuthContext';

const Header = () => {
  const { username, logout } = useAuth();
  return (
    <header className="header">
      <nav className="nav-links">
        <Link to="/main">Главная</Link>
        <Link to="/exchanges">Обмены</Link>
        <Link to="/admin">Админка</Link>
      </nav>
      <div className="user-info">
        {username ? (
          <>
            <Link to={`/profile/me`}>{username}</Link>
            <button onClick={logout} className="logout-btn">
              Выйти
            </button>
          </>
        ) : (
          <Link to="/">Войти</Link>
        )}
      </div>
    </header>
  );
};

export default Header;
