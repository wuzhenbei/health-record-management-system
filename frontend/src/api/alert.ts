import request from '@/utils/request'

export function getAlertPage(params: any) {
  return request.get('/alerts', { params })
}

export function handleAlert(id: number, note: string) {
  return request.put(`/alerts/${id}/handle`, { note })
}

export function getPendingAlertCount() {
  return request.get('/alerts/pending-count')
}

export function batchScanAlerts() {
  return request.post('/alerts/batch-scan')
}
