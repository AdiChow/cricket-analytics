import { Link } from 'react-router-dom'
import type { BattingLeader } from '../types'

interface LeaderboardTableProps {
  players: BattingLeader[]
  compact?: boolean
}

export function LeaderboardTable({ players, compact = false }: LeaderboardTableProps) {
  return (
    <div className="overflow-hidden rounded-2xl border border-line bg-white shadow-sm">
      <div className="overflow-x-auto">
        <table className="w-full min-w-160 border-collapse text-left">
          <thead>
            <tr className="border-b border-line bg-[#f8faf7] text-xs font-bold uppercase tracking-[0.16em] text-muted">
              <th className="px-5 py-4">Rank</th>
              <th className="px-5 py-4">Player</th>
              <th className="px-5 py-4 text-right">Runs</th>
              <th className="px-5 py-4 text-right">Balls</th>
              <th className="px-5 py-4 text-right">Strike rate</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-line">
            {players.map((player, index) => (
              <tr key={player.playerId} className="group hover:bg-brand-soft/35">
                <td className="px-5 py-4">
                  <span
                    className={`grid h-8 w-8 place-items-center rounded-lg text-sm font-black ${
                      index < 3 ? 'bg-brand text-white' : 'bg-canvas text-muted'
                    }`}
                  >
                    {index + 1}
                  </span>
                </td>
                <td className="px-5 py-4">
                  <Link
                    to={`/players/${player.playerId}`}
                    className="font-bold text-ink group-hover:text-brand"
                  >
                    {player.playerName}
                  </Link>
                  {!compact && (
                    <span className="mt-0.5 block text-xs text-muted">
                      Player ID {player.playerId}
                    </span>
                  )}
                </td>
                <td className="px-5 py-4 text-right font-black tabular-nums text-ink">
                  {player.runs.toLocaleString()}
                </td>
                <td className="px-5 py-4 text-right tabular-nums text-muted">
                  {player.ballsFaced.toLocaleString()}
                </td>
                <td className="px-5 py-4 text-right font-semibold tabular-nums text-brand-dark">
                  {player.strikeRate.toFixed(2)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
