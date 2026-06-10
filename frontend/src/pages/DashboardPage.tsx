import { Link } from 'react-router-dom'
import { ErrorState, LoadingState } from '../components/AsyncStates'
import { LeaderboardTable } from '../components/LeaderboardTable'
import { useTopBatters } from '../hooks/useTopBatters'

export function DashboardPage() {
  const { data, loading, error, retry } = useTopBatters()
  const leader = data[0]

  return (
    <div className="space-y-10">
      <section className="relative overflow-hidden rounded-3xl bg-ink px-6 py-10 text-white sm:px-10 lg:px-14 lg:py-14">
        <div className="absolute -right-20 -top-24 h-72 w-72 rounded-full border-40 border-white/5" />
        <div className="absolute bottom-0 right-24 h-28 w-28 rounded-t-full bg-brand/45" />
        <div className="relative max-w-3xl">
          <span className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3 py-1 text-xs font-bold uppercase tracking-[0.16em] text-brand-soft">
            <span className="h-2 w-2 rounded-full bg-[#67d39b]" />
            Live analytics API
          </span>
          <h1 className="mt-6 text-4xl font-black leading-tight tracking-[-0.04em] sm:text-5xl lg:text-6xl">
            Cricket performance,
            <span className="block text-[#78d6a6]">made clear.</span>
          </h1>
          <p className="mt-5 max-w-2xl text-base leading-7 text-white/70 sm:text-lg">
            Search player profiles, explore batting output, and compare the leading run scorers from one focused dashboard.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link
              to="/players"
              className="rounded-xl bg-white px-5 py-3 text-sm font-black text-ink hover:bg-brand-soft"
            >
              Search players
            </Link>
            <Link
              to="/compare"
              className="rounded-xl border border-white/20 px-5 py-3 text-sm font-bold text-white hover:bg-white/10"
            >
              Compare players
            </Link>
            <Link
              to="/leaderboard"
              className="rounded-xl border border-white/20 px-5 py-3 text-sm font-bold text-white hover:bg-white/10"
            >
              View leaderboard
            </Link>
          </div>
        </div>
      </section>

      <section className="grid gap-4 md:grid-cols-3">
        <div className="rounded-2xl border border-line bg-white p-6 shadow-sm">
          <p className="text-xs font-bold uppercase tracking-[0.16em] text-muted">Dataset view</p>
          <p className="mt-3 text-3xl font-black tracking-tight text-ink">Top 20</p>
          <p className="mt-1 text-sm text-muted">Batters ranked by total runs</p>
        </div>
        <div className="rounded-2xl border border-line bg-white p-6 shadow-sm">
          <p className="text-xs font-bold uppercase tracking-[0.16em] text-muted">Current leader</p>
          <p className="mt-3 truncate text-2xl font-black tracking-tight text-ink">
            {leader?.playerName ?? 'Loading...'}
          </p>
          <p className="mt-1 text-sm text-muted">
            {leader ? `${leader.runs.toLocaleString()} total runs` : 'Fetching live data'}
          </p>
        </div>
        <div className="rounded-2xl border border-line bg-brand p-6 text-white shadow-sm">
          <p className="text-xs font-bold uppercase tracking-[0.16em] text-white/65">Available analysis</p>
          <p className="mt-3 text-3xl font-black tracking-tight">Profiles</p>
          <p className="mt-1 text-sm text-white/70">Matches, runs, balls, and strike rate</p>
        </div>
      </section>

      <section>
        <div className="mb-5 flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-brand">Live rankings</p>
            <h2 className="mt-2 text-3xl font-black tracking-tight text-ink">Leading batters</h2>
          </div>
          <Link to="/leaderboard" className="text-sm font-bold text-brand hover:text-brand-dark">
            View full leaderboard
          </Link>
        </div>

        {loading && <LoadingState label="Loading leaderboard..." />}
        {error && <ErrorState message={error} onRetry={retry} />}
        {!loading && !error && <LeaderboardTable players={data.slice(0, 5)} compact />}
      </section>
    </div>
  )
}
