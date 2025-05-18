// frontend/website/src/components/ExchangesPage/ExchangesPage.jsx

import React, { useEffect, useState } from 'react';
import { useAuth } from '../../AuthContext';
import {
  getBooks,
  createExchange,
  getInitiatedExchanges,
  getReceivedExchanges,
  updateExchangeStatus,
} from '../../api';

import ErrorModal from '../ErrorModal/ErrorModal';

const ExchangesPage = () => {
  const { username } = useAuth(); // логин текущего пользователя
  const [allBooks, setAllBooks] = useState([]);
  const [myBooks, setMyBooks] = useState([]);
  const [otherBooks, setOtherBooks] = useState([]);

  // Поля формы создания обмена
  const [offeredBook, setOfferedBook] = useState('');   // хранит ID книги, которую отдаём
  const [requestedBook, setRequestedBook] = useState(''); // хранит ID книги, которую хотим получить
  const [targetUser, setTargetUser] = useState('');

  // Загрузка списков обменов
  const [outgoingExchanges, setOutgoingExchanges] = useState([]); // “Я предложил”
  const [incomingExchanges, setIncomingExchanges] = useState([]); // “Мне предложили”

  // Ошибки и успехи
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // --- Загрузка всех книг и разделение на «мои» и «чужие» ---
  const fetchBooks = async () => {
    try {
      const books = await getBooks();
      setAllBooks(books);

      // мои книги = те, где owners содержит username
      const mine = books.filter((b) => b.owners && b.owners.includes(username));
      setMyBooks(mine);

      // чужие книги = остальные
      const others = books.filter((b) => !(b.owners && b.owners.includes(username)));
      setOtherBooks(others);
    } catch (e) {
      setError('Не удалось загрузить список книг');
    }
  };

  // --- Загрузка обменов: исходящие и входящие ---
  const fetchExchanges = async () => {
    try {
      const [out, inc] = await Promise.all([
        getInitiatedExchanges(username),
        getReceivedExchanges(username),
      ]);
      setOutgoingExchanges(out);
      setIncomingExchanges(inc);
    } catch (e) {
      setError('Не удалось загрузить обмены');
    }
  };

  useEffect(() => {
    fetchBooks();
    fetchExchanges();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // --- Создание нового обмена ---
  const handleCreateExchange = async (e) => {
    e.preventDefault();

    if (!offeredBook || !requestedBook || !targetUser.trim()) {
      setError('Пожалуйста, заполните все поля для обмена');
      return;
    }

    try {
      // Из ID книги находим название (бэкенд ожидает название книги)
      const offeredName = myBooks.find((b) => b.id === +offeredBook)?.name;
      const requestedName = otherBooks.find((b) => b.id === +requestedBook)?.name;

      if (!offeredName || !requestedName) {
        setError('Выбраны некорректные книги');
        return;
      }

      await createExchange(
        username,
        targetUser.trim(),
        offeredName,
        requestedName
      );

      setSuccess('Обмен успешно создан!');
      // Сброс полей
      setOfferedBook('');
      setRequestedBook('');
      setTargetUser('');

      // Обновляем списки
      fetchExchanges();
      fetchBooks();
    } catch (e) {
      setError(e.message || 'Ошибка при создании обмена');
    }
  };

  // --- Изменение статуса обмена (для Confirm/Cancel) ---
  const handleChangeStatus = async (exchangeId, newStatus) => {
    try {
      await updateExchangeStatus(exchangeId, newStatus);
      setSuccess(
        newStatus === 'done'
          ? 'Обмен подтверждён!'
          : 'Обмен отменён!'
      );
      // Обновляем списки
      fetchExchanges();
      fetchBooks();
    } catch (e) {
      setError(e.message || 'Не удалось изменить статус обмена');
    }
  };

  const closeError = () => setError('');
  const closeSuccess = () => setSuccess('');

  return (
    <div className="exchange-container">
      <h1>Обмены</h1>

      {/* --- Форма создания нового обмена --- */}
      <section className="exchange-form-section">
        <h2>Создать обмен</h2>
        <form onSubmit={handleCreateExchange} className="exchange-form">
          <label>
            Моя книга:
            <select
              value={offeredBook}
              onChange={(e) => setOfferedBook(e.target.value)}
              required
            >
              <option value="">— Выберите книгу —</option>
              {myBooks.map((book) => (
                <option key={book.id} value={book.id}>
                  {book.name} — {book.author}
                </option>
              ))}
            </select>
          </label>

          <label>
            Хочу получить:
            <select
              value={requestedBook}
              onChange={(e) => setRequestedBook(e.target.value)}
              required
            >
              <option value="">— Выберите книгу —</option>
              {otherBooks.map((book) => (
                <option key={book.id} value={book.id}>
                  {book.name} — {book.author}
                </option>
              ))}
            </select>
          </label>

          <label>
            Логин получателя:
            <input
              type="text"
              value={targetUser}
              onChange={(e) => setTargetUser(e.target.value)}
              placeholder="Введите логин человека"
              required
            />
          </label>

          <button type="submit">Отправить предложение</button>
        </form>
      </section>

      {/* --- Список «Я предложил» (исходящие обмены) --- */}
      <section className="exchange-list-section">
        <h2>Я предложил</h2>
        {outgoingExchanges.length === 0 ? (
          <p>У вас нет исходящих обменов</p>
        ) : (
          <div className="exchange-grid">
            {outgoingExchanges.map((ex) => (
              <div key={ex.id} className="exchange-card">
                <p>
                  <strong>Кому:</strong> {ex.recipient}
                </p>
                <p>
                  <strong>Моя книга:</strong> {ex.bookInitiator}
                </p>
                <p>
                  <strong>Хочу:</strong> {ex.bookRecipient}
                </p>
                <p className={`status status-${ex.status}`}>
                  <strong>Статус:</strong> {ex.status}
                </p>

                {/* Если статус “wait” и текущий пользователь — инициатор, можно отменить */}
                {ex.status === 'wait' && (
                  <div className="exchange-actions">
                    <button
                      className="btn-cancel"
                      onClick={() => handleChangeStatus(ex.id, 'cancel')}
                    >
                      Отменить
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>

      {/* --- Список «Мне предложили» (входящие обмены) --- */}
      <section className="exchange-list-section">
        <h2>Мне предложили</h2>
        {incomingExchanges.length === 0 ? (
          <p>Никто вам пока не предлагал обмен</p>
        ) : (
          <div className="exchange-grid">
            {incomingExchanges.map((ex) => (
              <div key={ex.id} className="exchange-card">
                <p>
                  <strong>От кого:</strong> {ex.initiator}
                </p>
                <p>
                  <strong>Предлагает:</strong> {ex.bookInitiator}
                </p>
                <p>
                  <strong>Хочет:</strong> {ex.bookRecipient}
                </p>
                <p className={`status status-${ex.status}`}>
                  <strong>Статус:</strong> {ex.status}
                </p>

                {/* Если статус “wait” и текущий пользователь — получатель, показываем “Подтвердить” и “Отменить” */}
                {ex.status === 'wait' && (
                  <div className="exchange-actions">
                    <button
                      className="btn-confirm"
                      onClick={() => handleChangeStatus(ex.id, 'done')}
                    >
                      Подтвердить
                    </button>
                    <button
                      className="btn-cancel"
                      onClick={() => handleChangeStatus(ex.id, 'cancel')}
                    >
                      Отменить
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>

      {/* --- Модалки ошибок/успеха --- */}
      {error && <ErrorModal visible={true} message={error} onClose={closeError} />}
      {success && (
        <ErrorModal visible={true} message={success} onClose={closeSuccess} />
      )}
    </div>
  );
};

export default ExchangesPage;
