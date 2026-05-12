import request from '@/utils/request'

export function getResidentPage(params: any) {
  return request.get('/residents', { params })
}

export function searchResidents(name: string) {
  return request.get('/residents/search', { params: { name } })
}

export function getResidentById(id: number) {
  return request.get(`/residents/${id}`)
}

export function getMyResident() {
  return request.get('/residents/my')
}

export function createResident(data: any) {
  return request.post('/residents', data)
}

export function updateResident(id: number, data: any) {
  return request.put(`/residents/${id}`, data)
}

export function deleteResident(id: number) {
  return request.delete(`/residents/${id}`)
}

export function getHealthIndicators(residentId: number) {
  return request.get(`/health-indicators/resident/${residentId}`)
}

export function createHealthIndicator(data: any) {
  return request.post('/health-indicators', data)
}

export function updateHealthIndicator(id: number, data: any) {
  return request.put(`/health-indicators/${id}`, data)
}

export function deleteHealthIndicator(id: number) {
  return request.delete(`/health-indicators/${id}`)
}

export function getMedicalHistories(residentId: number) {
  return request.get(`/medical-histories/resident/${residentId}`)
}

export function createMedicalHistory(data: any) {
  return request.post('/medical-histories', data)
}

export function updateMedicalHistory(id: number, data: any) {
  return request.put(`/medical-histories/${id}`, data)
}

export function deleteMedicalHistory(id: number) {
  return request.delete(`/medical-histories/${id}`)
}

export function getChronicByResident(residentId: number) {
  return request.get(`/chronic-diseases/resident/${residentId}`)
}
