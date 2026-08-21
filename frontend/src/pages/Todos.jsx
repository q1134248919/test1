import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchMyFamily } from '../api/family'
import { addTodo, deleteTodo, fetchTodos, toggleTodo } from '../api/todo'
import Nav from '../components/Nav'
import { message } from '../utils/message'

export default function Todos() {
  const [family, setFamily] = useState(null)
  const [todos, setTodos] = useState([])
  const [title, setTitle] = useState('')
  const [loading, setLoading] = useState(true)

  const load = async () => {
    try {
      const fam = await fetchMyFamily()
      setFamily(fam.data)
      if (fam.data) {
        const res = await fetchTodos()
        setTodos(res.data || [])
      }
    } catch {
      /* 接口错误由 message 统一提示 */
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const onAdd = async (e) => {
    e.preventDefault()
    if (!title.trim()) {
      message.error('请输入待办内容')
      return
    }
    try {
      const res = await addTodo({ title: title.trim() })
      setTodos((list) => [res.data, ...list])
      setTitle('')
    } catch {
      /* 接口错误由 message 统一提示 */
    }
  }

  const onToggle = async (id) => {
    try {
      const res = await toggleTodo(id)
      setTodos((list) => list.map((t) => (t.id === id ? res.data : t)))
    } catch {
      /* 接口错误由 message 统一提示 */
    }
  }

  const onDelete = async (id) => {
    try {
      await deleteTodo(id)
      setTodos((list) => list.filter((t) => t.id !== id))
    } catch {
      /* 接口错误由 message 统一提示 */
    }
  }

  return (
    <div className="home">
      <Nav />
      <main className="shell">
        <header className="page-head">
          <h2>家里的事</h2>
          {family && <p className="hint">{family.name} · 谁看见谁勾</p>}
        </header>
        {loading ? <p className="hint">加载中...</p> : !family ? (
          <p className="hint">请先到 <Link to="/family">家里</Link> 安一个家。</p>
        ) : (
          <div className="fridge-wrap">
            <form className="todo-form" onSubmit={onAdd}>
              <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="今晚买菜、交电费、给爸妈打个电话" />
              <button type="submit">贴上去</button>
            </form>
            <ul className="todo-list">
              {todos.length === 0 && <li className="empty">冰箱上还是空的</li>}
              {todos.map((t, i) => (
                <li key={t.id} className={`sticky s${i % 5} ${t.done ? 'done' : ''}`}>
                  <label>
                    <input type="checkbox" checked={t.done} onChange={() => onToggle(t.id)} />
                    <span>{t.title}</span>
                  </label>
                  <small>{t.creatorNickname}</small>
                  <button type="button" className="ghost" onClick={() => onDelete(t.id)}>撕掉</button>
                </li>
              ))}
            </ul>
          </div>
        )}
      </main>
    </div>
  )
}
