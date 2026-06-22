<script setup>
import { ref, computed, onMounted } from 'vue'
import { TreePine, Droplets, Wind, MapPin, Eye, EyeOff, Info } from 'lucide-vue-next'
import { environmentApi } from '@/api/index.js'
import { escapeHtml } from '@/utils/html.js'

const LAYERS = [
  { key: 'green', label: '공원/녹지', icon: TreePine, color: '#27AE60', desc: '녹지 환경 데이터', keywords: ['공원', '녹지', '수목'] },
  { key: 'water', label: '수질', icon: Droplets, color: '#2D9CDB', desc: '수질 측정 데이터', keywords: ['수질', '하천', '강'] },
  { key: 'air', label: '대기', icon: Wind, color: '#EB5757', desc: '대기질 측정 데이터', keywords: ['대기', '미세', '먼지'] },
]

const active = ref({ green: false, water: false, air: false })
const layerData = ref({ green: [], water: [], air: [] })
const loading = ref(false)
let map, mapOverlays = [], infowindow

const hasActiveLayer = computed(() => Object.values(active.value).some(Boolean))
const activeCount = computed(() => Object.values(active.value).filter(Boolean).length)

async function fetchLayer(key) {
  if (!map) return
  const center = map.getCenter()
  const all = await environmentApi.getEnvironment({ lat: center.getLat(), lng: center.getLng(), radius: 5000 })
  const layer = LAYERS.find(l => l.key === key)
  layerData.value[key] = all.filter(item =>
    layer.keywords.some(kw => item.itemName?.includes(kw))
  )
}

function clearOverlays() { mapOverlays.forEach(o => o.setMap(null)); mapOverlays = [] }

function renderMapOverlays() {
  clearOverlays()
  if (!map) return
  LAYERS.filter(l => active.value[l.key]).forEach(layer => {
    layerData.value[layer.key].forEach(data => {
      if (!data.latitude || !data.longitude) return
      const pos = new window.kakao.maps.LatLng(parseFloat(data.latitude), parseFloat(data.longitude))
      const circle = new window.kakao.maps.Circle({ center: pos, radius: 300, strokeWeight: 2, strokeColor: layer.color, strokeOpacity: 0.8, fillColor: layer.color, fillOpacity: 0.2 })
      circle.setMap(map)
      mapOverlays.push(circle)
      const marker = new window.kakao.maps.Marker({ position: pos })
      marker.setMap(map)
      mapOverlays.push(marker)
      window.kakao.maps.event.addListener(marker, 'click', () => {
        infowindow.setContent(`<div style="padding:10px;font-size:12px;color:#1A3C6E;min-width:150px"><span style="font-weight:700;display:block;margin-bottom:4px">${escapeHtml(data.itemName)}</span><span style="color:#6b7280">${escapeHtml(data.value)} ${escapeHtml(data.unit)}</span></div>`)
        infowindow.open(map, marker)
      })
    })
  })
}

async function toggleLayer(key) {
  active.value[key] = !active.value[key]
  if (active.value[key] && layerData.value[key].length === 0) {
    loading.value = true
    try {
      await fetchLayer(key)
    } catch {
      active.value[key] = false
    } finally {
      loading.value = false
    }
  }
  renderMapOverlays()
}

function initMap() {
  const container = document.getElementById('map')
  if (!container || typeof window.kakao === 'undefined') return
  map = new window.kakao.maps.Map(container, { center: new window.kakao.maps.LatLng(37.5665, 126.9780), level: 8 })
  infowindow = new window.kakao.maps.InfoWindow({ zIndex: 1 })
}

onMounted(initMap)
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] bg-bg-page flex flex-col">
    <div class="bg-white border-b border-gray-100 px-6 py-4">
      <div class="max-w-[80rem] mx-auto">
        <h1 class="text-navy text-[1.375rem] font-bold">환경 정보</h1>
        <p class="text-gray-400 text-[0.8125rem] mt-1">지도 위에서 환경 데이터 레이어를 확인하세요</p>
      </div>
    </div>

    <div class="flex-1 relative min-h-[500px]">
      <div id="map" class="absolute inset-0 bg-gradient-to-br from-[#E8F0FE] to-[#D4E4F7] overflow-hidden"></div>

      <div v-if="!hasActiveLayer" class="absolute inset-0 flex items-center justify-center flex-col z-[5] pointer-events-none">
        <div class="w-20 h-20 rounded-full bg-white/80 flex items-center justify-center mb-4">
          <MapPin :size="40" class="text-blue" />
        </div>
        <p class="text-navy/80 text-sm font-semibold bg-white/80 px-2 py-1 rounded">
          {{ loading ? '데이터를 불러오는 중...' : '좌측 패널에서 레이어를 활성화해 주세요' }}
        </p>
      </div>

      <!-- Legend panel -->
      <div class="absolute top-4 left-4 z-20 w-72 bg-white rounded-2xl shadow-[0_4px_16px_rgba(0,0,0,0.1)] border border-gray-100 overflow-hidden">
        <div class="px-5 py-[0.875rem] border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-navy text-sm font-semibold">데이터 레이어</h3>
          <span class="text-blue bg-blue/10 rounded-lg px-2 py-0.5 text-[0.6875rem] font-semibold">{{ activeCount }}/3 활성</span>
        </div>
        <div class="p-3 flex flex-col gap-2">
          <button
            v-for="layer in LAYERS"
            :key="layer.key"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl border border-transparent text-left transition-all cursor-pointer"
            :class="active[layer.key] ? 'bg-bg-page border-[#e5e7eb]' : 'bg-transparent'"
            @click="toggleLayer(layer.key)"
          >
            <div
              class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0"
              :style="`background:${active[layer.key] ? layer.color + '15' : '#F4F6F9'}`"
            >
              <component :is="layer.icon" :size="18" :style="`color:${active[layer.key] ? layer.color : '#aaa'}`" />
            </div>
            <div class="flex-1 min-w-0">
              <div class="text-navy text-[0.8125rem] font-semibold flex items-center gap-2">
                {{ layer.label }}
                <span
                  class="px-1.5 py-0.5 rounded-[0.375rem] text-[0.625rem] font-semibold"
                  :style="`background:${active[layer.key] ? layer.color + '15' : '#F4F6F9'};color:${active[layer.key] ? layer.color : '#aaa'}`"
                >{{ layerData[layer.key].length }}</span>
              </div>
              <p class="text-gray-400 text-[0.6875rem] truncate max-w-[10rem]">{{ layer.desc }}</p>
            </div>
            <component :is="active[layer.key] ? Eye : EyeOff" :size="16" :style="`color:${active[layer.key] ? layer.color : '#d1d5db'}`" />
          </button>
        </div>
        <div class="px-5 py-3 border-t border-gray-100 flex items-start gap-2 text-gray-400 text-[0.6875rem]">
          <Info :size="14" />
          데이터 출처: 백엔드 환경 API
        </div>
      </div>
    </div>
  </div>
</template>
