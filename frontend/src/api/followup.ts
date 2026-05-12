import request from '@/utils/request'

export function getFollowupPage(params: any) {
  return request.get('/followup-records', { params })
}

export function getFollowupByResident(residentId: number) {
  return request.get(`/followup-records/resident/${residentId}`)
}

export function createFollowup(data: any) {
  return request.post('/followup-records', data)
}

export function updateFollowup(id: number, data: any) {
  return request.put(`/followup-records/${id}`, data)
}

export function deleteFollowup(id: number) {
  return request.delete(`/followup-records/${id}`)
}
