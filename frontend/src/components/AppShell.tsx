import { NavLink, Outlet } from 'react-router-dom'

const navigation = [
  { label: 'Dashboard', to: '/' },
  { label: 'Player Search', to: '/players' },
  { label: 'Compare Players', to: '/compare' },
  { label: 'Leaderboard', to: '/leaderboard' },
]

export function AppShell() {
  return (
    <div className="min-h-screen bg-canvas text-ink">
      <header className="border-b border-line bg-white/95">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-5 py-5 sm:px-8 lg:flex-row lg:items-center lg:justify-between lg:px-10">
          <NavLink to="/" className="flex items-center gap-3">
            <span className="grid h-10 w-10 place-items-center rounded-xl bg-brand text-sm font-black tracking-tight text-white">
              CI
            </span>
            <span>
              <span className="block text-sm font-bold tracking-wide text-ink">
                CRICKET INTELLIGENCE
              </span>
              <span className="block text-xs text-muted">Performance data platform</span>
            </span>
          </NavLink>

          <nav className="flex gap-1 overflow-x-auto rounded-xl bg-canvas p-1" aria-label="Primary navigation">
            {navigation.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                className={({ isActive }) =>
                  `whitespace-nowrap rounded-lg px-4 py-2 text-sm font-semibold transition-colors ${
                    isActive
                      ? 'bg-white text-brand shadow-sm'
                      : 'text-muted hover:text-ink'
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>
      </header>

      <main className="mx-auto w-full max-w-7xl px-5 py-8 sm:px-8 lg:px-10 lg:py-12">
        <Outlet />
      </main>

      <footer className="border-t border-line bg-white">
        <div className="mx-auto flex max-w-7xl flex-col gap-2 px-5 py-6 text-sm text-muted sm:px-8 md:flex-row md:items-center md:justify-between lg:px-10">
          <p>Cricket Intelligence Platform</p>
          <p>Spring Boot analytics API + React dashboard</p>
        </div>
      </footer>
    </div>
  )
}
