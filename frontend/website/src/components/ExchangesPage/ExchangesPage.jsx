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
import SuccessModal from '../SuccessModal/SuccessModal';

const ExchangesPage = () => {
  const { username } = useAuth();
  const [allBooks, setAllBooks] = useState([]);
  const [myBooks, setMyBooks] = useState([]);
  const [otherBooks, setOtherBooks] = useState([]);

  const [offeredBook, setOfferedBook] = useState('');
  const [requestedBook, setRequestedBook] = useState('');
  const [targetUser, setTargetUser] = useState('');

  const [outgoingExchanges, setOutgoingExchanges] = useState([]);
  const [incomingExchanges, setIncomingExchanges] = useState([]);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchBooks = async () => {
    try {
      const books = await getBooks();
      setAllBooks(books);

      const mine = books.filter((b) => b.owners && b.owners.includes(username));
      setMyBooks(mine);

      const others = books.filter((b) => !(b.owners && b.owners.includes(username)));
      setOtherBooks(others);
    } catch (e) {
      setError('Не удалось загрузить список книг');
    }
  };

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
  }, []);


  const handleCreateExchange = async (e) => {
    e.preventDefault();

    if (!offeredBook || !requestedBook || !targetUser.trim()) {
      setError('Пожалуйста, заполните все поля для обмена');
      return;
    }

    try {

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

      setOfferedBook('');
      setRequestedBook('');
      setTargetUser('');


      fetchExchanges();
      fetchBooks();
    } catch (e) {
      setError(e.message || 'Ошибка при создании обмена');
    }
  };


  const handleChangeStatus = async (exchangeId, newStatus) => {
    try {
      await updateExchangeStatus(exchangeId, newStatus);
      setSuccess(
        newStatus === 'done'
          ? 'Обмен подтверждён!'
          : 'Обмен отменён!'
      );

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
      {error && <ErrorModal visible={true} message={error} onClose={closeError} />}

      {success && (<SuccessModal visible={true} message={success} onClose={closeSuccess} />)}
    </div>
  );
};

export default ExchangesPage;
