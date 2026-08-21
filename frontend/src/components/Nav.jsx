import { NavLink, useNavigate } from 'react-router-dom'
import { logout } from '../api/auth'
import { clearSession, getUser } from '../auth'

export default function Nav() {
  const navigate = useNavigate()
  const user = getUser()

  const onLogout = async () => {
    try {
      await logout()
    } catch {
      /* 本地清会话即可 */
    }
    clearSession()
    navigate('/login', { replace: true })
  }

  return (
    <header className="topbar">
      <div className="shell topbar-inner">
        <div className="brand-mini">
          <span className="logo">家</span>
          家享生活
        </div>
        <nav className="nav-links">
          <NavLink to="/" end>回家</NavLink>
          <NavLink to="/family">家里</NavLink>
          <NavLink to="/todos">清单</NavLink>
          <NavLink to="/memories">照片墙</NavLink>
        </nav>
        <div className="user-bar">
          <span>{user?.nickname}</span>
          <button className="ghost" onClick={onLogout}>出门</button>
        </div>
      </div>
    </header>
  )
}
