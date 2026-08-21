import { useEffect, useState } from 'react'
import { fetchMe } from '../api/auth'
import { createFamily, dissolveFamily, fetchMyFamily, joinFamily, leaveFamily, refreshInviteCode } from '../api/family'
import { saveUser } from '../auth'
import Nav from '../components/Nav'
import { message } from '../utils/message'

const formatCode = (code) => (code || '').replace(/(.{4})/g, '$1-').replace(/-$/, '')
const AVATARS = ['#e08a5d', '#7d9b86', '#8bb8c9', '#d4a5c9', '#f0b7a0']

export default function Family() {
  const [family, setFamily] = useState(null)
  const [loading, setLoading] = useState(true)
  const [name, setName] = useState('')
  const [inviteCode, setInviteCode] = useState('')
  const [copied, setCopied] = useState(false)

  const refreshUser = async () => {
    const res = await fetchMe()
    saveUser(res.data)
  }

  const load = async () => {
    try {
      const res = await fetchMyFamily()
      setFamily(res.data)
    } catch {
      /* 接口错误由 message 统一提示 */
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const onCreate = async (e) => {
    e.preventDefault()
    if (!name.trim()) {
      message.error('请填写家庭名称')
      return
    }
    try {
      const res = await createFamily({ name })
      setFamily(res.data)
      setName('')
      await refreshUser()
    } catch {
      /* 接口错误由 message 统一提示 */
    }
  }

  const onJoin = async (e) => {
    e.preventDefault()
    if (!inviteCode.trim()) {
      message.error('请输入邀请码')
      return
    }
    try {
      const res = await joinFamily({ inviteCode: inviteCode.trim().toUpperCase() })
      setFamily(res.data)
      setInviteCode('')
      await refreshUser()
    } catch {
      /* 接口错误由 message 统一提示 */
    }
  }

  const onLeave = async () => {
    if (!window.confirm('确定退出该家庭群？')) return
    try {
      await leaveFamily()
      setFamily(null)
      await refreshUser()
    } catch {
      /* 接口错误由 message 统一提示 */
    }
  }

  const onDissolve = async () => {
    if (!window.confirm('解散后成员和待办都会清空，确定吗？')) return
    try {
      await dissolveFamily()
      setFamily(null)
      await refreshUser()
    } catch {
      /* 接口错误由 message 统一提示 */
    }
  }

  const onRefreshCode = async () => {
    if (!window.confirm('刷新后旧邀请码立即失效，确定吗？')) return
    try {
      const res = await refreshInviteCode()
      setFamily(res.data)
      setCopied(false)
    } catch {
      /* 接口错误由 message 统一提示 */
    }
  }

  const copyCode = async () => {
    try {
      await navigator.clipboard.writeText(formatCode(family.inviteCode))
      setCopied(true)
      message.success('邀请码已复制')
      setTimeout(() => setCopied(false), 1500)
    } catch {
      message.error('复制失败，请手动复制邀请码')
    }
  }

  return (
    <div className="home">
      <Nav />
      <main className="shell">
        <header className="page-head">
          <h2>我们的家</h2>
          <p className="hint">门牌挂在门口，钥匙分给每一个回来的人。</p>
        </header>
        {loading ? <p className="hint">加载中...</p> : family ? (
          <>
            <section className="plaque">
              <p className="label">门牌</p>
              <h3>{family.name}</h3>
              <div className="key-row">
                <code>{formatCode(family.inviteCode)}</code>
                <button type="button" className="ghost" onClick={copyCode}>{copied ? '已复制' : '复制钥匙'}</button>
                {family.owner && <button type="button" className="ghost" onClick={onRefreshCode}>换一把</button>}
              </div>
            </section>
            <h3 className="subhead">家里的人</h3>
            <ul className="member-list">
              {family.members.map((m, i) => (
                <li key={m.id}>
                  <span className="avatar" style={{ background: AVATARS[i % AVATARS.length] }}>
                    {m.nickname.slice(0, 1)}
                  </span>
                  <div>
                    <strong>{m.nickname}</strong>
                    <small>{m.owner ? '当家' : m.username}</small>
                  </div>
                </li>
              ))}
            </ul>
            {family.owner
              ? <button type="button" className="danger" onClick={onDissolve}>解散这个家</button>
              : <button type="button" className="ghost" onClick={onLeave}>离开这个家</button>}
          </>
        ) : (
          <div className="split">
            <form onSubmit={onCreate}>
              <div className="illus door mini" aria-hidden="true"><span className="knob" /></div>
              <h3>安一个家</h3>
              <label>
                门牌上写什么
                <input value={name} onChange={(e) => setName(e.target.value)} placeholder="例如：老张家" />
              </label>
              <button type="submit">安家</button>
            </form>
            <form onSubmit={onJoin}>
              <div className="illus photo mini" aria-hidden="true" />
              <h3>走进这个家</h3>
              <label>
                家人给你的钥匙
                <input value={inviteCode} onChange={(e) => setInviteCode(e.target.value)} placeholder="12 位邀请码" />
              </label>
              <button type="submit">进门</button>
            </form>
          </div>
        )}
      </main>
    </div>
  )
}
