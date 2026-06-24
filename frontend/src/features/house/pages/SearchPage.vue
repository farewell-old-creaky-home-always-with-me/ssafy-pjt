<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { X, SlidersHorizontal, ChevronsUpDown, ChevronUp, ChevronDown,
  ChevronsLeft, ChevronsRight, ChevronLeft, ChevronRight, Heart,
  Building2, Handshake, Calendar, MapPin, Hash, Landmark, Maximize2, Layers } from 'lucide-vue-next'
import { useFavoritesStore } from '@/stores/favoritesStore.js'
import { useAuthStore } from '@/stores/authStore.js'
import { housesApi, regionsApi } from '@/api/index.js'
import BaseButton from '@/components/base/BaseButton.vue'
import { escapeHtml } from '@/utils/html.js'

const route = useRoute()
const favoritesStore = useFavoritesStore()
const authStore = useAuthStore()

const TYPE_MAP = {
  'apt-sale':   { buildingType: '아파트', transactionType: '매매' },
  'apt-rent':   { buildingType: '아파트', transactionType: '전월세' },
  'multi-sale': { buildingType: '다세대', transactionType: '매매' },
  'multi-rent': { buildingType: '다세대', transactionType: '전월세' },
}

const PAGE_SIZE = 10
const WINDOW_SIZE = 5

const regions = ref([])
const pageData = ref([])
const totalItems = ref(0)
const loadError = ref(false)
const loading = ref(false)
const sidebarOpen = ref(false)
const selectedHouseId = ref(null)
const modalItem = ref(null)
const currentPage = ref(1)
const sortKey = ref('date')
const sortDir = ref('desc')

const filters = ref({ regionCode: '', houseName: '', buildingType: '아파트', transactionType: '매매' })

function formatManwon(manwon) {
  if (manwon == null) return '-'
  const eok = Math.floor(manwon / 10000)
  const rest = manwon % 10000
  if (eok === 0) return rest.toLocaleString() + '만'
  if (rest === 0) return eok + '억'
  return eok + '억 ' + rest.toLocaleString() + '만'
}

function formatDealPrice(deal) {
  if (!deal) return '-'
  if (deal.dealType === '매매') return formatManwon(deal.dealAmount)
  const parts = []
  if (deal.depositAmount) parts.push('보 ' + formatManwon(deal.depositAmount))
  if (deal.monthlyRent) parts.push('월 ' + formatManwon(deal.monthlyRent))
  return parts.join(' / ') || '-'
}

async function fetchHouses() {
  if (!filters.value.regionCode) return
  loading.value = true
  loadError.value = false
  try {
    const res = await housesApi.searchHouses({
      regionCode: filters.value.regionCode,
      houseName: filters.value.houseName.trim() || undefined,
      houseType: filters.value.buildingType || undefined,
      dealType: filters.value.transactionType || undefined,
      sortBy: sortKey.value,
      sortDir: sortDir.value,
      page: currentPage.value,
      size: PAGE_SIZE,
    })
    pageData.value = res.items
    totalItems.value = res.total
    renderMapMarkers()
  } catch {
    loadError.value = true
    pageData.value = []
    totalItems.value = 0
    renderMapMarkers()
  } finally {
    loading.value = false
  }
}

const totalPages = computed(() => Math.max(1, Math.ceil(totalItems.value / PAGE_SIZE)))
const resultRange = computed(() => {
  if (!totalItems.value) return ''
  const start = (currentPage.value - 1) * PAGE_SIZE + 1
  const end = Math.min(currentPage.value * PAGE_SIZE, totalItems.value)
  return `${start}–${end} / ${totalItems.value}`
})
const pageRange = computed(() => {
  const start = Math.max(1, currentPage.value - Math.floor(WINDOW_SIZE / 2))
  const end = Math.min(totalPages.value, start + WINDOW_SIZE - 1)
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

function setSort(key) {
  if (sortKey.value === key) sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  else { sortKey.value = key; sortDir.value = 'asc' }
  currentPage.value = 1
  fetchHouses()
}

function goPage(n) {
  if (n < 1 || n > totalPages.value) return
  currentPage.value = n
  fetchHouses()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function applyFilter() { currentPage.value = 1; sidebarOpen.value = false; fetchHouses() }
function resetFilter() {
  filters.value = { regionCode: '', houseName: '', buildingType: '아파트', transactionType: '매매' }
  currentPage.value = 1
  pageData.value = []
  totalItems.value = 0
  clearMarkers()
}

const modalInfoItems = computed(() => {
  if (!modalItem.value) return []
  const item = modalItem.value
  return [
    { icon: Building2, label: '건물유형', value: item.houseType },
    { icon: Handshake, label: '거래유형', value: item.latestDeal?.dealType ?? '-' },
    { icon: Calendar, label: '건축연도', value: item.buildYear ? item.buildYear + '년' : '-' },
    { icon: MapPin, label: '동', value: item.dongName },
    { icon: Hash, label: '지번', value: item.jibun },
    { icon: Landmark, label: '지역코드', value: item.regionCode },
    { icon: Maximize2, label: '전용면적', value: item.latestDeal?.area != null ? item.latestDeal.area.toFixed(2) + ' ㎡' : '-' },
    { icon: Layers, label: '층', value: item.latestDeal?.floor != null ? item.latestDeal.floor + '층' : '-' },
  ]
})

const isFavItem = computed(() => {
  if (!modalItem.value) return false
  return favoritesStore.items.some(f => f.regionCode === modalItem.value.regionCode)
})

function openModal(item) {
  selectedHouseId.value = item.houseId
  modalItem.value = item
  focusMapItem(item)
}
function closeModal() { modalItem.value = null; selectedHouseId.value = null }

async function toggleFavorite() {
  if (!authStore.isLoggedIn()) { alert('로그인이 필요합니다'); return }
  if (!modalItem.value) return
  if (isFavItem.value) {
    const fav = favoritesStore.items.find(f => f.regionCode === modalItem.value.regionCode)
    if (fav) await favoritesStore.removeFavorite(fav.favoriteId)
  } else {
    await favoritesStore.addFavorite(modalItem.value.regionCode)
  }
}

let map
let infowindow
let markers = []

function clearMarkers() {
  markers.forEach(({ marker }) => marker.setMap(null))
  markers = []
  if (infowindow) infowindow.close()
}

function getPosition(item) {
  if (item.latitude == null || item.longitude == null || typeof window.kakao === 'undefined') return null
  const latitude = Number(item.latitude)
  const longitude = Number(item.longitude)
  if (Number.isNaN(latitude) || Number.isNaN(longitude)) return null
  return new window.kakao.maps.LatLng(latitude, longitude)
}

function showMarkerInfo(item) {
  if (!map || !infowindow) return
  const entry = markers.find(({ item: markerItem }) => markerItem.houseId === item.houseId)
  if (!entry) return
  infowindow.setContent(
    `<div style="padding:8px 10px;font-size:12px;font-weight:600;white-space:nowrap;">${escapeHtml(item.aptName)}</div>`
  )
  infowindow.open(map, entry.marker)
}

function focusMapItem(item) {
  if (!map) return
  const position = getPosition(item)
  if (!position) return
  map.panTo(position)
  showMarkerInfo(item)
}

function renderMapMarkers() {
  if (!map || typeof window.kakao === 'undefined') return
  clearMarkers()

  const bounds = new window.kakao.maps.LatLngBounds()
  let hasMarker = false

  pageData.value.forEach((item) => {
    const position = getPosition(item)
    if (!position) return

    const marker = new window.kakao.maps.Marker({
      map,
      position,
      title: item.aptName,
    })
    window.kakao.maps.event.addListener(marker, 'click', () => openModal(item))
    markers.push({ marker, item })
    bounds.extend(position)
    hasMarker = true
  })

  if (hasMarker) map.setBounds(bounds)
}

function initMap() {
  const container = document.getElementById('map')
  if (!container) return
  map = new window.kakao.maps.Map(container, {
    center: new window.kakao.maps.LatLng(37.5665, 126.9780),
    level: 5,
  })
  infowindow = new window.kakao.maps.InfoWindow({ zIndex: 1 })
  renderMapMarkers()
}

onMounted(async () => {
  try {
    regions.value = await regionsApi.getRegions()
  } catch {
    // regions stays [], rest of initialization continues
  }

  const { dong, type } = route.query
  if (dong) {
    const matches = regions.value.filter(r => r.dongName === dong)
    if (matches.length === 1) filters.value.regionCode = matches[0].regionCode
  }
  if (type && TYPE_MAP[type]) Object.assign(filters.value, TYPE_MAP[type])

  if (filters.value.regionCode) await fetchHouses()

  if (typeof window.kakao !== 'undefined') {
    window.kakao.maps.load(initMap)
  }
})
</script>

<template>
  <div class="bg-bg-page min-h-[calc(100vh-64px)] py-6">
    <div class="max-w-[80rem] mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex flex-col lg:flex-row gap-6">

        <!-- Filter Sidebar -->
        <aside
          class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] overflow-hidden lg:w-[35%] lg:max-w-[320px] lg:shrink-0 lg:sticky lg:top-[88px] lg:self-start"
          :class="{ 'hidden lg:block': !sidebarOpen }"
        >
          <div class="bg-navy px-5 py-4 flex items-center justify-between">
            <h2 class="text-white text-base font-semibold m-0">필터</h2>
            <button class="lg:hidden w-8 h-8 flex items-center justify-center rounded-lg text-white/70 hover:text-white transition-colors" @click="sidebarOpen = false"><X :size="16" /></button>
          </div>
          <div class="p-5 flex flex-col gap-5">
            <div>
              <label class="flex items-center gap-1.5 text-navy text-[0.8125rem] font-semibold mb-2">법정동</label>
              <select v-model="filters.regionCode"
                class="w-full px-3 py-2.5 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-[0.8125rem] outline-none cursor-pointer appearance-none transition-colors focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]">
                <option value="">지역을 선택하세요</option>
                <option v-for="r in regions" :key="r.regionCode" :value="r.regionCode">
                  {{ r.sidoName }} {{ r.sigunguName }} {{ r.dongName }}
                </option>
              </select>
            </div>
            <div>
              <label class="flex items-center gap-1.5 text-navy text-[0.8125rem] font-semibold mb-2">아파트명</label>
              <input
                v-model="filters.houseName"
                type="search"
                placeholder="아파트명을 입력하세요"
                class="w-full px-3 py-2.5 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-[0.8125rem] outline-none transition-colors focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
                @keyup.enter="applyFilter"
              />
            </div>
            <div>
              <label class="flex items-center gap-1.5 text-navy text-[0.8125rem] font-semibold mb-2">건물유형</label>
              <div class="flex gap-2">
                <button
                  v-for="t in ['아파트', '다세대']" :key="t"
                  class="flex-1 py-2.5 rounded-xl border border-[#e5e7eb] bg-bg-page text-gray-500 text-[0.8125rem] cursor-pointer transition-all hover:border-blue/50"
                  :class="{ 'bg-blue border-blue text-white font-semibold': filters.buildingType === t }"
                  @click="filters.buildingType = t"
                >{{ t }}</button>
              </div>
            </div>
            <div>
              <label class="flex items-center gap-1.5 text-navy text-[0.8125rem] font-semibold mb-2">거래유형</label>
              <div class="flex gap-2">
                <button
                  v-for="t in ['매매', '전월세']" :key="t"
                  class="flex-1 py-2.5 rounded-xl border border-[#e5e7eb] bg-bg-page text-gray-500 text-[0.8125rem] cursor-pointer transition-all hover:border-blue/50"
                  :class="{ 'bg-navy border-navy text-white font-semibold': filters.transactionType === t }"
                  @click="filters.transactionType = t"
                >{{ t }}</button>
              </div>
            </div>
            <div class="flex gap-2 mt-4">
              <BaseButton size="sm" :full="true" :disabled="!filters.regionCode" @click="applyFilter">적용</BaseButton>
              <BaseButton size="sm" variant="ghost" :full="true" @click="resetFilter">초기화</BaseButton>
            </div>
          </div>
        </aside>

        <!-- Main Content -->
        <main class="flex-1 min-w-0 flex flex-col gap-6">
          <div class="flex items-center gap-3">
            <button
              class="lg:hidden flex items-center gap-1.5 px-4 py-2 rounded-xl bg-navy text-white text-[0.8125rem] font-medium"
              @click="sidebarOpen = !sidebarOpen"
            >
              <SlidersHorizontal :size="16" /> 필터
            </button>
            <span class="text-navy text-sm font-medium">총 <strong>{{ totalItems }}</strong>건</span>
            <span class="text-gray-400 text-xs">{{ resultRange }}</span>
          </div>

          <div>
            <div id="map" style="width:100%;height:300px"></div>
          </div>

          <div class="bg-white rounded-2xl border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] overflow-hidden">
            <div class="overflow-x-auto">
              <table class="w-full min-w-[600px]">
                <thead>
                  <tr class="bg-bg-page">
                    <th class="px-4 py-3 text-xs font-semibold text-navy cursor-pointer select-none transition-colors whitespace-nowrap hover:bg-gray-200/40" @click="setSort('name')">
                      <span class="inline-flex items-center gap-1">건물명 <ChevronsUpDown v-if="sortKey !== 'name'" :size="12" /><ChevronUp v-else-if="sortDir === 'asc'" :size="12" /><ChevronDown v-else :size="12" /></span>
                    </th>
                    <th class="px-4 py-3 text-xs font-semibold text-navy cursor-pointer select-none transition-colors whitespace-nowrap hover:bg-gray-200/40" @click="setSort('area')">
                      <span class="inline-flex items-center gap-1">면적(㎡) <ChevronsUpDown v-if="sortKey !== 'area'" :size="12" /><ChevronUp v-else-if="sortDir === 'asc'" :size="12" /><ChevronDown v-else :size="12" /></span>
                    </th>
                    <th class="px-4 py-3 text-xs font-semibold text-navy cursor-pointer select-none transition-colors whitespace-nowrap hover:bg-gray-200/40" @click="setSort('floor')">
                      <span class="inline-flex items-center gap-1">층 <ChevronsUpDown v-if="sortKey !== 'floor'" :size="12" /><ChevronUp v-else-if="sortDir === 'asc'" :size="12" /><ChevronDown v-else :size="12" /></span>
                    </th>
                    <th class="px-4 py-3 text-xs font-semibold text-navy cursor-pointer select-none transition-colors whitespace-nowrap hover:bg-gray-200/40" @click="setSort('price')">
                      <span class="inline-flex items-center gap-1">가격 <ChevronsUpDown v-if="sortKey !== 'price'" :size="12" /><ChevronUp v-else-if="sortDir === 'asc'" :size="12" /><ChevronDown v-else :size="12" /></span>
                    </th>
                    <th class="px-4 py-3 text-xs font-semibold text-navy cursor-pointer select-none transition-colors whitespace-nowrap hover:bg-gray-200/40" @click="setSort('date')">
                      <span class="inline-flex items-center gap-1">거래일 <ChevronsUpDown v-if="sortKey !== 'date'" :size="12" /><ChevronUp v-else-if="sortDir === 'asc'" :size="12" /><ChevronDown v-else :size="12" /></span>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="!filters.regionCode">
                    <td colspan="5" class="text-center py-12 text-gray-400">좌측 필터에서 법정동을 선택하고 검색하세요.</td>
                  </tr>
                  <tr v-else-if="loading">
                    <td colspan="5" class="text-center py-12 text-gray-400">불러오는 중...</td>
                  </tr>
                  <tr v-else-if="pageData.length === 0">
                    <td colspan="5" class="text-center py-12 text-gray-400">
                      {{ loadError ? '데이터를 불러오는 중 오류가 발생했습니다.' : '검색 결과가 없습니다.' }}
                    </td>
                  </tr>
                  <tr
                    v-for="item in pageData"
                    :key="item.houseId"
                    class="cursor-pointer border-b border-gray-50 transition-colors last:border-none hover:bg-blue/[0.03]"
                    :class="{ 'bg-blue/5 border-l-[3px] border-l-blue': selectedHouseId === item.houseId }"
                    @click="openModal(item)"
                  >
                    <td class="px-4 py-3 text-[0.8125rem] font-medium text-navy">{{ item.aptName }}</td>
                    <td class="px-4 py-3 text-[0.8125rem]">{{ item.latestDeal?.area?.toFixed(2) ?? '-' }}</td>
                    <td class="px-4 py-3 text-[0.8125rem]">{{ item.latestDeal?.floor != null ? item.latestDeal.floor + '층' : '-' }}</td>
                    <td class="px-4 py-3 text-[0.8125rem] font-semibold text-navy">{{ formatDealPrice(item.latestDeal) }}</td>
                    <td class="px-4 py-3 text-[0.8125rem] text-gray-400">{{ item.latestDeal?.dealDate ?? '-' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="flex items-center justify-center gap-1 py-5">
              <button class="w-9 h-9 flex items-center justify-center rounded-lg text-gray-500 text-[0.8125rem] transition-colors hover:bg-bg-page hover:text-navy disabled:text-[#d1d5db] disabled:cursor-not-allowed" :disabled="currentPage === 1" @click="goPage(1)"><ChevronsLeft :size="14" /></button>
              <button class="w-9 h-9 flex items-center justify-center rounded-lg text-gray-500 text-[0.8125rem] transition-colors hover:bg-bg-page hover:text-navy disabled:text-[#d1d5db] disabled:cursor-not-allowed" :disabled="currentPage === 1" @click="goPage(currentPage - 1)"><ChevronLeft :size="14" /></button>
              <button
                v-for="p in pageRange" :key="p"
                class="w-9 h-9 flex items-center justify-center rounded-lg text-[0.8125rem] transition-colors"
                :class="p === currentPage ? 'bg-blue text-white font-semibold cursor-default' : 'text-gray-500 hover:bg-bg-page hover:text-navy'"
                @click="goPage(p)"
              >{{ p }}</button>
              <button class="w-9 h-9 flex items-center justify-center rounded-lg text-gray-500 text-[0.8125rem] transition-colors hover:bg-bg-page hover:text-navy disabled:text-[#d1d5db] disabled:cursor-not-allowed" :disabled="currentPage === totalPages" @click="goPage(currentPage + 1)"><ChevronRight :size="14" /></button>
              <button class="w-9 h-9 flex items-center justify-center rounded-lg text-gray-500 text-[0.8125rem] transition-colors hover:bg-bg-page hover:text-navy disabled:text-[#d1d5db] disabled:cursor-not-allowed" :disabled="currentPage === totalPages" @click="goPage(totalPages)"><ChevronsRight :size="14" /></button>
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- Detail Modal -->
    <div v-if="modalItem" class="fixed inset-0 z-[100] flex items-center justify-center p-4" @click.self="closeModal">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="closeModal"></div>
      <div class="relative bg-white rounded-2xl shadow-[0_20px_60px_rgba(0,0,0,0.2)] w-full max-w-[720px] max-h-[90vh] flex flex-col overflow-hidden">
        <div class="flex items-start justify-between px-6 py-5 border-b border-gray-100">
          <div>
            <h2 class="text-navy text-xl font-bold">{{ modalItem.aptName }}</h2>
            <p class="text-gray-400 text-sm mt-1">{{ modalItem.dongName }} {{ modalItem.jibun }}</p>
          </div>
          <button class="w-9 h-9 flex items-center justify-center rounded-lg text-gray-400 transition-colors hover:bg-bg-page" @click="closeModal"><X :size="20" /></button>
        </div>
        <div class="px-6 py-4 bg-gradient-to-r from-blue/5 to-navy/5 border-b border-blue/10 flex items-center justify-between">
          <span class="text-navy text-2xl font-bold">{{ formatDealPrice(modalItem.latestDeal) }}</span>
          <span class="text-gray-400 text-xs">거래일: {{ modalItem.latestDeal?.dealDate ?? '-' }}</span>
        </div>
        <div class="overflow-y-auto flex-1 p-6">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div
              v-for="inf in modalInfoItems"
              :key="inf.label"
              class="flex items-start gap-3 bg-bg-page rounded-xl px-4 py-3"
            >
              <div class="w-8 h-8 rounded-lg bg-white flex items-center justify-center shrink-0 shadow-[0_1px_3px_rgba(0,0,0,0.06)]">
                <component :is="inf.icon" :size="16" class="text-blue" />
              </div>
              <div>
                <p class="text-gray-400 text-[0.6875rem] font-medium">{{ inf.label }}</p>
                <p class="text-navy text-sm font-semibold">{{ inf.value }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100">
          <BaseButton
            size="sm"
            :variant="isFavItem ? 'outline-danger' : 'ghost'"
            @click="toggleFavorite"
          >
            <Heart :size="16" /> {{ isFavItem ? '관심지역 해제' : '관심지역 추가' }}
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>
