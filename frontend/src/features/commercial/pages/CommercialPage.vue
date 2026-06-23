<script setup>
import { ref, computed, onMounted } from 'vue'
import { UtensilsCrossed, Store, Hospital, BookOpen, LayoutGrid, X } from 'lucide-vue-next'
import { commercialApi } from '@/api/index.js'

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
    storesData.value = await commercialApi.getCommercials(params)
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
  if (!container) return
  map = new window.kakao.maps.Map(container, { center: new window.kakao.maps.LatLng(37.4979, 127.0276), level: 4 })
  fetchAndRender()
}

onMounted(() => {
  if (typeof window.kakao === 'undefined') return
  window.kakao.maps.load(initMap)
})
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] bg-bg-page flex flex-col relative">
    <div id="map" style="width:100%;height:calc(100vh - 64px)"></div>

    <!-- Category tabs -->
    <div class="absolute top-20 left-1/2 -translate-x-1/2 flex gap-2 z-10 flex-wrap justify-center">
      <button
        v-for="cat in CATEGORIES"
        :key="cat.key"
        class="flex items-center gap-2 px-5 py-2.5 rounded-xl whitespace-nowrap text-[0.8125rem] font-medium border-none cursor-pointer transition-all my-3 bg-bg-page text-gray-500 hover:bg-[#e5e7eb]"
        :class="{ 'text-white font-semibold shadow-[0_2px_8px_rgba(0,0,0,0.15)]': activeCategory === cat.key }"
        :style="activeCategory === cat.key ? `background-color:${cat.color}` : ''"
        @click="selectCategory(cat.key)"
      >
        <component :is="cat.icon" :size="14" />
        {{ cat.label }}
        <span
          class="px-1.5 py-0.5 rounded-[0.375rem] text-[0.6875rem] font-semibold"
          :style="activeCategory === cat.key ? 'background:rgba(255,255,255,0.2)' : 'background:rgba(156,163,175,0.4)'"
        >
          {{ activeCategory === cat.key ? storesData.length : '-' }}
        </span>
      </button>
    </div>

    <!-- Bottom sheet -->
    <div v-if="selectedShop" class="absolute bottom-0 left-0 right-0 bg-white rounded-t-2xl shadow-[0_-4px_20px_rgba(0,0,0,0.1)] border-t border-gray-100 z-30 overflow-hidden max-h-[60%]">
      <div class="px-5 py-4 flex items-start gap-3">
        <div
          class="w-10 h-10 rounded-xl flex items-center justify-center shrink-0"
          :style="`background:${activeCat.color}15`"
        >
          <component :is="activeCat.icon" :size="20" :style="`color:${activeCat.color}`" />
        </div>
        <div class="flex-1 min-w-0">
          <h3 class="text-navy text-base font-bold">{{ selectedShop.bizName }}</h3>
          <p class="text-gray-400 text-[0.8125rem] mt-0.5">{{ selectedShop.categoryMedium || selectedShop.categoryLarge }}</p>
        </div>
        <button class="text-gray-400 hover:text-gray-600" @click="selectedShop = null"><X :size="18" /></button>
      </div>
    </div>
  </div>
</template>
