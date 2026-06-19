<template>
  <div class="profile-page">
    <div v-if="profileError" style="max-width:40rem;margin:2rem auto;display:flex;align-items:center;gap:0.5rem;padding:1rem 1.25rem;background:#FEF2F2;border:1px solid #FECACA;border-radius:0.75rem;color:#DC2626;font-size:0.875rem">
      <AlertCircle :size="16" style="flex-shrink:0" />
      {{ profileError }}
    </div>
    <div class="profile-wrap" v-else-if="profile">
      <div class="card card-lg" style="margin-bottom:1.5rem;overflow:hidden">
        <div class="profile-banner">
          <div class="profile-avatar-wrap">
            <div class="profile-avatar"><User :size="32" /></div>
          </div>
        </div>
        <div class="profile-name-section">
          <h1 style="color:#1A3C6E;font-size:1.375rem;font-weight:700">{{ profile.name }}</h1>
          <p style="color:#9ca3af;font-size:0.875rem;margin-top:0.25rem">{{ profile.email }}</p>
        </div>
      </div>

      <div class="card card-lg" style="overflow:hidden">
        <!-- 조회 모드 -->
        <div v-if="!editMode">
          <div class="profile-section-header"><h2>회원 정보</h2></div>
          <div style="padding:1.25rem 1.5rem;display:flex;flex-direction:column;gap:1rem">
            <div class="profile-info-row">
              <div class="profile-info-icon"><UserCircle :size="18" /></div>
              <div><p class="profile-info-label">이름</p><p class="profile-info-value">{{ profile.name }}</p></div>
            </div>
            <div class="profile-info-row">
              <div class="profile-info-icon"><Mail :size="18" /></div>
              <div><p class="profile-info-label">이메일</p><p class="profile-info-value">{{ profile.email }}</p></div>
            </div>
            <div class="profile-actions">
              <button class="btn btn-primary btn-full" @click="startEdit"><Pencil :size="16" /> 정보 수정</button>
              <button class="btn btn-outline-danger btn-full" @click="showDeleteModal = true"><Trash2 :size="16" /> 회원 탈퇴</button>
            </div>
          </div>
        </div>

        <!-- 수정 모드 -->
        <div v-else>
          <div class="profile-section-header">
            <h2>정보 수정</h2>
            <button class="cancel-close-btn" @click="editMode = false"><X :size="16" /></button>
          </div>
          <form @submit.prevent="handleUpdate" novalidate style="padding:1.25rem 1.5rem;display:flex;flex-direction:column;gap:1rem">
            <div class="form-group">
              <label class="form-label" for="edit-name">이름</label>
              <div class="input-icon-wrap">
                <span class="input-icon"><UserCircle :size="16" /></span>
                <input id="edit-name" v-model="editName" type="text" class="input-base" />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label" for="edit-password">새 비밀번호 (필수)</label>
              <div class="input-icon-wrap">
                <span class="input-icon"><Lock :size="16" /></span>
                <input id="edit-password" v-model="editPassword" type="password" class="input-base" placeholder="8자 이상" />
              </div>
            </div>
            <div style="display:flex;gap:0.75rem;margin-top:0.5rem">
              <button type="submit" class="btn btn-primary btn-full" :disabled="saving">
                <Loader2 v-if="saving" :size="16" class="animate-spin" />
                {{ saving ? '저장 중...' : '저장하기' }}
              </button>
              <button type="button" class="btn btn-ghost btn-full" @click="editMode = false">취소</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 탈퇴 확인 모달 -->
    <div v-if="showDeleteModal" class="modal-overlay visible">
      <div class="modal-backdrop" @click="showDeleteModal = false"></div>
      <div class="modal-box">
        <div style="padding:2rem 1.5rem 0.5rem;text-align:center">
          <div class="confirm-modal-icon"><AlertTriangle :size="24" /></div>
          <h3 style="color:#1A3C6E;font-size:1.125rem;font-weight:700;margin-bottom:0.5rem">정말 탈퇴하시겠습니까?</h3>
          <p style="color:#9ca3af;font-size:0.8125rem">탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다.</p>
        </div>
        <div style="display:flex;gap:0.75rem;padding:1.5rem">
          <button class="btn btn-ghost btn-full" @click="showDeleteModal = false">취소</button>
          <button class="btn btn-danger btn-full" :disabled="deleting" @click="handleDelete">
            <Loader2 v-if="deleting" :size="16" class="animate-spin" />
            {{ deleting ? '처리 중...' : '탈퇴하기' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, UserCircle, Mail, Lock, Pencil, Trash2, X, AlertTriangle, AlertCircle, Loader2 } from 'lucide-vue-next'
import { membersApi } from '../api/index.js'
import { useAuthStore } from '../stores/auth.js'
import '../../css/pages/profile.css'

const router = useRouter()
const authStore = useAuthStore()

const profile = ref(null)
const profileError = ref(null)
const editMode = ref(false)
const editName = ref('')
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
  editPassword.value = ''
  editMode.value = true
}

async function handleUpdate() {
  if (!editName.value.trim() || editPassword.value.length < 8) return
  saving.value = true
  try {
    await membersApi.updateMyMember({ name: editName.value.trim(), password: editPassword.value })
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
