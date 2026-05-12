import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserInfo {
  userId: number
  username: string
  realName: string
  role: string
  avatar?: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  function setToken(t: string) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function setUserInfo(info: UserInfo) {
    userInfo.value = info
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  function isAdmin() {
    return userInfo.value?.role === 'ADMIN'
  }

  function isDoctor() {
    return userInfo.value?.role === 'DOCTOR'
  }

  function isResident() {
    return userInfo.value?.role === 'RESIDENT'
  }

  return { token, userInfo, setToken, setUserInfo, logout, isAdmin, isDoctor, isResident }
})
