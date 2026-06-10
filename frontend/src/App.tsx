import { Navigate, Route, Routes } from 'react-router-dom'
import { AppShell } from './components/AppShell'
import { DashboardPage } from './pages/DashboardPage'
import { ComparePlayersPage } from './pages/ComparePlayersPage'
import { LeaderboardPage } from './pages/LeaderboardPage'
import { PlayerProfilePage } from './pages/PlayerProfilePage'
import { SearchPage } from './pages/SearchPage'

function App() {
  return (
    <Routes>
      <Route element={<AppShell />}>
        <Route index element={<DashboardPage />} />
        <Route path="players" element={<SearchPage />} />
        <Route path="players/:playerId" element={<PlayerProfilePage />} />
        <Route path="compare" element={<ComparePlayersPage />} />
        <Route path="leaderboard" element={<LeaderboardPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  )
}

export default App
