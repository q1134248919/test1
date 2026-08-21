import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchMyFamily } from '../api/family'
import { deleteMemory, fetchMemories, uploadMemory } from '../api/memory'
import Nav from '../components/Nav'
import { message } from '../utils/message'
import './Memory.css'

const ICONS = ['📷', '🌸', '🌊', '🌙', '☀️', '🍃', '🏠', '⭐']

export default function Memory() {
  const [family, setFamily] = useState(null)
  const [all, setAll] = useState([])
  const [loading, setLoading] = useState(true)
  const [preview, setPreview] = useState(null)
  const [draft, setDraft] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [uploadOpen, setUploadOpen] = useState(false)
  const [mode, setMode] = useState(() => localStorage.getItem('memory-mode') || 'polaroid')
  const inputRef = useRef(null)

  const switchMode = (next) => {
    setMode(next)
    localStorage.setItem('memory-mode', next)
  }

  const closeUpload = () => {
    if (draft?.url) URL.revokeObjectURL(draft.url)
    setDraft(null)
    setUploadOpen(false)
  }

  const load = async () => {
    try {
      const fam = await fetchMyFamily()
      setFamily(fam.data)
      if (fam.data) {
        const res = await fetchMemories()
        setAll(res.data || [])
      }
    } catch {
      /* message 统一提示 */
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])
  useEffect(() => () => {
    if (draft?.url) URL.revokeObjectURL(draft.url)
  }, [draft?.url])

  const onPick = (e) => {
    const file = e.target.files?.[0]
    e.target.value = ''
    if (!file) return
    if (!file.type.startsWith('image/')) {
      message.error('请选择图片')
      return
    }
    if (draft?.url) URL.revokeObjectURL(draft.url)
    setDraft({ file, url: URL.createObjectURL(file), caption: draft?.caption || '' })
  }

  const onUpload = async () => {
    if (!draft?.file) {
      message.error('请先选择图片')
      return
    }
    setUploading(true)
    try {
      const res = await uploadMemory(draft.file, (draft.caption || '').trim())
      setAll((list) => [res.data, ...list])
      closeUpload()
      message.success('已贴上照片墙')
    } catch {
      /* message 统一提示 */
    } finally {
      setUploading(false)
    }
  }

  const onDelete = async (e, id) => {
    e.stopPropagation()
    if (!window.confirm('从墙上取下这张照片？')) return
    try {
      await deleteMemory(id)
      setAll((list) => list.filter((m) => m.id !== id))
      setPreview(null)
    } catch {
      /* message 统一提示 */
    }
  }

  return (
    <div className="home memory-page">
      <Nav />
      {loading ? (
        <main className="memory-empty shell"><p>记忆加载中...</p></main>
      ) : !family ? (
        <main className="memory-empty shell">
          <p>请先到 <Link to="/family">家里</Link> 安一个家，再把照片贴上墙。</p>
        </main>
      ) : (
        <main className="memory-main shell">
          <div className="section-head">
            <header className="page-head">
              <h2>{mode === 'polaroid' ? '墙上的照片' : '相册抽屉'}</h2>
              <p className="hint">
                {mode === 'polaroid' ? '歪歪斜斜贴在软木板上' : '一张一张翻过去'}
              </p>
            </header>
            <div className="head-actions">
              <div className="mode-switch">
                <button type="button" className={mode === 'polaroid' ? 'on' : ''} onClick={() => switchMode('polaroid')}>拍立得</button>
                <button type="button" className={mode === 'masonry' ? 'on' : ''} onClick={() => switchMode('masonry')}>瀑布流</button>
              </div>
              <button type="button" className="upload-btn" onClick={() => setUploadOpen(true)}>贴一张照片</button>
            </div>
          </div>

          {all.length ? (
            mode === 'polaroid' ? (
              <section className="polaroid-wall">
                {all.map((memory, i) => (
                  <article key={memory.id} className="polaroid" onClick={() => setPreview(memory)}>
                    <img src={memory.url} alt="" />
                    <span className="caption">{ICONS[i % ICONS.length]} {memory.caption || memory.uploaderNickname}</span>
                  </article>
                ))}
              </section>
            ) : (
              <section className="masonry">
                {all.map((memory) => (
                  <article key={memory.id} className="masonry-item" onClick={() => setPreview(memory)}>
                    <img src={memory.url} alt="" />
                    <div className="overlay">
                      <strong>{memory.caption || memory.uploaderNickname}</strong>
                      <span>{memory.createTime}</span>
                    </div>
                  </article>
                ))}
              </section>
            )
          ) : (
            <section className="polaroid-wall empty-wall">
              还没有照片，点右上角贴上第一张
            </section>
          )}
        </main>
      )}

      {uploadOpen && (
        <div className="memory-modal" onClick={closeUpload}>
          <div className="upload-card" onClick={(e) => e.stopPropagation()}>
            <h3>贴上照片墙</h3>
            <button type="button" className="pick-zone" onClick={() => inputRef.current?.click()}>
              {draft?.url ? <img src={draft.url} alt="" /> : <span>点击选择图片</span>}
            </button>
            <input ref={inputRef} type="file" accept="image/*" hidden onChange={onPick} />
            <label>
              备注
              <input
                value={draft?.caption || ''}
                maxLength={50}
                onChange={(e) => setDraft((prev) => ({ file: null, url: '', ...prev, caption: e.target.value }))}
                placeholder="给这张照片起个名字"
              />
            </label>
            <small>{(draft?.caption || '').length}/50</small>
            <div className="upload-actions">
              <button type="button" style={{backgroundColor:"#eee",color:"#000"}}   onClick={closeUpload}>取消</button>
              <button type="button"  onClick={onUpload} disabled={uploading}>
                {uploading ? '上传中...' : '贴上去'}
              </button>
            </div>
          </div>
        </div>
      )}

      {preview && (
        <div className="memory-modal" onClick={() => setPreview(null)}>
          <figure className="preview-card" onClick={(e) => e.stopPropagation()}>
            <img src={preview.url} alt="" />
            <figcaption>
              <div>
                <strong>{preview.caption || '未写下备注'}</strong>
                <span>{preview.uploaderNickname} · {preview.createTime}</span>
              </div>
              <button type="button"   onClick={(e) => onDelete(e, preview.id)}>取下</button>
            </figcaption>
          </figure>
        </div>
      )}
    </div>
  )
}
