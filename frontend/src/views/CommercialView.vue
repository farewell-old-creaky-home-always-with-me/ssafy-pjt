<template>
  <div class="commercial-page">
    <div id="map" style="width:100%;height:calc(100vh - 64px)"></div>

    <!-- Category tabs -->
    <div id="cat-tabs" style="position:absolute;top:80px;left:50%;transform:translateX(-50%);display:flex;gap:0.5rem;z-index:10;flex-wrap:wrap;justify-content:center">
      <button v-for="cat in CATEGORIES" :key="cat.key"
        class="cat-tab" :class="{ active: activeCategory === cat.key }"
        :style="activeCategory === cat.key ? `background-color:${cat.color}` : ''"
        @click="selectCategory(cat.key)">
        <component :is="cat.icon" :size="14" />
        {{ cat.label }}
        <span class="cat-tab-count" :style="activeCategory === cat.key ? 'background:rgba(255,255,255,0.2)' : 'background:rgba(156,163,175,0.4)'">
          {{ activeCategory === cat.key ? storesData.length : '-' }}
        </span>
      </button>
    </div>

    <!-- Bottom sheet -->
    <div v-if="selectedShop" id="bottom-sheet" class="bottom-sheet expanded">
      <div style="padding:1rem 1.25rem;display:flex;align-items:flex-start;gap:0.75rem">
        <div id="sheet-icon" :style="`background:${activeCat.color}15;width:2.5rem;height:2.5rem;border-radius:0.75rem;display:flex;align-items:center;justify-content:center;flex-shrink:0`">
          <component :is="activeCat.icon" :size="20" :style="`color:${activeCat.color}`" />
        </div>
        <div style="flex:1;min-width:0">
          <h3 style="color:#1A3C6E;font-size:1rem;font-weight:700">{{ selectedShop.bizName }}</h3>
          <p style="color:#9ca3af;font-size:0.8125rem;margin-top:0.125rem">{{ selectedShop.categoryMedium || selectedShop.categoryLarge }}</p>
        </div>
        <button @click="selectedShop = null" style="background:none;border:none;cursor:pointer"><X :size="18" /></button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { UtensilsCrossed, Store, Hospital, BookOpen, LayoutGrid, X } from 'lucide-vue-next'
import { api } from '../api/index.js'
import '../../css/pages/commercial.css'

const CATEGORIES = [
  { key: '', label: '전체', icon: LayoutGrid, color: '#4F46E5' },
  { key: 'I2', label: '음식점/카페', icon: UtensilsCrossed, color: '#EB5757' },
  { key: 'G2', label: '편의점/마트', icon: Store, color: '#2D9CDB' },
  { key: 'Q1', label: '의료/병원', icon: Hospital, color: '#27AE60' },
  { key: 'P1', label: '교육/학원', icon: BookOpen, color: '#F2994A' },
]

const activeCategory = ref('')
const storesData = ref([])
const selectedShop = ref(null)
let map, markers = []

const activeCat = computed(() => CATEGORIES.find(c => c.key === activeCategory.value) || CATEGORIES[0])

function clearMarkers() { markers.forEach(m => m.setMap(null)); markers = [] }

async function fetchAndRender() {
  if (!map) return
  const center = map.getCenter()
  const params = { lat: center.getLat(), lng: center.getLng(), radius: 1000 }
  if (activeCategory.value) params.category = activeCategory.value
  try {
    storesData.value = await api.get('/api/commercial', params)
  } catch { storesData.value = [] }

  clearMarkers()
  selectedShop.value = null

  storesData.value.forEach(store => {
    if (!store.latitude || !store.longitude) return
    const pos = new window.kakao.maps.LatLng(parseFloat(store.latitude), parseFloat(store.longitude))
    const content = document.createElement('div')
    content.className = 'marker-btn'
    content.style.cssText = 'position:relative;cursor:pointer'
    const dot = document.createElement('div')
    dot.className = 'marker-dot'
    dot.style.background = activeCat.value.color
    const labelEl = document.createElement('div')
    labelEl.className = 'marker-label'
    labelEl.textContent = store.bizName
    content.appendChild(dot)
    content.appendChild(labelEl)
    content.onclick = () => { selectedShop.value = store }
    const overlay = new window.kakao.maps.CustomOverlay({ position: pos, content, yAnchor: 1 })
    overlay.setMap(map)
    markers.push(overlay)
  })
}

async function selectCategory(key) {
  activeCategory.value = key
  await fetchAndRender()
}

function initMap() {
  const container = document.getElementById('map')
  if (!container || typeof window.kakao === 'undefined') return
  map = new window.kakao.maps.Map(container, { center: new window.kakao.maps.LatLng(37.4979, 127.0276), level: 4 })
  fetchAndRender()
}

onMounted(initMap)
</script>
