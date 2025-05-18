import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './AuthContext';
import PrivateRoute from './components/PrivateRoute/PrivateRoute';
import Header from './components/Header/Header';
import LandingPage from './components/LandingPage/LandingPage';
import MainPage from './components/MainPage/MainPage';
import BookPage from './components/BookPage/BookPage';
import ProfilePage from './components/ProfilePage/ProfilePage';
import ExchangesPage from './components/ExchangesPage/ExchangesPage';
import AdminPanel from './components/AdminPanel/AdminPanel';



function App() {
  return (
    <AuthProvider>
      <Router>
        <Header />
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/main" element={<PrivateRoute element={MainPage} />} />
          <Route path="/book/:id" element={<PrivateRoute element={BookPage} />} />
          <Route
            path="/profile/me"
            element={<PrivateRoute element={ProfilePage} />}
          />
          <Route
            path="/exchanges"
            element={<PrivateRoute element={ExchangesPage} />}
          />
          <Route path="/admin" element={<PrivateRoute element={AdminPanel} />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
