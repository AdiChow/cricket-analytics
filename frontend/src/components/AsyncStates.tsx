interface LoadingStateProps {
  label?: string
}

export function LoadingState({ label = 'Loading data...' }: LoadingStateProps) {
  return (
    <div className="flex min-h-56 items-center justify-center rounded-2xl border border-line bg-white p-8">
      <div className="flex items-center gap-3 text-sm font-semibold text-muted">
        <span className="h-3 w-3 animate-pulse rounded-full bg-brand" />
        {label}
      </div>
    </div>
  )
}

interface ErrorStateProps {
  message: string
  onRetry?: () => void
}

export function ErrorState({ message, onRetry }: ErrorStateProps) {
  return (
    <div className="rounded-2xl border border-red-200 bg-red-50 p-6">
      <p className="text-sm font-bold text-red-800">Unable to load data</p>
      <p className="mt-1 text-sm text-red-700">{message}</p>
      {onRetry && (
        <button
          type="button"
          onClick={onRetry}
          className="mt-4 rounded-lg bg-red-700 px-4 py-2 text-sm font-bold text-white hover:bg-red-800"
        >
          Try again
        </button>
      )}
    </div>
  )
}
