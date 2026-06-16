import { MOD } from '../data/modData'
import { MODULE_CATEGORIES } from '../data/modData'
import { SITE, SITE_URL, absoluteUrl, assetAbsoluteUrl } from '../seo/siteConfig'

function moduleKeywordList() {
  return MODULE_CATEGORIES.flatMap((category) => category.modules).slice(0, 24).join(', ')
}

export function buildStructuredData() {
  const moduleSummary = MODULE_CATEGORIES.map((c) => `${c.name} (${c.count})`).join(', ')

  return [
    {
      '@context': 'https://schema.org',
      '@type': 'WebSite',
      name: SITE.name,
      url: SITE_URL,
      description: SITE.description,
      inLanguage: 'en-US',
      publisher: {
        '@type': 'Organization',
        name: SITE.author,
        url: MOD.repoUrl,
        sameAs: [MOD.repoUrl, MOD.discordUrl],
      },
    },
    {
      '@context': 'https://schema.org',
      '@type': 'SoftwareApplication',
      name: SITE.name,
      alternateName: ['AquaticAces', 'aquaticaces'],
      applicationCategory: 'GameApplication',
      applicationSubCategory: 'Minecraft Fabric Client Mod',
      operatingSystem: 'Windows, macOS, Linux',
      softwareVersion: MOD.version,
      description: SITE.description,
      url: SITE_URL,
      downloadUrl: SITE.releaseUrl,
      installUrl: absoluteUrl('#install'),
      license: `https://opensource.org/licenses/${MOD.license}`,
      author: {
        '@type': 'Organization',
        name: SITE.author,
        url: MOD.repoUrl,
        sameAs: [MOD.repoUrl, MOD.discordUrl],
      },
      offers: {
        '@type': 'Offer',
        price: '0',
        priceCurrency: 'USD',
        availability: 'https://schema.org/InStock',
        url: SITE.releaseUrl,
      },
      featureList: [
        'NanoVG ClickGUI with live search',
        '60+ modules across 8 categories',
        'Drag-and-drop HUD editor',
        'Config profiles with JSON export',
        'Schematic preview and symmetry brush',
        'Dot-prefix chat command system',
        'Friends, alts, and waypoints',
      ],
      keywords: `${SITE.keywords.join(', ')}, ${moduleKeywordList()}`,
      screenshot: assetAbsoluteUrl('og-image.svg'),
      requirements: `Minecraft ${MOD.minecraft}, Fabric Loader ${MOD.loader}+, Fabric API ${MOD.fabricApi}, Fabric Language Kotlin ${MOD.fabricKotlin}, Java 21`,
      releaseNotes: SITE.releaseUrl,
      additionalProperty: [
        { '@type': 'PropertyValue', name: 'Minecraft Version', value: MOD.minecraft },
        { '@type': 'PropertyValue', name: 'Mod Loader', value: 'Fabric' },
        { '@type': 'PropertyValue', name: 'Module Categories', value: moduleSummary },
        { '@type': 'PropertyValue', name: 'Default Keybind', value: MOD.defaultKeybind },
      ],
    },
    {
      '@context': 'https://schema.org',
      '@type': 'FAQPage',
      mainEntity: [
        {
          '@type': 'Question',
          name: 'What Minecraft version does Aquatic Aces support?',
          acceptedAnswer: {
            '@type': 'Answer',
            text: `Aquatic Aces supports Minecraft ${MOD.minecraft} with Fabric Loader ${MOD.loader}+, Fabric API, and Fabric Language Kotlin.`,
          },
        },
        {
          '@type': 'Question',
          name: 'How do I install Aquatic Aces?',
          acceptedAnswer: {
            '@type': 'Answer',
            text: 'Install Java 21, Fabric for Minecraft 1.21, Fabric API, Fabric Language Kotlin, then place aquaticaces.jar in your .minecraft/mods folder.',
          },
        },
        {
          '@type': 'Question',
          name: 'Where can I download Aquatic Aces?',
          acceptedAnswer: {
            '@type': 'Answer',
            text: `Download the latest jar from ${SITE.releaseUrl} or the official website at ${SITE_URL}.`,
          },
        },
      ],
    },
  ]
}
