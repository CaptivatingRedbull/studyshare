import './App.css'
import { ThemeProvider } from './components/theme-provider.tsx'
import Page from './dashboard.tsx'

function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <Page />
    </ThemeProvider>
  )
}

export default App


