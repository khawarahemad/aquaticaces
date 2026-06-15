# Aquatic Aces Website

React landing page for the Aquatic Aces Minecraft mod — features, downloads, install guide, commands, and FAQ.

## Quick Start

```bash
cd website
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173)

## Build for Production

```bash
npm run build
```

Output goes to `website/dist/`. Deploy that folder to any static host (Vercel, Netlify, GitHub Pages, etc.).

## Downloads

The mod jar is served from `public/downloads/aquaticaces-1.0.0.jar`.

After rebuilding the mod, refresh the website jar:

```bash
cp ../build/libs/aquaticaces-1.0.0.jar public/downloads/
```

## Design

The UI design system lives in Figma: [Aquatic Aces — Landing Page](https://www.figma.com/design/X9yZix9KMGPpcUh4HJoean)

Design tokens: `bg/primary`, `accent/cyan`, `accent/blue`, `text/primary`, etc.

## Stack

- React 19 + TypeScript
- Vite 8
- Outfit font (matches in-game UI)
