import { useEffect, useState } from 'react'
import {
  fetchGitHubReleases,
  getLatestRelease,
  type GitHubRelease,
} from '../lib/githubReleases'

export function useGitHubReleases() {
  const [releases, setReleases] = useState<GitHubRelease[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let active = true

    fetchGitHubReleases()
      .then((data) => {
        if (active) setReleases(data)
      })
      .catch((err: Error) => {
        if (active) setError(err.message)
      })
      .finally(() => {
        if (active) setLoading(false)
      })

    return () => {
      active = false
    }
  }, [])

  return {
    releases,
    latest: getLatestRelease(releases),
    loading,
    error,
  }
}
