export const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function apiFetch(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(options.headers || {}),
  };

  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (res.status === 401) {
    localStorage.removeItem('token');
    window.location.href = '/';
    return;
  }

  if (res.status === 204) {
    return {};
  }

  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error(data.message || `HTTP ${res.status}`);
  }
  return data;
}

export function loginUser(login, password) {
  return apiFetch('/users/token', {
    method: 'POST',
    body: JSON.stringify({ login, password }),
  });
}

export function registerUser(login, password, phone) {
  return apiFetch('/users', {
    method: 'POST',
    body: JSON.stringify({ login, password, phone }),
  });
}

export function validateToken() {
  const token = localStorage.getItem('token');
  return apiFetch('/users/valid', {
    method: 'POST',
    body: JSON.stringify({ token }),
  });
}

export const getBooks = async () => {
  const response = await fetch(`${API_BASE}/books/top`);
  if (!response.ok) {
    throw new Error('Не удалось загрузить книги');
  }
  return await response.json();
};

export function createBook(owner, name, author, genre, description) {
  return apiFetch('/books', {
    method: 'POST',
    body: JSON.stringify({ owner, name, author, genre, description }),
  });
}

export function appendBook(owner, name) {
  return apiFetch('/books/append', {
    method: 'POST',
    body: JSON.stringify({ owner, name }),
  });
}


export function createExchange(initiator, recipient, bookInitiator, bookRecipient) {
  return apiFetch('/exchanges', {
    method: 'POST',
    body: JSON.stringify({ initiator, recipient, bookInitiator, bookRecipient }),
  });
}


export function getInitiatedExchanges(login) {
  return apiFetch(`/exchanges/initiator/${encodeURIComponent(login)}`);
}


export function getReceivedExchanges(login) {
  return apiFetch(`/exchanges/recipient/${encodeURIComponent(login)}`);
}


export function updateExchangeStatus(exchangeId, status) {
  return apiFetch(`/exchanges/${exchangeId}/status?status=${encodeURIComponent(status)}`, {
    method: 'PATCH',
  });
}

export function getBookById(bookId) {
  return apiFetch(`/books/${encodeURIComponent(bookId)}`);
}


export function getBookOwners(bookId) {
  return apiFetch(`/books/owners/${encodeURIComponent(bookId)}`);
}


export function getReviewsByBook(bookId) {
  return apiFetch(`/reviews/book/${bookId}`);
}

export function createReview(author, bookName, rating, comment) {
  return apiFetch('/reviews', {
    method: 'POST',
    body: JSON.stringify({ author, bookName, rating, comment }),
  });
}

export function getReviewStats(bookId) {
  return apiFetch(`/reviews/stats/${bookId}`);
}



export function getBooksStats() {
  return apiFetch('/books/stats');
}

export function getReviewsGlobalStats() {
  return apiFetch('/reviews/stats');
}

export function getExchangesStats() {
  return apiFetch('/exchanges/stats');
}

export function getLastExchanges() {
  return apiFetch('/exchanges/last');
}

export function promoteToAdmin(login, code) {
  return apiFetch(`/users/role/${encodeURIComponent(code)}`, {
    method: 'POST',
    body: JSON.stringify({
      login,
      role: 'ADMIN'
    }),
  });
}


export function getBooksByUser(login) {
  return apiFetch(`/books/user/${encodeURIComponent(login)}`);
}

export function removeBookFromUser(owner, name) {
  return apiFetch('/books/delete', {
    method: 'PUT',
    body: JSON.stringify({ name, owner }),
  });
}

export function getUserByToken(token) {
  return apiFetch('/users/valid', {
    method: 'POST',
    body: JSON.stringify({ token }),
  });
}
