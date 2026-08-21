import { Navigate, Route, Routes } from 'react-router-dom'
import { isLoggedIn } from './auth'
import Family from './pages/Family'
import Home from './pages/Home'
import Login from './pages/Login'
import Memory from './pages/Memory'
import Register from './pages/Register'
import Todos from './pages/Todos'

function Guard({ children }) {
  return isLoggedIn() ? children : <Navigate to="/login" replace />
}

function Guest({ children }) {
  return isLoggedIn() ? <Navigate to="/" replace /> : children
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Guest><Login /></Guest>} />
      <Route path="/register" element={<Guest><Register /></Guest>} />
      <Route path="/" element={<Guard><Home /></Guard>} />
      <Route path="/family" element={<Guard><Family /></Guard>} />
      <Route path="/todos" element={<Guard><Todos /></Guard>} />
      <Route path="/memories" element={<Guard><Memory /></Guard>} />
    </Routes>
  )
}
