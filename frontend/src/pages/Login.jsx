import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { login } from '../api/auth'
import { saveSession } from '../auth'
import AuthShell from '../components/AuthShell'
import { message } from '../utils/message'

export default function Login() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [loading, setLoading] = useState(false)

  const onChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const onSubmit = async (e) => {
    e.preventDefault()
    if (!form.username || !form.password) {
      message.error('请输入用户名和密码')
      return
    }
    setLoading(true)
    try {
      const res = await login(form)
      saveSession(res.data)
      navigate('/', { replace: true })
    } catch {
      /* 接口错误由 message 统一提示 */
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthShell caption="灯还亮着，人就到齐了">
      <div className="auth-card">
        <div className="brand">
          <h1>回家</h1>
          <p>推开门，灯还亮着</p>
        </div>
        <form onSubmit={onSubmit}>
          <label>
            用户名
            <input name="username" value={form.username} onChange={onChange} placeholder="请输入用户名" autoComplete="username" />
          </label>
          <label>
            密码
            <input name="password" type="password" value={form.password} onChange={onChange} placeholder="请输入密码" autoComplete="current-password" />
          </label>
          <button type="submit" disabled={loading}>{loading ? '进门中...' : '进门'}</button>
        </form>
        <p className="switch">
          还没有钥匙？<Link to="/register">先做一个</Link>
        </p>
      </div>
    </AuthShell>
  )
}
