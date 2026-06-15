import { buildStructuredData } from '../seo/structuredData'

export function StructuredData() {
  const schemas = buildStructuredData()

  return (
    <>
      {schemas.map((schema) => (
        <script
          key={schema['@type'] as string}
          type="application/ld+json"
          dangerouslySetInnerHTML={{ __html: JSON.stringify(schema) }}
        />
      ))}
    </>
  )
}
