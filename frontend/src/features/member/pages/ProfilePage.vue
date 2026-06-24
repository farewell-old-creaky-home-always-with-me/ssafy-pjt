<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, UserCircle, Mail, Lock, Pencil, Trash2, X, AlertTriangle, AlertCircle, Loader2, Phone } from 'lucide-vue-next'
import { membersApi } from '@/api/index.js'
import { useAuthStore } from '@/stores/authStore.js'
import BaseButton from '@/components/base/BaseButton.vue'

const router = useRouter()
const authStore = useAuthStore()

const profile = ref(null)
const profileError = ref(null)
const editMode = ref(false)
const editName = ref('')
const editPhone = ref('')
const editPassword = ref('')
const saving = ref(false)
const showDeleteModal = ref(false)
const deleting = ref(false)

onMounted(async () => {
  try {
    profile.value = await membersApi.getMyMember()
  } catch {
    profileError.value = '프로필 정보를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.'
  }
})

function startEdit() {
  editName.value = profile.value.name
  editPhone.value = profile.value.phone ?? ''
  editPassword.value = ''
  editMode.value = true
}

async function handleUpdate() {
  if (!editName.value.trim() || !editPhone.value.trim() || editPassword.value.length < 8) return
  saving.value = true
  try {
    await membersApi.updateMyMember({
      name: editName.value.trim(),
      password: editPassword.value,
      phone: editPhone.value.trim()
    })
    profile.value = await membersApi.getMyMember()
    authStore.patchUser({ name: profile.value.name })
    editMode.value = false
  } catch (err) {
    alert(err.data?.message ?? '저장에 실패했습니다')
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  deleting.value = true
  try {
    await membersApi.deleteMyMember()
    await authStore.logout()
    router.push('/login')
  } catch (err) {
    alert(err.data?.message ?? '탈퇴에 실패했습니다')
  } finally {
    deleting.value = false
    showDeleteModal.value = false
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-64px)] bg-bg-page py-10 px-4">
    <div v-if="profileError" class="max-w-[40rem] mx-auto mt-8 flex items-center gap-2 px-5 py-4 bg-[#FEF2F2] border border-[#FECACA] rounded-xl text-[#DC2626] text-sm">
      <AlertCircle :size="16" class="shrink-0" />
      {{ profileError }}
    </div>

    <div v-else-if="profile" class="max-w-[520px] mx-auto">
      <!-- 프로필 카드 -->
      <div class="bg-white rounded-2xl shadow-[0_4px_16px_rgba(0,0,0,0.08)] mb-6 overflow-hidden">
        <div class="h-28 bg-gradient-to-br from-navy to-blue relative rounded-t-2xl">
          <div class="absolute bottom-0 left-1/2 translate-y-1/2 -translate-x-1/2">
            <div class="w-20 h-20 rounded-full bg-white border-4 border-white shadow-[0_4px_12px_rgba(0,0,0,0.1)] flex items-center justify-center text-blue">
              <User :size="32" />
            </div>
          </div>
        </div>
        <div class="pt-14 pb-6 text-center px-6">
          <h1 class="text-navy text-[1.375rem] font-bold">{{ profile.name }}</h1>
          <p class="text-gray-400 text-sm mt-1">{{ profile.email }}</p>
        </div>
      </div>

      <!-- 정보 카드 -->
      <div class="bg-white rounded-2xl shadow-[0_4px_16px_rgba(0,0,0,0.08)] overflow-hidden">
        <!-- 조회 모드 -->
        <div v-if="!editMode">
          <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
            <h2 class="text-navy text-base font-semibold">회원 정보</h2>
          </div>
          <div class="px-6 py-5 flex flex-col gap-4">
            <div class="flex items-center gap-3 bg-bg-page rounded-xl px-4 py-[0.875rem]">
              <div class="w-9 h-9 rounded-lg bg-white flex items-center justify-center shrink-0 shadow-[0_1px_3px_rgba(0,0,0,0.06)]">
                <UserCircle :size="18" class="text-blue" />
              </div>
              <div>
                <p class="text-gray-400 text-[0.6875rem] font-medium">이름</p>
                <p class="text-navy text-sm font-semibold">{{ profile.name }}</p>
              </div>
            </div>
            <div class="flex items-center gap-3 bg-bg-page rounded-xl px-4 py-[0.875rem]">
              <div class="w-9 h-9 rounded-lg bg-white flex items-center justify-center shrink-0 shadow-[0_1px_3px_rgba(0,0,0,0.06)]">
                <Mail :size="18" class="text-blue" />
              </div>
              <div>
                <p class="text-gray-400 text-[0.6875rem] font-medium">이메일</p>
                <p class="text-navy text-sm font-semibold">{{ profile.email }}</p>
              </div>
            </div>
            <div class="flex items-center gap-3 bg-bg-page rounded-xl px-4 py-[0.875rem]">
              <div class="w-9 h-9 rounded-lg bg-white flex items-center justify-center shrink-0 shadow-[0_1px_3px_rgba(0,0,0,0.06)]">
                <Phone :size="18" class="text-blue" />
              </div>
              <div>
                <p class="text-gray-400 text-[0.6875rem] font-medium">전화번호</p>
                <p class="text-navy text-sm font-semibold">{{ profile.phone }}</p>
              </div>
            </div>
            <div class="flex gap-3 mt-2">
              <BaseButton :full="true" @click="startEdit"><Pencil :size="16" /> 정보 수정</BaseButton>
              <BaseButton variant="outline-danger" :full="true" @click="showDeleteModal = true"><Trash2 :size="16" /> 회원 탈퇴</BaseButton>
            </div>
          </div>
        </div>

        <!-- 수정 모드 -->
        <div v-else>
          <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
            <h2 class="text-navy text-base font-semibold">정보 수정</h2>
            <button
              class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 transition-colors hover:bg-bg-page hover:text-gray-500"
              @click="editMode = false"
            ><X :size="16" /></button>
          </div>
          <form @submit.prevent="handleUpdate" novalidate class="px-6 py-5 flex flex-col gap-4">
            <div>
              <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="edit-name">이름</label>
              <div class="relative">
                <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><UserCircle :size="16" /></span>
                <input id="edit-name" v-model="editName" type="text"
                  class="w-full pl-11 pr-4 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]" />
              </div>
            </div>
            <div>
              <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="edit-phone">전화번호</label>
              <div class="relative">
                <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><Phone :size="16" /></span>
                <input id="edit-phone" v-model="editPhone" type="tel"
                  class="w-full pl-11 pr-4 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]" />
              </div>
            </div>
            <div>
              <label class="block text-navy text-[0.8125rem] font-semibold mb-2" for="edit-password">새 비밀번호 (필수)</label>
              <div class="relative">
                <span class="absolute left-[0.875rem] top-1/2 -translate-y-1/2 pointer-events-none text-gray-400"><Lock :size="16" /></span>
                <input id="edit-password" v-model="editPassword" type="password"
                  class="w-full pl-11 pr-4 py-3 rounded-xl bg-bg-page border border-[#e5e7eb] text-navy text-sm outline-none transition-all placeholder:text-gray-400 focus:border-blue focus:shadow-[0_0_0_3px_rgba(45,156,219,0.15)]"
                  placeholder="8자 이상" />
              </div>
            </div>
            <div class="flex gap-3 mt-2">
              <BaseButton type="submit" :full="true" :disabled="saving">
                <Loader2 v-if="saving" :size="16" class="animate-spin" />
                {{ saving ? '저장 중...' : '저장하기' }}
              </BaseButton>
              <BaseButton variant="ghost" :full="true" type="button" @click="editMode = false">취소</BaseButton>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 탈퇴 확인 모달 -->
    <div v-if="showDeleteModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="showDeleteModal = false"></div>
      <div class="relative bg-white rounded-2xl shadow-[0_20px_60px_rgba(0,0,0,0.2)] w-full max-w-[380px] overflow-hidden">
        <div class="px-6 pt-8 pb-2 text-center">
          <div class="w-14 h-14 rounded-full bg-red/10 flex items-center justify-center mx-auto mb-4">
            <AlertTriangle :size="24" class="text-red" />
          </div>
          <h3 class="text-navy text-lg font-bold mb-2">정말 탈퇴하시겠습니까?</h3>
          <p class="text-gray-400 text-[0.8125rem]">탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다.</p>
        </div>
        <div class="flex gap-3 px-6 py-6">
          <BaseButton variant="ghost" :full="true" @click="showDeleteModal = false">취소</BaseButton>
          <BaseButton variant="danger" :full="true" :disabled="deleting" @click="handleDelete">
            <Loader2 v-if="deleting" :size="16" class="animate-spin" />
            {{ deleting ? '처리 중...' : '탈퇴하기' }}
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>
