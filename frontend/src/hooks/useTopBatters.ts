import { useCallback, useEffect, useState } from 'react'
import { getTopBatters } from '../lib/api'
import type { BattingLeader } from '../types'

export function useTopBatters() {
  const [data, setData] = useState<BattingLeader[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [requestId, setRequestId] = useState(0)

  const retry = useCallback(() => {
    setRequestId((current) => current + 1)
  }, [])

  useEffect(() => {
    const controller = new AbortController()

    async function load() {
      setLoading(true)
      setError(null)

      try {
        setData(await getTopBatters(controller.signal))
      } catch (requestError) {
        if (!controller.signal.aborted) {
          setError(
            requestError instanceof Error
              ? requestError.message
              : 'Unable to load batting data',
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
  }, [requestId])

  return { data, loading, error, retry }
}
