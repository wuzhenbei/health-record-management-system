import request from '@/utils/request'

export function getOverview() {
  return request.get('/stats/overview')
}

export function getChronicByType() {
  return request.get('/stats/chronic-by-type')
}

export function getChronicByGender() {
  return request.get('/stats/chronic-by-gender')
}

export function getChronicByAge() {
  return request.get('/stats/chronic-by-age')
}

export function getFollowupTrend() {
  return request.get('/stats/followup-trend')
}

export function getResidentTrend() {
  return request.get('/stats/resident-trend')
}

export function getPendingFollowup() {
  return request.get('/stats/pending-followup')
}
