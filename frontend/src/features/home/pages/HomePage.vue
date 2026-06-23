<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ChevronDown, BarChart3, Building2, Landmark, TrendingUp, TrendingDown } from 'lucide-vue-next'
import { statsApi } from '@/api/index.js'

const router = useRouter()
const heroInput = ref('')
const heroTxType = ref('apt-sale')
const searchFocused = ref(false)

const stats = ref({
  thisMonthDealCount: null,
  thisMonthDealCountChange: null,
  avgSalePrice: null,
  avgSalePriceChange: null,
  avgLeasePrice: null,
  avgLeasePriceChange: null,
})

function formatKoreanManwon(won) {
  if (won == null) return '-'
  const manwon = Math.round(won / 10000)
  const eok = Math.floor(manwon / 10000)
  const rest = manwon % 10000
  if (eok === 0) return rest.toLocaleString()
  if (rest === 0) return eok + '억'
  return eok + '억 ' + rest.toLocaleString()
}

onMounted(async () => {
  try {
    stats.value = await statsApi.getStats()
  } catch {
    // Keep the placeholder stats when the backend is unavailable.
  }
})
</script>

<template>
  <section class="relative overflow-hidden min-h-[480px]">
    <div class="absolute inset-0 bg-cover bg-center" style="background-image:url('https://images.unsplash.com/photo-1768006241304-6b833ee11611?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxTZW91bCUyMGNpdHlzY2FwZSUyMGFwYXJ0bWVudCUyMGJ1aWxkaW5ncyUyMHNreWxpbmV8ZW58MXx8fHwxNzcyNzY0NTQ2fDA&ixlib=rb-4.1.0&q=80&w=1080')"></div>
    <div class="absolute inset-0 bg-gradient-to-b from-navy/85 via-navy/75 to-navy/90"></div>
    <div class="relative z-10 max-w-3xl mx-auto px-4 py-20 sm:py-28 flex flex-col items-center text-center">
      <div class="inline-flex items-center gap-2 bg-white/10 backdrop-blur border border-white/20 rounded-full px-4 py-1.5 mb-6 text-white/90 text-[0.8125rem] font-medium">
        <span class="w-2 h-2 rounded-full bg-blue animate-pulse"></span>
        공공데이터 기반 실거래가 조회 서비스
      </div>
      <h1 class="text-white text-3xl sm:text-4xl font-bold leading-tight mb-3">
        구해줘! <span class="text-blue">SSAFY HOME</span>
      </h1>
      <p class="text-white/70 text-[0.9375rem] font-normal mb-10 max-w-lg">
        원하는 지역의 부동산 실거래가를 간편하게 조회하세요.<br>
        아파트, 다세대 등 다양한 거래유형을 지원합니다.
      </p>
      <div class="w-full max-w-[42rem]">
        <div
          class="bg-white rounded-2xl p-2 shadow-[0_20px_60px_rgba(0,0,0,0.2)] transition-shadow duration-150"
          :class="{ 'shadow-[0_20px_60px_rgba(0,0,0,0.2),0_0_0_2px_rgba(45,156,219,0.5)]': searchFocused }"
        >
          <div class="flex flex-col sm:flex-row gap-2">
            <div class="relative flex-1">
              <span class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none">
                <Search :size="18" />
              </span>
              <input
                v-model="heroInput"
                type="text"
                class="w-full py-3 pl-10 pr-4 rounded-xl bg-bg-page border-none outline-none text-navy text-sm transition-colors focus:bg-[#EDF0F5] placeholder:text-gray-400"
                placeholder="법정동을 입력하세요 (예: 역삼동)"
                @focus="searchFocused = true"
                @blur="searchFocused = false"
              />
            </div>
            <div class="relative sm:w-48">
              <select v-model="heroTxType" class="w-full py-3 pl-4 pr-10 rounded-xl bg-bg-page border-none outline-none text-navy text-sm appearance-none cursor-pointer transition-colors focus:bg-[#EDF0F5]">
                <option value="apt-sale">아파트 매매</option>
                <option value="apt-rent">아파트 전월세</option>
                <option value="multi-sale">다세대 매매</option>
                <option value="multi-rent">다세대 전월세</option>
              </select>
              <span class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none">
                <ChevronDown :size="16" />
              </span>
            </div>
            <button
              class="py-3 px-6 rounded-xl bg-blue text-white text-sm font-semibold whitespace-nowrap shadow-[0_2px_8px_rgba(45,156,219,0.3)] transition-colors hover:bg-blue-hover hover:shadow-[0_4px_12px_rgba(45,156,219,0.4)] active:bg-blue-active"
              @click="router.push({ path: '/search', query: { dong: heroInput, type: heroTxType } })"
            >검색하기</button>
          </div>
        </div>
        <div class="flex flex-wrap justify-center gap-2 mt-5">
          <button
            v-for="tag in ['역삼동', '서초동', '잠실동', '합정동']"
            :key="tag"
            class="px-3 py-1 rounded-full bg-white/10 text-white/80 border border-white/15 text-xs cursor-pointer transition-colors hover:bg-white/20 hover:text-white"
            @click="heroInput = tag"
          >{{ { 역삼동: '강남구 역삼동', 서초동: '서초구 서초동', 잠실동: '송파구 잠실동', 합정동: '마포구 합정동' }[tag] }}</button>
        </div>
      </div>
    </div>
  </section>

  <section class="bg-bg-page py-16">
    <div class="max-w-[80rem] mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-10">
        <h2 class="text-navy text-[1.375rem] font-semibold mb-2">실시간 부동산 시장 현황</h2>
        <p class="text-gray-500 text-sm">공공데이터 기반 최신 거래 통계를 확인하세요</p>
      </div>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mt-10">
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] transition-shadow hover:shadow-[0_4px_12px_rgba(0,0,0,0.08)]">
          <div class="flex items-start justify-between mb-4">
            <div class="w-11 h-11 rounded-xl flex items-center justify-center" style="background:rgba(45,156,219,0.1)">
              <BarChart3 style="color:#2D9CDB" :size="20" />
            </div>
            <div v-if="stats.thisMonthDealCountChange != null"
              class="flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold"
              :class="stats.thisMonthDealCountChange >= 0 ? 'bg-[#f0fdf4] text-[#16a34a]' : 'bg-[#fef2f2] text-red'"
            >
              <TrendingUp v-if="stats.thisMonthDealCountChange >= 0" :size="14" />
              <TrendingDown v-else :size="14" />
              {{ (stats.thisMonthDealCountChange >= 0 ? '+' : '') + stats.thisMonthDealCountChange.toFixed(1) }}%
            </div>
          </div>
          <p class="text-gray-500 text-[0.8125rem] mb-1">이번 달 거래량</p>
          <div class="flex items-baseline gap-1">
            <span class="text-navy text-[1.75rem] font-bold">{{ stats.thisMonthDealCount != null ? stats.thisMonthDealCount.toLocaleString() : '-' }}</span>
            <span class="text-gray-400 text-sm">건</span>
          </div>
          <p class="text-gray-400 text-xs mt-2">전월 대비</p>
        </div>

        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] transition-shadow hover:shadow-[0_4px_12px_rgba(0,0,0,0.08)]">
          <div class="flex items-start justify-between mb-4">
            <div class="w-11 h-11 rounded-xl flex items-center justify-center" style="background:rgba(26,60,110,0.1)">
              <Building2 style="color:#1A3C6E" :size="20" />
            </div>
            <div v-if="stats.avgSalePriceChange != null"
              class="flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold"
              :class="stats.avgSalePriceChange >= 0 ? 'bg-[#f0fdf4] text-[#16a34a]' : 'bg-[#fef2f2] text-red'"
            >
              <TrendingUp v-if="stats.avgSalePriceChange >= 0" :size="14" />
              <TrendingDown v-else :size="14" />
              {{ (stats.avgSalePriceChange >= 0 ? '+' : '') + stats.avgSalePriceChange.toFixed(1) }}%
            </div>
          </div>
          <p class="text-gray-500 text-[0.8125rem] mb-1">평균 매매가</p>
          <div class="flex items-baseline gap-1">
            <span class="text-navy text-[1.75rem] font-bold">{{ formatKoreanManwon(stats.avgSalePrice) }}</span>
            <span class="text-gray-400 text-sm">만원</span>
          </div>
          <p class="text-gray-400 text-xs mt-2">전월 대비</p>
        </div>

        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_1px_3px_rgba(0,0,0,0.05)] transition-shadow hover:shadow-[0_4px_12px_rgba(0,0,0,0.08)]">
          <div class="flex items-start justify-between mb-4">
            <div class="w-11 h-11 rounded-xl flex items-center justify-center" style="background:rgba(16,185,129,0.1)">
              <Landmark style="color:#059669" :size="20" />
            </div>
            <div v-if="stats.avgLeasePriceChange != null"
              class="flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold"
              :class="stats.avgLeasePriceChange >= 0 ? 'bg-[#f0fdf4] text-[#16a34a]' : 'bg-[#fef2f2] text-red'"
            >
              <TrendingUp v-if="stats.avgLeasePriceChange >= 0" :size="14" />
              <TrendingDown v-else :size="14" />
              {{ (stats.avgLeasePriceChange >= 0 ? '+' : '') + stats.avgLeasePriceChange.toFixed(1) }}%
            </div>
          </div>
          <p class="text-gray-500 text-[0.8125rem] mb-1">평균 전세가</p>
          <div class="flex items-baseline gap-1">
            <span class="text-navy text-[1.75rem] font-bold">{{ formatKoreanManwon(stats.avgLeasePrice) }}</span>
            <span class="text-gray-400 text-sm">만원</span>
          </div>
          <p class="text-gray-400 text-xs mt-2">전월 대비</p>
        </div>
      </div>
    </div>
  </section>
</template>
