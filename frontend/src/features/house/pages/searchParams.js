function optionalText(value) {
  if (value == null) return undefined
  const trimmed = String(value).trim()
  return trimmed || undefined
}

function optionalNumber(value) {
  if (value == null || value === '') return undefined
  const numberValue = Number(value)
  return Number.isFinite(numberValue) ? numberValue : undefined
}

export function buildHouseSearchParams({ filters, sortKey, sortDir, page, size }) {
  return {
    regionCode: filters.regionCode,
    houseName: optionalText(filters.houseName),
    houseType: filters.buildingType || undefined,
    dealType: filters.transactionType || undefined,
    minAmount: optionalNumber(filters.minAmount),
    maxAmount: optionalNumber(filters.maxAmount),
    sortBy: sortKey,
    sortDir,
    page,
    size,
  }
}
