import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../api/auth'
import { saveSession } from '../auth'
import AuthShell from '../components/AuthShell'
import { message } from '../utils/message'

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({
    username: '',
    nickname: '',
    phone: '',
    password: '',
    confirm: '',
  })
  const [loading, setLoading] = useState(false)

  const onChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const onSubmit = async (e) => {
    e.preventDefault()
    if (!/^[a-zA-Z0-9_]{4,20}$/.test(form.username)) {
      message.error('用户名需为 4-20 位字母、数字或下划线')
      return
    }
    if (!form.nickname.trim()) {
      message.error('请填写昵称')
      return
    }
    if (form.password.length < 6 || form.password.length > 20) {
      message.error('密码长度为 6-20 位')
      return
    }
    if (form.password !== form.confirm) {
      message.error('两次密码不一致')
      return
    }
    if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) {
      message.error('手机号格式不正确')
      return
    }
    setLoading(true)
    try {
      const res = await register({
        username: form.username,
        nickname: form.nickname.trim(),
        phone: form.phone,
        password: form.password,
      })
      saveSession(res.data)
      navigate('/', { replace: true })
    } catch {
      /* 接口错误由 message 统一提示 */
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthShell caption="先把钥匙配好">
      <div className="auth-card">
        <div className="brand">
          <h1>配一把钥匙</h1>
          <p>进门之后，灯就亮了</p>
        </div>
        <form onSubmit={onSubmit}>
          <label>
            用户名
            <input name="username" value={form.username} onChange={onChange} placeholder="4-20 位字母数字下划线" />
          </label>
          <label>
            昵称
            <input name="nickname" value={form.nickname} onChange={onChange} placeholder="家里人怎么称呼你" />
          </label>
          <label>
            手机号（选填）
            <input name="phone" value={form.phone} onChange={onChange} placeholder="11 位手机号" />
          </label>
          <label>
            密码
            <input name="password" type="password" value={form.password} onChange={onChange} placeholder="6-20 位密码" />
          </label>
          <label>
            确认密码
            <input name="confirm" type="password" value={form.confirm} onChange={onChange} placeholder="再次输入密码" />
          </label>
          <button type="submit" disabled={loading}>{loading ? '配钥匙中...' : '配好进门'}</button>
        </form>
        <p className="switch">
          已经有钥匙了？<Link to="/login">回家</Link>
        </p>
      </div>
    </AuthShell>
  )
}
