<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { MapPin, Navigation, Route as RouteIcon } from 'lucide-vue-next'
import { placesApi, routeApi } from '@/api/index.js'
import BaseButton from '@/components/base/BaseButton.vue'
import { useAuthStore } from '@/stores/authStore.js'

const props = defineProps({
  houseId: { type: Number, required: true },
})

const authStore = useAuthStore()

const places = ref([])
const selectedPlaceId = ref('')
const routeResult = ref(null)
const loadingPlaces = ref(false)
const calculating = ref(false)
const errorMessage = ref('')

const canCalculate = computed(() => selectedPlaceId.value && !calculating.value)

const distanceText = computed(() => {
  if (!routeResult.value) return ''
  const meters = routeResult.value.totalDistanceM
  if (meters < 1000) return `${meters.toLocaleString()}m`
  return `${(meters / 1000).toFixed(1)}km`
})

watch(() => props.houseId, () => {
  routeResult.value = null
  errorMessage.value = ''
})

onMounted(() => {
  if (authStore.isLoggedIn()) {
    loadPlaces()
  }
})

async function loadPlaces() {
  loadingPlaces.value = true
  errorMessage.value = ''
  try {
    places.value = await placesApi.getPlaces()
    selectedPlaceId.value = places.value[0]?.placeId ?? ''
  } catch {
    places.value = []
    errorMessage.value = '등록한 장소를 불러오지 못했습니다.'
  } finally {
    loadingPlaces.value = false
  }
}

async function handleCalculate() {
  if (!canCalculate.value) return
  calculating.value = true
  errorMessage.value = ''
  routeResult.value = null
  try {
    routeResult.value = await routeApi.getAstarRoute({
      houseId: props.houseId,
      placeId: Number(selectedPlaceId.value),
    })
  } catch {
    errorMessage.value = '경로를 계산하지 못했습니다.'
  } finally {
    calculating.value = false
  }
}
</script>

<template>
  <section class="mt-6 border-t border-gray-100 pt-5">
    <div class="mb-3 flex items-center justify-between gap-3">
      <h3 class="flex items-center gap-2 text-sm font-bold text-navy">
        <RouteIcon :size="16" class="text-blue" />
        경로 탐색
      </h3>
      <span v-if="routeResult" class="text-xs font-semibold text-blue">{{ distanceText }}</span>
    </div>

    <p v-if="!authStore.isLoggedIn()" class="rounded-xl bg-bg-page px-4 py-3 text-sm text-gray-500">
      로그인 후 등록한 장소까지의 경로를 탐색할 수 있습니다.
    </p>

    <div v-else class="flex flex-col gap-3">
      <div class="flex flex-col gap-2 sm:flex-row">
        <label class="sr-only" for="route-place">목적지</label>
        <select
          id="route-place"
          v-model="selectedPlaceId"
          class="min-w-0 flex-1 rounded-xl border border-[#e5e7eb] bg-bg-page px-3 py-2.5 text-[0.8125rem] text-navy outline-none transition-colors focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
          :disabled="loadingPlaces || places.length === 0"
        >
          <option value="" disabled>{{ loadingPlaces ? '장소 불러오는 중...' : '목적지를 선택하세요' }}</option>
          <option v-for="place in places" :key="place.placeId" :value="place.placeId">
            {{ place.name }}
          </option>
        </select>
        <BaseButton size="sm" :disabled="!canCalculate" @click="handleCalculate">
          <Navigation :size="15" />
          {{ calculating ? '계산 중' : '탐색' }}
        </BaseButton>
      </div>

      <p v-if="places.length === 0 && !loadingPlaces && !errorMessage" class="text-sm text-gray-400">
        먼저 프로필에서 목적지를 등록하세요.
      </p>
      <p v-if="errorMessage" class="text-sm font-medium text-red">{{ errorMessage }}</p>

      <div v-if="routeResult" class="grid grid-cols-2 gap-3 rounded-xl bg-bg-page px-4 py-3">
        <div class="flex items-center gap-2">
          <Navigation :size="15" class="text-blue" />
          <div>
            <p class="text-[0.6875rem] font-medium text-gray-400">총 거리</p>
            <p class="text-sm font-bold text-navy">{{ distanceText }}</p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <MapPin :size="15" class="text-blue" />
          <div>
            <p class="text-[0.6875rem] font-medium text-gray-400">경로 지점</p>
            <p class="text-sm font-bold text-navy">{{ routeResult.path.length }}개</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
