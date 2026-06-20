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
            <select v-model="filters.dong" class="filter-select" id="filter-dong">
              <option value="">전체</option>
              <option v-for="d in dongList" :key="d" :value="d">{{ d }}</option>
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
          <div class="filter-group">
            <label class="filter-label">건축연도</label>
            <div style="display:flex;gap:0.5rem;align-items:center">
              <input v-model.number="filters.yearStart" type="number" class="filter-input" style="width:5rem" />
              <span style="color:#9ca3af">~</span>
              <input v-model.number="filters.yearEnd" type="number" class="filter-input" style="width:5rem" />
            </div>
          </div>
          <div style="display:flex;gap:0.5rem;margin-top:1rem">
            <button class="btn btn-primary btn-full btn-sm" @click="applyFilter">적용</button>
            <button class="btn btn-ghost btn-full btn-sm" @click="resetFilter">초기화</button>
          </div>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="search-main">
        <div class="search-top-bar">
          <button class="filter-toggle-btn" @click="sidebarOpen = !sidebarOpen"><SlidersHorizontal :size="16" /> 필터</button>
          <span class="result-count">총 <strong id="result-count">{{ filteredData.length }}</strong>건</span>
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
            <tr v-if="pageData.length === 0">
              <td colspan="5" style="text-align:center;padding:3rem;color:#9ca3af">
                {{ loadError ? '데이터를 불러오는 중 오류가 발생했습니다. 콘솔을 확인하세요.' : '검색 결과가 없습니다.' }}
              </td>
            </tr>
            <tr v-for="item in pageData" :key="item.id"
              :class="{ selected: selectedId === item.id }"
              @click="openModal(item)">
              <td style="font-weight:500;color:#1A3C6E">{{ item.name }}</td>
              <td>{{ item.area.toFixed(2) }}</td>
              <td>{{ item.floor }}층</td>
              <td style="font-weight:600;color:#1A3C6E">{{ item.price }}</td>
              <td style="color:#9ca3af">{{ item.date }}</td>
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
            <h2 class="modal-title">{{ modalItem.name }}</h2>
            <p class="modal-subtitle">{{ modalItem.dong }} {{ modalItem.jibun }}</p>
          </div>
          <button class="modal-close-btn" @click="closeModal"><X :size="20" /></button>
        </div>
        <img :src="aptImages[modalItem.id % aptImages.length]" class="modal-img" :alt="modalItem.name" />
        <div class="modal-price-row">
          <span class="modal-price">{{ modalItem.price }}</span>
          <span class="modal-date">거래일: {{ modalItem.date }}</span>
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

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { X, SlidersHorizontal, ChevronsUpDown, ChevronUp, ChevronDown, ChevronsLeft, ChevronsRight, ChevronLeft, ChevronRight, Heart, Building2, Handshake, Calendar, MapPin, Hash, Landmark, Maximize2, Layers } from 'lucide-vue-next'
import { useFavoritesStore } from '../stores/favoritesStore.js'
import { useAuthStore } from '../stores/authStore.js'
import '../../css/pages/search.css'

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

const aptImages = [
  'https://images.unsplash.com/photo-1486325212027-8081e485255e?w=600&q=80',
  'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=600&q=80',
  'https://images.unsplash.com/photo-1460317442991-0ec209397118?w=600&q=80',
  'https://images.unsplash.com/photo-1515263487990-61b07816dd07?w=600&q=80',
  'https://images.unsplash.com/photo-1523217582562-09d0def993a6?w=600&q=80',
]

const allData = ref([])
const dongList = ref([])
const loadError = ref(false)
const sidebarOpen = ref(false)
const selectedId = ref(null)
const modalItem = ref(null)
const currentPage = ref(1)
const sortKey = ref('date')
const sortDir = ref('desc')

const filters = ref({ dong: '', buildingType: '아파트', transactionType: '매매', yearStart: 1970, yearEnd: 2026 })

// ── XML 파싱 (기존 search.js 로직 그대로) ──
async function loadDongListFromXML() {
  const xmlFiles = ['/resources/ssafy_home/AptInfo.xml', '/resources/ssafy_home/AptDealHistory.xml',
    '/resources/ssafy_home/AptRentHistory.xml', '/resources/ssafy_home/HomeDealHistory.xml', '/resources/ssafy_home/HomeRentHistory.xml']
  const dongSet = new Set()
  for (const file of xmlFiles) {
    try {
      const res = await fetch(file)
      const text = await res.text()
      const doc = new DOMParser().parseFromString(text, 'text/xml')
      Array.from(doc.getElementsByTagName('법정동')).forEach(el => { if (el.textContent.trim()) dongSet.add(el.textContent.trim()) })
    } catch (err) {
      console.error('[XML] 법정동 목록 로딩 실패:', file, err)
    }
  }
  return Array.from(dongSet).sort()
}

function getTag(node, tag) { const els = node.getElementsByTagName(tag); return els.length ? els[0].textContent : '' }
function formatPrice(amount) {
  const cleaned = amount.replace(/,/g, '').trim(); const num = parseInt(cleaned)
  if (isNaN(num)) return amount
  if (num >= 10000) { const e = Math.floor(num / 10000); const m = num % 10000; return m > 0 ? `${e}억 ${m.toLocaleString()}` : `${e}억` }
  return num.toLocaleString()
}
function formatRentPrice(dep, mon) {
  const d = parseInt(dep.replace(/,/g, '').trim()) || 0, m = parseInt(mon.replace(/,/g, '').trim()) || 0
  let r = ''
  if (d >= 10000) { const e = Math.floor(d / 10000), mm = d % 10000; r = mm > 0 ? `보 ${e}억 ${mm.toLocaleString()}` : `보 ${e}억` }
  else if (d > 0) r = `보 ${d.toLocaleString()}`
  if (m > 0) r += (r ? ' / ' : '') + `월 ${m.toLocaleString()}`
  return r || '0'
}

async function loadRealEstateDataFromXML() {
  const files = [
    { path: '/resources/ssafy_home/AptDealHistory.xml', buildingType: '아파트', transactionType: '매매', nameTag: '아파트' },
    { path: '/resources/ssafy_home/AptRentHistory.xml', buildingType: '아파트', transactionType: '전월세', nameTag: '아파트' },
    { path: '/resources/ssafy_home/HomeDealHistory.xml', buildingType: '다세대', transactionType: '매매', nameTag: '연립다세대' },
    { path: '/resources/ssafy_home/HomeRentHistory.xml', buildingType: '다세대', transactionType: '전월세', nameTag: '연립다세대' },
  ]
  const result = []; let idCounter = 1
  for (const fi of files) {
    try {
      const res = await fetch(fi.path)
      const doc = new DOMParser().parseFromString(await res.text(), 'text/xml')
      Array.from(doc.getElementsByTagName('item')).forEach(item => {
        const name = getTag(item, fi.nameTag), area = parseFloat(getTag(item, '전용면적')) || 0
        const floor = parseInt(getTag(item, '층')) || 0, buildYear = parseInt(getTag(item, '건축년도')) || 0
        const dong = getTag(item, '법정동').trim(), jibun = getTag(item, '지번'), regionCode = getTag(item, '지역코드')
        const year = getTag(item, '년'), month = getTag(item, '월'), day = getTag(item, '일')
        const rawDeal = getTag(item, '거래금액').trim(), rawDeposit = getTag(item, '보증금액').trim()
        const price = fi.transactionType === '매매' ? formatPrice(rawDeal) : formatRentPrice(rawDeposit, getTag(item, '월세금액').trim())
        const priceRaw = fi.transactionType === '매매'
          ? (parseInt(rawDeal.replace(/,/g, '')) || 0)
          : (parseInt(rawDeposit.replace(/,/g, '')) || 0)
        const date = year && month && day ? `${year}.${month.padStart(2,'0')}.${day.padStart(2,'0')}` : ''
        if (name && dong && buildYear) result.push({ id: idCounter++, name, area, floor, price, priceRaw, date, buildYear, dong, jibun, regionCode, buildingType: fi.buildingType, transactionType: fi.transactionType })
      })
    } catch (err) {
      console.error('[XML] 부동산 데이터 로딩 실패:', fi.path, err)
      loadError.value = true
    }
  }
  return result
}

// ── 필터/정렬/페이지 ──
const filteredData = computed(() => {
  const f = filters.value
  return allData.value.filter(item => {
    if (f.dong && item.dong !== f.dong) return false
    if (item.buildingType !== f.buildingType) return false
    if (item.transactionType !== f.transactionType) return false
    if (item.buildYear < f.yearStart || item.buildYear > f.yearEnd) return false
    return true
  }).sort((a, b) => {
    let av = a[sortKey.value], bv = b[sortKey.value]
    if (sortKey.value === 'price') { av = a.priceRaw; bv = b.priceRaw }
    if (av < bv) return sortDir.value === 'asc' ? -1 : 1
    if (av > bv) return sortDir.value === 'asc' ? 1 : -1
    return 0
  })
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredData.value.length / PAGE_SIZE)))
const pageData = computed(() => filteredData.value.slice((currentPage.value - 1) * PAGE_SIZE, currentPage.value * PAGE_SIZE))
const resultRange = computed(() => {
  const total = filteredData.value.length
  if (!total) return ''
  const start = (currentPage.value - 1) * PAGE_SIZE + 1
  const end = Math.min(currentPage.value * PAGE_SIZE, total)
  return `${start}–${end} / ${total}`
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
}
function goPage(n) { if (n < 1 || n > totalPages.value) return; currentPage.value = n; window.scrollTo({ top: 0, behavior: 'smooth' }) }
function applyFilter() { currentPage.value = 1; sidebarOpen.value = false }
function resetFilter() { filters.value = { dong: '', buildingType: '아파트', transactionType: '매매', yearStart: 1970, yearEnd: 2026 }; currentPage.value = 1 }

// ── 모달 ──
const modalInfoItems = computed(() => {
  if (!modalItem.value) return []
  const item = modalItem.value
  return [
    { icon: Building2, label: '건물유형', value: item.buildingType },
    { icon: Handshake, label: '거래유형', value: item.transactionType },
    { icon: Calendar, label: '건축연도', value: item.buildYear + '년' },
    { icon: MapPin, label: '동', value: item.dong },
    { icon: Hash, label: '지번', value: item.jibun },
    { icon: Landmark, label: '지역코드', value: item.regionCode },
    { icon: Maximize2, label: '전용면적', value: item.area.toFixed(2) + ' ㎡' },
    { icon: Layers, label: '층', value: item.floor + '층' },
  ]
})

const isFavItem = computed(() => {
  if (!modalItem.value) return false
  return favoritesStore.items.some(f => f.regionCode === modalItem.value.regionCode)
})

const favBtnStyle = computed(() => isFavItem.value
  ? 'background:#FEE2E2;border-color:#EB5757;color:#EB5757'
  : '')

function openModal(item) { selectedId.value = item.id; modalItem.value = item }
function closeModal() { modalItem.value = null; selectedId.value = null }

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

// ── 카카오맵 ──
let map
function initMap() {
  const container = document.getElementById('map')
  if (!container || typeof window.kakao === 'undefined') return
  map = new window.kakao.maps.Map(container, { center: new window.kakao.maps.LatLng(37.5665, 126.9780), level: 5 })
}

onMounted(async () => {
  dongList.value = await loadDongListFromXML()
  allData.value = await loadRealEstateDataFromXML()
  initMap()

  const { dong, type } = route.query
  if (dong) filters.value.dong = dong
  if (type && TYPE_MAP[type]) Object.assign(filters.value, TYPE_MAP[type])
})
</script>
