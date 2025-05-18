import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getBooks, createBook, appendBook } from '../../api';
import { useAuth } from '../../AuthContext';
import ErrorModal from '../ErrorModal/ErrorModal';

const MainPage = () => {
  const navigate = useNavigate();
  const { username } = useAuth();
  const [books, setBooks] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [newName, setNewName] = useState('');
  const [newAuthor, setNewAuthor] = useState('');
  const [newGenre, setNewGenre] = useState('');
  const [newDescription, setNewDescription] = useState('');

  const [existName, setExistName] = useState('');

  const fetchBooks = async () => {
    try {
      const data = await getBooks();
      setBooks(data);
    } catch (err) {
      console.error(err);
      setError('Ошибка при загрузке книг');
    }
  };

  useEffect(() => {
    fetchBooks();
  }, []);

  const goToBookPage = (id) => {
    navigate(`/book/${id}`);
  };

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!newName.trim() || !newAuthor.trim() || !newGenre.trim() || !newDescription.trim()) {
        setError('Пожалуйста, заполните все поля новой книги');
        return;
      }
      await createBook(username, newName.trim(), newAuthor.trim(), newGenre.trim(), newDescription.trim());
      setSuccess('Новая книга успешно добавлена!');
      setNewName('');
      setNewAuthor('');
      setNewGenre('');
      setNewDescription('');
      fetchBooks();
    } catch (e) {
      setError(e.message || 'Не удалось создать книгу');
    }
  };

  const handleAppendSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!existName.trim()) {
        setError('Укажите название существующей книги');
        return;
      }
      await appendBook(username, existName.trim());
      setSuccess('Книга успешно привязана к вашему профилю!');
      setExistName('');
      fetchBooks();
    } catch (e) {
      setError(e.message || 'Не удалось привязать книгу');
    }
  };

  const closeErrorModal = () => setError('');
  const closeSuccessModal = () => setSuccess('');

  return (
    <div className="main-container">
      <h1>Библиотека книг</h1>

      <section className="form-section">
        <h2>Добавить новую книгу</h2>
        <form onSubmit={handleCreateSubmit} className="book-form">
          <label>
            Название:
            <input
              type="text"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              placeholder="Введите название"
            />
          </label>
          <label>
            Автор:
            <input
              type="text"
              value={newAuthor}
              onChange={(e) => setNewAuthor(e.target.value)}
              placeholder="Введите автора"
            />
          </label>
          <label>
            Жанр:
            <input
              type="text"
              value={newGenre}
              onChange={(e) => setNewGenre(e.target.value)}
              placeholder="Введите жанр"
            />
          </label>
          <label>
            Описание:
            <textarea
              value={newDescription}
              onChange={(e) => setNewDescription(e.target.value)}
              placeholder="Краткое описание книги"
            />
          </label>
          <button type="submit">Создать книгу</button>
        </form>
      </section>

      <section className="form-section">
        <h2>Добавить существующую книгу к моему профилю</h2>
        <form onSubmit={handleAppendSubmit} className="book-form">
          <label>
            Название:
            <input
              type="text"
              value={existName}
              onChange={(e) => setExistName(e.target.value)}
              placeholder="Введите название уже существующей книги"
            />
          </label>
          <button type="submit">Добавить к профилю</button>
        </form>
      </section>

      <div className="book-grid">
        {books.map((book) => (
          <div
            key={book.id}
            className="book-card"
            onClick={() => goToBookPage(book.id)}
          >
            <h2>{book.name}</h2>
            <p className="author">Автор: {book.author}</p>
            <p className="genre">Жанр: {book.genre}</p>
            <p className="owners">
              Владельцы: {book.owners && book.owners.join(', ')}
            </p>
          </div>
        ))}
      </div>

      {error && (
        <ErrorModal visible={true} message={error} onClose={closeErrorModal} />
      )}
      {success && (
        <ErrorModal visible={true} message={success} onClose={closeSuccessModal} />
      )}
    </div>
  );
};

export default MainPage;
