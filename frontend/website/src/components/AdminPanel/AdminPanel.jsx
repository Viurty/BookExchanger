import React, { useEffect, useState } from 'react';
import {
  getBooksStats,
  getReviewsGlobalStats,
  getExchangesStats,
  getLastExchanges,
  promoteToAdmin,
  getUserByToken,
} from '../../api';

import { useAuth } from '../../AuthContext';
import ErrorModal from '../ErrorModal/ErrorModal';
import SuccessModal from '../SuccessModal/SuccessModal';
const AdminPanel = () => {
  const { token, refreshUser } = useAuth();

  const [userInfo, setUserInfo] = useState({ username: '', role: '' });
  const [loadingUser, setLoadingUser] = useState(true);
  const [loadingStats, setLoadingStats] = useState(false);

  const [bookStats, setBookStats] = useState(null);
  const [reviewStats, setReviewStats] = useState(null);
  const [exchangeStats, setExchangeStats] = useState(null);
  const [lastExchanges, setLastExchanges] = useState([]);

  const [code, setCode] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchUser = async () => {
    try {
      setLoadingUser(true);
      const data = await getUserByToken(token);
      setUserInfo({ username: data.login, role: data.role });
    } catch (e) {
      setError(e.message || 'Не удалось получить данные пользователя');
    } finally {
      setLoadingUser(false);
    }
  };

  useEffect(() => {
    fetchUser();
  }, [token]);

  useEffect(() => {
    if (!loadingUser && userInfo.role === 'ADMIN') {
      fetchAllStats();
    }
  }, [loadingUser, userInfo.role]);

  const fetchAllStats = async () => {
    try {
      setLoadingStats(true);
      const [books, reviews, exchanges, recent] = await Promise.all([
        getBooksStats(),
        getReviewsGlobalStats(),
        getExchangesStats(),
        getLastExchanges(),
      ]);
      setBookStats(books);
      setReviewStats(reviews);
      setExchangeStats(exchanges);
      setLastExchanges(recent);
    } catch (e) {
      setError(e.message || 'Ошибка при загрузке статистики');
    } finally {
      setLoadingStats(false);
    }
  };

  const handleUpgrade = async (e) => {
    e.preventDefault();
    try {
      await promoteToAdmin(userInfo.username, code.trim());
      setSuccess('Доступ администратора предоставлен!');
      await fetchUser();
      await refreshUser?.();
    } catch (e) {
      setError(e.message || 'Неверный код или ошибка при повышении прав');
    }
  };

  const closeError = () => setError('');
  const closeSuccess = () => setSuccess('');

  if (loadingUser) {
    return <p>Загрузка данных пользователя...</p>;
  }

  if (userInfo.role !== 'ADMIN') {
    return (
      <div className="admin-panel">
        <h1>Доступ администратора</h1>
        <p>Введите секретный код для подтверждения прав:</p>
        <form onSubmit={handleUpgrade}>
          <input
            type="text"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="Секретный код"
          />
          <button type="submit">Подтвердить</button>
        </form>

        {error && <ErrorModal visible message={error} onClose={closeError} />}
        {success && <ErrorModal visible message={success} onClose={closeSuccess} />}
      </div>
    );
  }

  return (
    <div className="admin-panel">
      <h1>Панель администратора</h1>

      <section className="stats-section">
        <h2>📚 Книги</h2>
        {bookStats ? (
          <ul>
            <li>Всего книг: {bookStats.countBooks}</li>
            <li>Готовых к обмену: {bookStats.countReadyBooks}</li>
            <li>Процент готовых к обмену: {bookStats.percentReady}%</li>
          </ul>
        ) : (
          <p>{loadingStats ? 'Загрузка...' : 'Нет данных'}</p>
        )}
      </section>

      <section className="stats-section">
        <h2>📝 Отзывы</h2>
        {reviewStats ? (
          <ul>
            <li>Всего: {reviewStats.countReviews}</li>
            <li>5★: {reviewStats.countRate5}</li>
            <li>1★: {reviewStats.countRate1}</li>
          </ul>
        ) : (
          <p>{loadingStats ? 'Загрузка...' : 'Нет данных'}</p>
        )}
      </section>

      <section className="stats-section">
        <h2>🔁 Обмены</h2>
        {exchangeStats ? (
          <ul>
            <li>Всего: {exchangeStats.countExchanges}</li>
            <li>Успешных: {exchangeStats.countSuccessExchanges}</li>
            <li>Процент успешных: {exchangeStats.percentSuccess}%</li>
          </ul>
        ) : (
          <p>{loadingStats ? 'Загрузка...' : 'Нет данных'}</p>
        )}
      </section>

      <section className="stats-section">
        <h2>🕒 Последние обмены</h2>
        {lastExchanges.length > 0 ? (
          <ul>
            {lastExchanges.map((ex) => (
              <li key={ex.id}>
                {ex.initiator} ↔ {ex.recipient} — <strong>{ex.status}</strong>
              </li>
            ))}
          </ul>
        ) : (
          <p>Нет новых обменов</p>
        )}
      </section>

      {error && <ErrorModal visible message={error} onClose={closeError} />}

      {success && <SuccessModal visible message={success} onClose={closeSuccess} />}
    </div>
  );
};

export default AdminPanel;
