import test from 'node:test'
import assert from 'node:assert/strict'

import { buildHouseSearchParams } from './searchParams.js'

test('buildHouseSearchParams includes detailed filter fields when provided', () => {
  const params = buildHouseSearchParams({
    filters: {
      regionCode: '1168010100',
      houseName: '래미안',
      buildingType: '아파트',
      transactionType: '매매',
      minAmount: '10000',
      maxAmount: '30000',
    },
    sortKey: 'price',
    sortDir: 'asc',
    page: 2,
    size: 10,
  })

  assert.deepEqual(params, {
    regionCode: '1168010100',
    houseName: '래미안',
    houseType: '아파트',
    dealType: '매매',
    minAmount: 10000,
    maxAmount: 30000,
    sortBy: 'price',
    sortDir: 'asc',
    page: 2,
    size: 10,
  })
})

test('buildHouseSearchParams omits blank optional detailed filters', () => {
  const params = buildHouseSearchParams({
    filters: {
      regionCode: '1168010100',
      houseName: ' ',
      buildingType: '',
      transactionType: '',
      minAmount: '',
      maxAmount: null,
    },
    sortKey: 'date',
    sortDir: 'desc',
    page: 1,
    size: 10,
  })

  assert.deepEqual(params, {
    regionCode: '1168010100',
    houseName: undefined,
    houseType: undefined,
    dealType: undefined,
    minAmount: undefined,
    maxAmount: undefined,
    sortBy: 'date',
    sortDir: 'desc',
    page: 1,
    size: 10,
  })
})

test('buildHouseSearchParams passes recommendation sort key', () => {
  const params = buildHouseSearchParams({
    filters: {
      regionCode: '1168010100',
      houseName: '',
      buildingType: '아파트',
      transactionType: '매매',
      minAmount: '160000',
      maxAmount: '196000',
    },
    sortKey: 'recommend',
    sortDir: 'desc',
    page: 1,
    size: 10,
  })

  assert.equal(params.sortBy, 'recommend')
  assert.equal(params.sortDir, 'desc')
})
