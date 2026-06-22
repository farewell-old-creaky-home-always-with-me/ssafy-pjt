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
  <div class="env-page">
    <div class="env-header">
      <div class="container" style="padding-top:0;padding-bottom:0">
        <h1 style="color:#1A3C6E;font-size:1.375rem;font-weight:700">환경 정보</h1>
        <p style="color:#9ca3af;font-size:0.8125rem;margin-top:0.25rem">지도 위에서 환경 데이터 레이어를 확인하세요</p>
      </div>
    </div>

    <div class="env-map-area">
      <div id="map" class="env-map"></div>

      <div v-if="!hasActiveLayer" class="env-empty">
        <div style="width:5rem;height:5rem;border-radius:50%;background:rgba(255,255,255,0.8);display:flex;align-items:center;justify-content:center;margin-bottom:1rem">
          <MapPin :size="40" style="color:#2D9CDB" />
        </div>
        <p style="color:rgba(26,60,110,0.8);font-size:0.875rem;font-weight:600;background:rgba(255,255,255,0.8);padding:4px 8px;border-radius:4px">
          {{ loading ? '데이터를 불러오는 중...' : '좌측 패널에서 레이어를 활성화해 주세요' }}
        </p>
      </div>

      <div class="env-legend">
        <div class="env-legend-header">
          <h3>데이터 레이어</h3>
          <span class="env-active-count">{{ activeCount }}/3 활성</span>
        </div>
        <div style="padding:0.75rem;display:flex;flex-direction:column;gap:0.5rem">
          <button v-for="layer in LAYERS" :key="layer.key"
            class="env-layer-btn" :class="{ on: active[layer.key] }"
            @click="toggleLayer(layer.key)">
            <div class="env-layer-icon" :style="`background:${active[layer.key] ? layer.color + '15' : '#F4F6F9'}`">
              <component :is="layer.icon" :size="18" :style="`color:${active[layer.key] ? layer.color : '#aaa'}`" />
            </div>
            <div style="flex:1;min-width:0">
              <div class="env-layer-label">
                {{ layer.label }}
                <span class="env-layer-count" :style="`background:${active[layer.key] ? layer.color + '15' : '#F4F6F9'};color:${active[layer.key] ? layer.color : '#aaa'}`">
                  {{ layerData[layer.key].length }}
                </span>
              </div>
              <p class="env-layer-desc">{{ layer.desc }}</p>
            </div>
            <component :is="active[layer.key] ? Eye : EyeOff" :size="16" :style="`color:${active[layer.key] ? layer.color : '#d1d5db'}`" />
          </button>
        </div>
        <div class="env-legend-footer">
          <Info :size="14" />
          데이터 출처: 백엔드 환경 API
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.env-page { min-height: calc(100vh - 64px); background: #F4F6F9; display: flex; flex-direction: column; }
.env-header { background: #fff; border-bottom: 1px solid #f3f4f6; padding: 1rem 1.5rem; }
.env-map-area { flex: 1; position: relative; min-height: 500px; }
.env-map { position: absolute; inset: 0; background: linear-gradient(135deg, #E8F0FE, #D4E4F7); overflow: hidden; }
.env-empty { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; flex-direction: column; z-index: 5; pointer-events: none; }
.env-legend { position: absolute; top: 1rem; left: 1rem; z-index: 20; width: 18rem; background: #fff; border-radius: 1rem; box-shadow: 0 4px 16px rgba(0,0,0,0.1); border: 1px solid #f3f4f6; overflow: hidden; }
.env-legend-header { padding: 0.875rem 1.25rem; border-bottom: 1px solid #f3f4f6; display: flex; align-items: center; justify-content: space-between; }
.env-legend-header h3 { color: #1A3C6E; font-size: 0.875rem; font-weight: 600; }
.env-active-count { color: #2D9CDB; background: rgba(45,156,219,0.1); border-radius: 0.5rem; padding: 0.125rem 0.5rem; font-size: 0.6875rem; font-weight: 600; }
.env-layer-btn { width: 100%; display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem 1rem; border-radius: 0.75rem; border: 1px solid transparent; background: none; cursor: pointer; text-align: left; transition: all 0.15s; }
.env-layer-btn.on { background: #F4F6F9; border-color: #e5e7eb; }
.env-layer-icon { width: 2.25rem; height: 2.25rem; border-radius: 0.5rem; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.env-layer-label { color: #1A3C6E; font-size: 0.8125rem; font-weight: 600; display: flex; align-items: center; gap: 0.5rem; }
.env-layer-count { padding: 0.125rem 0.375rem; border-radius: 0.375rem; font-size: 0.625rem; font-weight: 600; }
.env-layer-desc { color: #9ca3af; font-size: 0.6875rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 10rem; }
.env-legend-footer { padding: 0.75rem 1.25rem; border-top: 1px solid #f3f4f6; display: flex; align-items: flex-start; gap: 0.5rem; color: #9ca3af; font-size: 0.6875rem; }
</style>
