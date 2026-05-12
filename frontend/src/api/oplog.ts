import request from '@/utils/request'

export function getOplogPage(params: any) {
  return request.get('/operation-logs', { params })
}
