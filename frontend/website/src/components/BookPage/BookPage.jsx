import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import {
  getBookById,
  getBookOwners,
  getReviewsByBook,
  getReviewStats,
  createReview,
} from '../../api';
import { useAuth } from '../../AuthContext';
import ErrorModal from '../ErrorModal/ErrorModal';
import SuccessModal from '../SuccessModal/SuccessModal'

const BookPage = () => {
  const { id } = useParams();
  const { username } = useAuth();
  const [book, setBook] = useState(null);
  const [owners, setOwners] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [stats, setStats] = useState(null);

  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (!/^\d+$/.test(id)) {
      setError('Некорректный ID книги');
      return;
    }
    fetchBookData();
  }, [id]);

  const fetchBookData = async () => {
    try {
      const bookData = await getBookById(id);
      setBook(bookData);

      const ownersData = await getBookOwners(bookData.id);
      setOwners(ownersData);

      const reviewsData = await getReviewsByBook(bookData.id);
      setReviews(reviewsData);

      const statsData = await getReviewStats(bookData.id);
      setStats(statsData);
    } catch (e) {
      setError(e.message || 'Не удалось загрузить данные');
    }
  };

  const closeError = () => setError('');
  const closeSuccess = () => setSuccess('');

  const handleSubmitReview = async (e) => {
    e.preventDefault();
    if (rating < 1 || rating > 5) {
      setError('Рейтинг должен быть от 1 до 5');
      return;
    }
    if (!comment.trim()) {
      setError('Напишите текст отзыва');
      return;
    }

    try {
      await createReview(username, book.name, rating, comment.trim());
      setSuccess('Отзыв успешно добавлен');
      setRating(5);
      setComment('');
      const updatedReviews = await getReviewsByBook(book.id);
      setReviews(updatedReviews);
      const updatedStats = await getReviewStats(book.id);
      setStats(updatedStats);
    } catch (e) {
      setError(e.message || 'Ошибка при добавлении отзыва');
    }
  };

  if (!book) {
    return (
      <div className="book-page">
        <p>Загрузка книги...</p>
      </div>
    );
  }

  return (
    <div className="book-page">
      <h1>{book.name}</h1>
      <p className="author">Автор: {book.author}</p>
      <p className="description">{book.description}</p>

      {/* 👇 Блок с оценкой */}
      {stats && typeof stats.avgRating === 'number' && (
        <p style={{ marginTop: '10px', fontSize: '16px', color: '#555' }}>
          Средняя оценка: <strong>{stats.avgRating.toFixed(1)}</strong> (на основе {stats.countRating} отзывов)
        </p>
      )}

      <div className="owners">
        <h3>Владельцы:</h3>
        {owners.length === 0 ? (
          <p>Пока нет владельцев</p>
        ) : (
          <ul>
            {owners.map((login) => (
              <li key={login}>
                <h4>{login}</h4>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="reviews">
        <h3>Отзывы:</h3>
        {reviews.length === 0 ? (
          <p>Пока никто не оставил отзыв</p>
        ) : (
          reviews.map((rev) => (
            <div key={rev.id} className="review">
              <p>
                <strong>{rev.author}</strong> — {rev.rating}/5
              </p>
              {rev.comment && <p>{rev.comment}</p>}
              {rev.createdAt && (
                <small>{new Date(rev.createdAt).toLocaleDateString()}</small>
              )}
            </div>
          ))
        )}
      </div>

      <div className="add-review">
        <h3>Оставить отзыв</h3>
        <form onSubmit={handleSubmitReview}>
          <label>
            Рейтинг:
            <select value={rating} onChange={(e) => setRating(+e.target.value)}>
              {[5, 4, 3, 2, 1].map((r) => (
                <option key={r} value={r}>{r}</option>
              ))}
            </select>
          </label>
          <label>
            Отзыв:
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Что думаете о книге?"
            />
          </label>
          <button type="submit">Отправить</button>
        </form>
      </div>
      {error && <ErrorModal visible={true} message={error} onClose={closeError} />}

      {success && <SuccessModal visible={true} message={success} onClose={closeSuccess} />}
    </div>
  );
};

export default BookPage;
