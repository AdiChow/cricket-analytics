import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ErrorState, LoadingState } from '../components/AsyncStates'
import { getPlayerProfile } from '../lib/api'
import type { PlayerProfile } from '../types'

export function PlayerProfilePage() {
  const { playerId } = useParams()
  const [profile, setProfile] = useState<PlayerProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [requestId, setRequestId] = useState(0)

  useEffect(() => {
    const controller = new AbortController()

    async function load() {
      if (!playerId) {
        setError('Player ID is missing')
        setLoading(false)
        return
      }

      setLoading(true)
      setError(null)

      try {
        setProfile(await getPlayerProfile(playerId, controller.signal))
      } catch (requestError) {
        if (!controller.signal.aborted) {
          setError(
            requestError instanceof Error
              ? requestError.message
              : 'Unable to load player profile',
          )
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false)
        }
      }
    }

    void load()
    return () => controller.abort()
  }, [playerId, requestId])

  if (loading) {
    return <LoadingState label="Loading player profile..." />
  }

  if (error || !profile) {
    return (
      <div className="mx-auto max-w-3xl">
        <ErrorState message={error ?? 'Player profile is unavailable'} onRetry={() => setRequestId((value) => value + 1)} />
        <Link to="/players" className="mt-5 inline-block text-sm font-bold text-brand">
          Back to player search
        </Link>
      </div>
    )
  }

  const metrics = [
    { label: 'Matches', value: profile.matches.toLocaleString() },
    { label: 'Total runs', value: profile.runs.toLocaleString() },
    { label: 'Balls faced', value: profile.ballsFaced.toLocaleString() },
    { label: 'Strike rate', value: profile.strikeRate.toFixed(2) },
  ]

  return (
    <div className="mx-auto max-w-5xl">
      <Link to="/players" className="text-sm font-bold text-brand hover:text-brand-dark">
        &lt; Back to player search
      </Link>

      <section className="mt-5 overflow-hidden rounded-3xl border border-line bg-white shadow-sm">
        <div className="bg-ink px-6 py-9 text-white sm:px-10 sm:py-12">
          <div className="flex flex-col gap-5 sm:flex-row sm:items-center">
            <div className="grid h-20 w-20 shrink-0 place-items-center rounded-2xl bg-brand text-2xl font-black">
              {profile.playerName
                .split(' ')
                .map((part) => part[0])
                .join('')
                .slice(0, 2)}
            </div>
            <div>
              <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#78d6a6]">Player profile</p>
              <h1 className="mt-2 text-4xl font-black tracking-[-0.04em] sm:text-5xl">
                {profile.playerName}
              </h1>
              <p className="mt-2 text-sm text-white/60">Dataset player ID {profile.playerId}</p>
            </div>
          </div>
        </div>

        <div className="grid gap-px bg-line sm:grid-cols-2 lg:grid-cols-4">
          {metrics.map((metric) => (
            <div key={metric.label} className="bg-white p-6 sm:p-7">
              <p className="text-xs font-bold uppercase tracking-[0.16em] text-muted">{metric.label}</p>
              <p className="mt-3 text-3xl font-black tabular-nums tracking-tight text-ink">{metric.value}</p>
            </div>
          ))}
        </div>
      </section>

      <div className="mt-6 rounded-2xl border border-line bg-brand-soft/60 p-5 text-sm leading-6 text-brand-dark">
        Statistics are calculated by the backend analytics service from persisted match delivery data.
      </div>
    </div>
  )
}
