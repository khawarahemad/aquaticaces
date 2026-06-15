export type GitHubReleaseAsset = {
  name: string
  browser_download_url: string
  size: number
  content_type: string
}

export type GitHubRelease = {
  id: number
  tag_name: string
  name: string
  body: string
  published_at: string
  html_url: string
  prerelease: boolean
  draft: boolean
  assets: GitHubReleaseAsset[]
}

const RELEASES_API = 'https://api.github.com/repos/khawarahemad/aquaticaces/releases'

let cachedReleases: GitHubRelease[] | null = null
let fetchPromise: Promise<GitHubRelease[]> | null = null

export function formatReleaseDate(iso: string) {
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(new Date(iso))
}

export function formatFileSize(bytes: number) {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${Math.round(bytes / 1024)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

export function getJarAsset(release: GitHubRelease) {
  return release.assets.find((asset) => asset.name.endsWith('.jar')) ?? null
}

export function getReleaseVersion(release: GitHubRelease) {
  return release.tag_name.replace(/^v/i, '')
}

export async function fetchGitHubReleases(): Promise<GitHubRelease[]> {
  if (cachedReleases) return cachedReleases
  if (fetchPromise) return fetchPromise

  fetchPromise = fetch(`${RELEASES_API}?per_page=20`, {
    headers: { Accept: 'application/vnd.github+json' },
  })
    .then(async (res) => {
      if (!res.ok) throw new Error(`GitHub API error (${res.status})`)
      const data = (await res.json()) as GitHubRelease[]
      cachedReleases = data.filter((release) => !release.draft)
      return cachedReleases
    })
    .catch((error) => {
      fetchPromise = null
      throw error
    })

  return fetchPromise
}

export function getLatestRelease(releases: GitHubRelease[]) {
  return releases.find((release) => !release.prerelease) ?? releases[0] ?? null
}

export function extractReleaseHighlights(body: string, max = 4): string[] {
  const lines = body.split('\n')
  const highlights: string[] = []
  let inWhatsNew = false

  for (const line of lines) {
    if (/^##\s+what'?s new/i.test(line.trim())) {
      inWhatsNew = true
      continue
    }
    if (/^##\s+/.test(line.trim()) && inWhatsNew) break
    if (!inWhatsNew) continue

    const boldMatch = line.match(/^[-*]\s+\*\*(.+?)\*\*/)
    if (boldMatch) {
      highlights.push(boldMatch[1].trim())
    } else if (/^[-*]\s+/.test(line.trim())) {
      highlights.push(
        line
          .replace(/^[-*]\s+/, '')
          .replace(/\*\*/g, '')
          .split(/[—–-]/)[0]
          .trim(),
      )
    }

    if (highlights.length >= max) break
  }

  return highlights
}

export function getReleasePreview(body: string) {
  const highlights = extractReleaseHighlights(body, 1)
  if (highlights[0]) return highlights[0]

  const paragraph = body
    .split('\n')
    .map((line) => line.trim())
    .find((line) => line && !line.startsWith('#') && !line.startsWith('-') && !line.startsWith('*'))

  return paragraph?.slice(0, 120) ?? 'View release notes on GitHub.'
}
