import request from '@/utils/request'

export function getUserPage(params: any) {
  return request.get('/users', { params })
}

export function createUser(data: any) {
  return request.post('/users', data)
}

export function updateUser(id: number, data: any) {
  return request.put(`/users/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete(`/users/${id}`)
}

export function resetPassword(id: number, password: string) {
  return request.post(`/users/${id}/reset-password`, { password })
}

export function updateUserStatus(id: number, status: number) {
  return request.put(`/users/${id}/status`, { status })
}
