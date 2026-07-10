import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // Intercepta todo lo que empiece con /api
      '/api': {
        target: 'http://localhost:8080', // Apunta a tu BFF en Docker
        changeOrigin: true,
        // Transforma /api/auth/... a /api/v1/auth/... para que calce con tu backend
        rewrite: (path) => path.replace(/^\/api/, '/api/v1') 
      }
    }
  }
})


