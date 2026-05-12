import request from '@/utils/request'

export function getChronicPage(params: any) {
  return request.get('/chronic-diseases', { params })
}

export function createChronic(data: any) {
  return request.post('/chronic-diseases', data)
}

export function updateChronic(id: number, data: any) {
  return request.put(`/chronic-diseases/${id}`, data)
}

export function deleteChronic(id: number) {
  return request.delete(`/chronic-diseases/${id}`)
}
