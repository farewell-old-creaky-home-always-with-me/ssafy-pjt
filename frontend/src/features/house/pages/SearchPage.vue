<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { X, SlidersHorizontal, ChevronsUpDown, ChevronUp, ChevronDown,
  ChevronsLeft, ChevronsRight, ChevronLeft, ChevronRight, Heart,
  Building2, Handshake, Calendar, MapPin, Hash, Landmark, Maximize2, Layers } from 'lucide-vue-next'
import { useFavoritesStore } from '@/stores/favoritesStore.js'
import { useAuthStore } from '@/stores/authStore.js'
import { housesApi, regionsApi } from '@/api/index.js'
import '@css/pages/search.css'

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

const filters = ref({ regionCode: '', buildingType: '아파트', transactionType: '매매' })

// 가격 포맷 — API는 만원 단위 숫자 반환
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
      houseType: filters.value.buildingType || undefined,
      dealType: filters.value.transactionType || undefined,
      sortBy: sortKey.value,
      sortDir: sortDir.value,
      page: currentPage.value,
      size: PAGE_SIZE,
    })
    pageData.value = res.items
    totalItems.value = res.total
  } catch {
    loadError.value = true
    pageData.value = []
    totalItems.value = 0
  } finally {
    loading.value = false
  }
}

// 페이지네이션
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
  filters.value = { regionCode: '', buildingType: '아파트', transactionType: '매매' }
  currentPage.value = 1
  pageData.value = []
  totalItems.value = 0
}

// 모달
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
    { icon: Maximize2, label: '전용면적', value: item.latestDeal ? item.latestDeal.area.toFixed(2) + ' ㎡' : '-' },
    { icon: Layers, label: '층', value: item.latestDeal ? item.latestDeal.floor + '층' : '-' },
  ]
})

const isFavItem = computed(() => {
  if (!modalItem.value) return false
  return favoritesStore.items.some(f => f.regionCode === modalItem.value.regionCode)
})

const favBtnStyle = computed(() =>
  isFavItem.value ? 'background:#FEE2E2;border-color:#EB5757;color:#EB5757' : ''
)

function openModal(item) { selectedHouseId.value = item.houseId; modalItem.value = item }
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

// 카카오맵
let map
function initMap() {
  const container = document.getElementById('map')
  if (!container || typeof window.kakao === 'undefined') return
  map = new window.kakao.maps.Map(container, {
    center: new window.kakao.maps.LatLng(37.5665, 126.9780),
    level: 5,
  })
}

onMounted(async () => {
  regions.value = await regionsApi.getRegions()

  const { dong, type } = route.query
  if (dong) {
    const matches = regions.value.filter(r => r.dongName === dong)
    if (matches.length === 1) filters.value.regionCode = matches[0].regionCode
  }
  if (type && TYPE_MAP[type]) Object.assign(filters.value, TYPE_MAP[type])

  if (filters.value.regionCode) await fetchHouses()

  initMap()
})
</script>

<template>
  <div class="search-page">
    <div class="search-layout">
      <!-- Filter Sidebar -->
      <aside class="filter-sidebar" :class="{ 'sidebar-hidden': !sidebarOpen }" id="filter-sidebar">
        <div class="filter-header">
          <h2>필터</h2>
          <button class="filter-close" @click="sidebarOpen = false"><X :size="16" /></button>
        </div>
        <div class="filter-body">
          <div class="filter-group">
            <label class="filter-label">법정동</label>
            <select v-model="filters.regionCode" class="filter-select" id="filter-dong">
              <option value="">지역을 선택하세요</option>
              <option v-for="r in regions" :key="r.regionCode" :value="r.regionCode">
                {{ r.sidoName }} {{ r.sigunguName }} {{ r.dongName }}
              </option>
            </select>
          </div>
          <div class="filter-group">
            <label class="filter-label">건물유형</label>
            <div class="toggle-group">
              <button class="toggle-btn" :class="{ 'active-blue': filters.buildingType === '아파트' }" @click="filters.buildingType = '아파트'">아파트</button>
              <button class="toggle-btn" :class="{ 'active-blue': filters.buildingType === '다세대' }" @click="filters.buildingType = '다세대'">다세대</button>
            </div>
          </div>
          <div class="filter-group">
            <label class="filter-label">거래유형</label>
            <div class="toggle-group">
              <button class="toggle-btn" :class="{ 'active-navy': filters.transactionType === '매매' }" @click="filters.transactionType = '매매'">매매</button>
              <button class="toggle-btn" :class="{ 'active-navy': filters.transactionType === '전월세' }" @click="filters.transactionType = '전월세'">전월세</button>
            </div>
          </div>
          <div style="display:flex;gap:0.5rem;margin-top:1rem">
            <button class="btn btn-primary btn-full btn-sm" :disabled="!filters.regionCode" @click="applyFilter">적용</button>
            <button class="btn btn-ghost btn-full btn-sm" @click="resetFilter">초기화</button>
          </div>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="search-main">
        <div class="search-top-bar">
          <button class="filter-toggle-btn" @click="sidebarOpen = !sidebarOpen"><SlidersHorizontal :size="16" /> 필터</button>
          <span class="result-count">총 <strong id="result-count">{{ totalItems }}</strong>건</span>
          <span class="result-range">{{ resultRange }}</span>
        </div>

        <div class="map-wrap">
          <div id="map" style="width:100%;height:300px"></div>
        </div>

        <table class="result-table">
          <thead>
            <tr>
              <th @click="setSort('name')">건물명 <ChevronsUpDown v-if="sortKey !== 'name'" :size="14" /><ChevronUp v-else-if="sortDir === 'asc'" :size="14" /><ChevronDown v-else :size="14" /></th>
              <th @click="setSort('area')">면적(㎡) <ChevronsUpDown v-if="sortKey !== 'area'" :size="14" /><ChevronUp v-else-if="sortDir === 'asc'" :size="14" /><ChevronDown v-else :size="14" /></th>
              <th @click="setSort('floor')">층 <ChevronsUpDown v-if="sortKey !== 'floor'" :size="14" /><ChevronUp v-else-if="sortDir === 'asc'" :size="14" /><ChevronDown v-else :size="14" /></th>
              <th @click="setSort('price')">가격 <ChevronsUpDown v-if="sortKey !== 'price'" :size="14" /><ChevronUp v-else-if="sortDir === 'asc'" :size="14" /><ChevronDown v-else :size="14" /></th>
              <th @click="setSort('date')">거래일 <ChevronsUpDown v-if="sortKey !== 'date'" :size="14" /><ChevronUp v-else-if="sortDir === 'asc'" :size="14" /><ChevronDown v-else :size="14" /></th>
            </tr>
          </thead>
          <tbody id="result-tbody">
            <tr v-if="!filters.regionCode">
              <td colspan="5" style="text-align:center;padding:3rem;color:#9ca3af">
                좌측 필터에서 법정동을 선택하고 검색하세요.
              </td>
            </tr>
            <tr v-else-if="loading">
              <td colspan="5" style="text-align:center;padding:3rem;color:#9ca3af">불러오는 중...</td>
            </tr>
            <tr v-else-if="pageData.length === 0">
              <td colspan="5" style="text-align:center;padding:3rem;color:#9ca3af">
                {{ loadError ? '데이터를 불러오는 중 오류가 발생했습니다.' : '검색 결과가 없습니다.' }}
              </td>
            </tr>
            <tr v-for="item in pageData" :key="item.houseId"
              :class="{ selected: selectedHouseId === item.houseId }"
              @click="openModal(item)">
              <td style="font-weight:500;color:#1A3C6E">{{ item.aptName }}</td>
              <td>{{ item.latestDeal?.area?.toFixed(2) ?? '-' }}</td>
              <td>{{ item.latestDeal?.floor != null ? item.latestDeal.floor + '층' : '-' }}</td>
              <td style="font-weight:600;color:#1A3C6E">{{ formatDealPrice(item.latestDeal) }}</td>
              <td style="color:#9ca3af">{{ item.latestDeal?.dealDate ?? '-' }}</td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div class="pagination" id="pagination">
          <button class="page-btn" :disabled="currentPage === 1" @click="goPage(1)"><ChevronsLeft :size="14" /></button>
          <button class="page-btn" :disabled="currentPage === 1" @click="goPage(currentPage - 1)"><ChevronLeft :size="14" /></button>
          <button v-for="p in pageRange" :key="p" class="page-btn" :class="{ active: p === currentPage }" @click="goPage(p)">{{ p }}</button>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="goPage(currentPage + 1)"><ChevronRight :size="14" /></button>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="goPage(totalPages)"><ChevronsRight :size="14" /></button>
        </div>
      </main>
    </div>

    <!-- Detail Modal -->
    <div v-if="modalItem" class="modal-overlay visible" @click.self="closeModal">
      <div class="detail-modal-box">
        <div class="modal-header">
          <div>
            <h2 class="modal-title">{{ modalItem.aptName }}</h2>
            <p class="modal-subtitle">{{ modalItem.dongName }} {{ modalItem.jibun }}</p>
          </div>
          <button class="modal-close-btn" @click="closeModal"><X :size="20" /></button>
        </div>
        <div class="modal-price-row">
          <span class="modal-price">{{ formatDealPrice(modalItem.latestDeal) }}</span>
          <span class="modal-date">거래일: {{ modalItem.latestDeal?.dealDate ?? '-' }}</span>
        </div>
        <div class="modal-info-grid">
          <div class="info-item" v-for="inf in modalInfoItems" :key="inf.label">
            <div class="info-item-icon"><component :is="inf.icon" :size="16" /></div>
            <div>
              <p class="info-item-label">{{ inf.label }}</p>
              <p class="info-item-value">{{ inf.value }}</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-sm" :style="favBtnStyle" @click="toggleFavorite">
            <Heart :size="16" /> {{ isFavItem ? '관심지역 해제' : '관심지역 추가' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
