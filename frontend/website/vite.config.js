import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // всё, что идёт на /users и /users/token, будет проксироваться на бэк
      '/users': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      // если у вас есть другие эндпоинты, добавьте их по аналогии
    }
  }
});
