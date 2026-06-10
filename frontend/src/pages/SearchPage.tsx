import { type FormEvent, useEffect, useRef, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { ErrorState } from '../components/AsyncStates'
import { searchPlayers } from '../lib/api'
import type { PlayerSearchResult } from '../types'

export function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const initialQuery = searchParams.get('q') ?? ''
  const [query, setQuery] = useState(initialQuery)
  const [results, setResults] = useState<PlayerSearchResult[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [hasSearched, setHasSearched] = useState(false)
  const requestController = useRef<AbortController | null>(null)

  async function runSearch(searchQuery: string) {
    const trimmedQuery = searchQuery.trim()
    if (!trimmedQuery) {
      setError('Enter a player name to search.')
      return
    }

    requestController.current?.abort()
    const controller = new AbortController()
    requestController.current = controller

    setLoading(true)
    setError(null)
    setHasSearched(true)
    setSearchParams({ q: trimmedQuery })

    try {
      setResults(await searchPlayers(trimmedQuery, controller.signal))
    } catch (requestError) {
      if (!controller.signal.aborted) {
        setError(
          requestError instanceof Error
            ? requestError.message
            : 'Unable to search players',
        )
      }
    } finally {
      if (!controller.signal.aborted) {
        setLoading(false)
      }
    }
  }

  useEffect(() => {
    return () => requestController.current?.abort()
  }, [])

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    void runSearch(query)
  }

  return (
    <div className="mx-auto max-w-4xl">
      <div className="max-w-2xl">
        <p className="text-xs font-bold uppercase tracking-[0.18em] text-brand">Player directory</p>
        <h1 className="mt-3 text-4xl font-black tracking-[-0.035em] text-ink sm:text-5xl">
          Find a player
        </h1>
        <p className="mt-4 text-base leading-7 text-muted">
          Search by full name, surname, or initials, then open a player profile for batting statistics.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="mt-8 rounded-2xl border border-line bg-white p-3 shadow-sm sm:flex">
        <label htmlFor="player-search" className="sr-only">Player name</label>
        <input
          id="player-search"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Try Kohli, Root, or Smith"
          className="min-w-0 flex-1 rounded-xl border-0 bg-canvas px-4 py-3 text-base font-medium text-ink outline-none placeholder:text-muted/70 focus:ring-2 focus:ring-brand/30"
        />
        <button
          type="submit"
          disabled={loading}
          className="mt-3 w-full rounded-xl bg-brand px-6 py-3 text-sm font-black text-white hover:bg-brand-dark disabled:cursor-not-allowed disabled:opacity-60 sm:mt-0 sm:ml-3 sm:w-auto"
        >
          {loading ? 'Searching...' : 'Search players'}
        </button>
      </form>

      <div className="mt-8">
        {error && <ErrorState message={error} />}

        {!error && hasSearched && !loading && results.length === 0 && (
          <div className="rounded-2xl border border-line bg-white p-8 text-center">
            <p className="font-bold text-ink">No matching players found</p>
            <p className="mt-1 text-sm text-muted">Try a shorter name or different spelling.</p>
          </div>
        )}

        {!error && results.length > 0 && (
          <div>
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-xl font-black text-ink">Search results</h2>
              <span className="text-sm font-semibold text-muted">
                {results.length} {results.length === 1 ? 'player' : 'players'}
              </span>
            </div>
            <div className="grid gap-3 sm:grid-cols-2">
              {results.map((player) => (
                <Link
                  key={player.playerId}
                  to={`/players/${player.playerId}`}
                  className="group flex items-center justify-between rounded-2xl border border-line bg-white p-5 shadow-sm hover:border-brand/40"
                >
                  <span>
                    <span className="block font-black text-ink group-hover:text-brand">
                      {player.playerName}
                    </span>
                    <span className="mt-1 block text-xs font-semibold uppercase tracking-wider text-muted">
                      Player ID {player.playerId}
                    </span>
                  </span>
                  <span className="grid h-9 w-9 place-items-center rounded-full bg-brand-soft text-lg font-bold text-brand">
                    &gt;
                  </span>
                </Link>
              ))}
            </div>
          </div>
        )}

        {!hasSearched && (
          <div className="grid gap-4 sm:grid-cols-3">
            {['Search the dataset', 'Open a profile', 'Review batting output'].map((label, index) => (
              <div key={label} className="rounded-2xl border border-line bg-white p-5">
                <span className="text-xs font-black text-brand">0{index + 1}</span>
                <p className="mt-3 text-sm font-bold text-ink">{label}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
