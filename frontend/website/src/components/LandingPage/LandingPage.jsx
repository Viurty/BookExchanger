import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser } from '../../api';
import { useAuth } from '../../AuthContext';
import ErrorModal from '../ErrorModal/ErrorModal';

const LandingPage = () => {
    const [mode, setMode] = useState('login');
    const [inputUsername, setInputUsername] = useState('');
    const [inputPassword, setInputPassword] = useState('');
    const [inputPhone, setInputPhone] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login, register } = useAuth();

    const handleSubmit = async () => {
        try {
            if (mode === 'login') {
                const res = await loginUser(inputUsername, inputPassword);
                login(res.token, inputUsername);
            } else {
                await register(inputUsername, inputPassword, inputPhone);
            }
            navigate('/main');
        } catch (e) {
            console.error('Auth error:', e);
            const msg = e.response?.data?.message || e.message || 'Неизвестная ошибка';
            setError(msg);
        }
    };

    const closeModal = () => setError('');

    return (
        <div className="landing-container">
            <div className="card">
                <h1>Добро пожаловать!</h1>
                <p>Обменивайтесь книгами, делитесь отзывами и рекомендациями.</p>

                <div className="tab-buttons">
                    <button
                        className={mode === 'login' ? 'active' : ''}
                        onClick={() => setMode('login')}
                    >
                        Вход
                    </button>

                    <button
                        className={mode === 'register' ? 'active' : ''}
                        onClick={() => setMode('register')}
                    >
                        Регистрация
                    </button>
                </div>

                <label>Логин:</label>
                <input
                    type="text"
                    placeholder="Ваш логин"
                    value={inputUsername}
                    onChange={(e) => setInputUsername(e.target.value)}
                />

                <label>Пароль:</label>
                <input
                    type="password"
                    placeholder="Ваш пароль"
                    value={inputPassword}
                    onChange={(e) => setInputPassword(e.target.value)}
                />

                {mode === 'register' && (
                    <>
                        <label>Номер телефона:</label>
                        <input
                            type="tel"
                            placeholder="+7..."
                            value={inputPhone}
                            onChange={(e) => setInputPhone(e.target.value)}
                        />
                    </>
                )}

                <button onClick={handleSubmit}>
                    {mode === 'login' ? 'Войти' : 'Зарегистрироваться'}
                </button>

                <footer>© Все права защищены.</footer>
            </div>
            <ErrorModal
                visible={!!error}
                message={error}
                onClose={closeModal}
            />
        </div>
    );
};

export default LandingPage;