import type {
  BattingLeader,
  PlayerComparison,
  PlayerProfile,
  PlayerSearchResult,
} from '../types'

const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL

if (!configuredBaseUrl) {
  throw new Error('VITE_API_BASE_URL is not configured')
}

export const API_BASE_URL = configuredBaseUrl.replace(/\/$/, '')

interface ApiErrorResponse {
  message?: string
}

async function request<T>(path: string, signal?: AbortSignal): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      Accept: 'application/json',
    },
    signal,
  })

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`

    try {
      const body = (await response.json()) as ApiErrorResponse
      if (body.message) {
        message = body.message
      }
    } catch {
      // Keep the status-based message when the response is not JSON.
    }

    throw new Error(message)
  }

  return response.json() as Promise<T>
}

export function getTopBatters(signal?: AbortSignal) {
  return request<BattingLeader[]>('/api/stats/top-batters', signal)
}

export function searchPlayers(query: string, signal?: AbortSignal) {
  const encodedQuery = encodeURIComponent(query.trim())
  return request<PlayerSearchResult[]>(
    `/api/stats/players/search?q=${encodedQuery}`,
    signal,
  )
}

export function getPlayerProfile(playerId: string, signal?: AbortSignal) {
  return request<PlayerProfile>(`/api/stats/players/${playerId}`, signal)
}

export function comparePlayers(
  player1Id: number,
  player2Id: number,
  signal?: AbortSignal,
) {
  const params = new URLSearchParams({
    player1Id: player1Id.toString(),
    player2Id: player2Id.toString(),
  })

  return request<PlayerComparison>(
    `/api/stats/players/compare?${params.toString()}`,
    signal,
  )
}
