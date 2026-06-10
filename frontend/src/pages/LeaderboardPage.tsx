import { ErrorState, LoadingState } from '../components/AsyncStates'
import { LeaderboardTable } from '../components/LeaderboardTable'
import { useTopBatters } from '../hooks/useTopBatters'

export function LeaderboardPage() {
  const { data, loading, error, retry } = useTopBatters()

  return (
    <div>
      <div className="mb-8 max-w-2xl">
        <p className="text-xs font-bold uppercase tracking-[0.18em] text-brand">Batting analytics</p>
        <h1 className="mt-3 text-4xl font-black tracking-[-0.035em] text-ink sm:text-5xl">
          Top batters leaderboard
        </h1>
        <p className="mt-4 text-base leading-7 text-muted">
          The leading run scorers in the current dataset, with total balls faced and calculated strike rate.
        </p>
      </div>

      {loading && <LoadingState label="Loading batting rankings..." />}
      {error && <ErrorState message={error} onRetry={retry} />}
      {!loading && !error && <LeaderboardTable players={data} />}
    </div>
  )
}
