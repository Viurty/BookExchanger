import React, { useEffect, useState } from 'react';
import { useAuth } from '../../AuthContext';
import { getBooksByUser, removeBookFromUser, getUserByToken } from '../../api';
import ErrorModal from '../ErrorModal/ErrorModal';

const ProfilePage = () => {
  const { username, token } = useAuth();
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState('');
  const [books, setBooks] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadUserDetails();
    loadUserBooks();
  }, []);

  const loadUserDetails = async () => {
    try {
      const data = await getUserByToken(token);
      setPhone(data.phone);
      setRole(data.role);
    } catch (e) {
      setError(e.message || 'Не удалось получить данные профиля');
    }
  };

  const loadUserBooks = async () => {
    try {
      const data = await getBooksByUser(username);
      setBooks(data);
    } catch (e) {
      setError(e.message || 'Не удалось загрузить книги');
    }
  };

  const handleUnlink = async (bookName) => {
    try {
      await removeBookFromUser(username, bookName);
      setSuccess(`Книга "${bookName}" отвязана`);
      loadUserBooks();
    } catch (e) {
      setError(e.message || 'Ошибка при отвязке книги');
    }
  };

  const closeError = () => setError('');
  const closeSuccess = () => setSuccess('');

  return (
    <div className="profile-container">
      <h1>Мой профиль</h1>
      <p><strong>Логин:</strong> {username}</p>
      <p><strong>Телефон:</strong> {phone || '—'}</p>
      <p><strong>Роль:</strong> {role || '—'}</p>

      <h2 style={{ marginTop: '30px' }}>Мои книги</h2>
      {books.length === 0 ? (
        <p>У вас пока нет книг</p>
      ) : (
        <ul>
          {books.map((book) => (
            <li key={book.id} style={{ marginBottom: '10px' }}>
              <strong>{book.name}</strong> — {book.author}
              <button
                onClick={() => handleUnlink(book.name)}
                style={{ marginLeft: '10px' }}
              >
                Отвязать
              </button>
            </li>
          ))}
        </ul>
      )}

      {error && <ErrorModal visible message={error} onClose={closeError} />}
      {success && <ErrorModal visible message={success} onClose={closeSuccess} />}
    </div>
  );
};

export default ProfilePage;
