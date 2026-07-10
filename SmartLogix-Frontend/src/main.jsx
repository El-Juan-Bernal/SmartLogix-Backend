import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles/global.css'
import App from './App.jsx'

// Apagamos el simulador comentando esta función
/*
async function enableMocking() {
  if (import.meta.env.MODE !== 'development') return
  const { worker } = await import('./test/mocks/browser')
  return worker.start({ onUnhandledRequest: 'bypass' })
}
*/

// Dibujamos la aplicación directamente
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)

