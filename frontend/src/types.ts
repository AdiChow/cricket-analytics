export interface PlayerSearchResult {
  playerId: number
  playerName: string
}

export interface PlayerProfile {
  playerId: number
  playerName: string
  matches: number
  runs: number
  ballsFaced: number
  strikeRate: number
}

export interface BattingLeader {
  playerId: number
  playerName: string
  runs: number
  ballsFaced: number
  strikeRate: number
}
