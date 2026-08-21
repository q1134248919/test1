export default function AuthShell({ children, caption }) {
  return (
    <div className="auth-page">
      <section className="auth-scene" aria-hidden="true">
        <div className="scene-window">
          <span className="sheer left" />
          <span className="sheer right" />
        </div>
        <p className="scene-caption">{caption}</p>
      </section>
      <section className="auth-panel">{children}</section>
    </div>
  )
}
