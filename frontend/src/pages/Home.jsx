import { Link } from 'react-router-dom'
import Nav from '../components/Nav'
import { getUser } from '../auth'

export default function Home() {
  const user = getUser()
  const inFamily = Boolean(user?.familyId)

  return (
    <div className="home">
      <Nav />
      <main className="shell">
        <section className="hero">
          <div className="hero-copy">
            <p className="welcome-kicker">灯还亮着</p>
            <h2>欢迎回家，{user?.nickname}</h2>
            <p className="hint">
              {inFamily ? '把今天的小事放进这个家里。' : '先安一个家，灯才真正亮起来。'}
            </p>
          </div>
          <div className="hero-window" aria-hidden="true">
            <span className="mv" />
            <span className="mh" />
          </div>
        </section>
        <div className="cards">
          <Link className="mini-card" to="/family">
            <div className="illus door" aria-hidden="true">
              <span className="knob" />
            </div>
            <strong>{inFamily ? '我们的家' : '安一个家'}</strong>
            <small>{inFamily ? '门牌和钥匙都在这儿' : '创建或走入一个家'}</small>
          </Link>
          <Link className="mini-card" to="/todos">
            <div className="illus note" aria-hidden="true">
              <span /><span /><span />
            </div>
            <strong>家里的事</strong>
            <small>贴在冰箱上的那几张纸</small>
          </Link>
          <Link className="mini-card" to="/memories">
            <div className="illus photo" aria-hidden="true" />
            <strong>墙上的照片</strong>
            <small>随手贴上客厅那面墙</small>
          </Link>
        </div>
      </main>
    </div>
  )
}
