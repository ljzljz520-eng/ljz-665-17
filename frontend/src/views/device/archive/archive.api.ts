import { defHttp } from '/@/utils/http/axios';
import { useGlobSetting } from '/@/hooks/setting';

const { apiUrl } = useGlobSetting();

enum Api {
  list = '/device/archive/list',
  save = '/device/archive/add',
  edit = '/device/archive/edit',
  get = '/device/archive/queryById',
  delete = '/device/archive/delete',
  deleteBatch = '/device/archive/deleteBatch',
  attachmentList = '/device/attachment/listByDevice',
  attachmentAdd = '/device/attachment/add',
  attachmentDelete = '/device/attachment/delete',
}

/**
 * 设备档案-分页列表
 */
export const getDeviceList = (params) => {
  return defHttp.get({ url: Api.list, params });
};

/**
 * 设备档案-新增/编辑
 */
export const saveOrUpdateDevice = (params, isUpdate) => {
  return defHttp.post({ url: isUpdate ? Api.edit : Api.save, params });
};

/**
 * 设备档案-详情（含附件列表）
 */
export const getDeviceById = (params) => {
  return defHttp.get({ url: Api.get, params });
};

/**
 * 设备档案-删除（后端级联删除附件）
 */
export const deleteDevice = (params) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true });
};

/**
 * 设备档案-批量删除
 */
export const batchDeleteDevice = (params) => {
  return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true });
};

/**
 * 设备附件-列表
 */
export const getAttachmentList = (deviceId: string) => {
  return defHttp.get({ url: Api.attachmentList, params: { deviceId } });
};

/**
 * 设备附件-保存附件关系记录（文件先走 /sys/common/upload）
 */
export const saveAttachment = (params) => {
  return defHttp.post({ url: Api.attachmentAdd, params });
};

/**
 * 设备附件-删除
 */
export const deleteAttachment = (params) => {
  return defHttp.delete({ url: Api.attachmentDelete, data: params }, { joinParamsToUrl: true });
};

/**
 * 通用文件上传（返回完整响应体，message 为文件相对路径）
 */
export const uploadDeviceFile = (params) => {
  // config.baseURL 会被强制设为 uploadUrl（docker 下为容器内地址），
  // 这里显式给完整同源 URL，保证浏览器在 dev / docker 部署下都能访问
  return defHttp.uploadFile({ baseURL: '', url: `${apiUrl}/sys/common/upload` }, params, { isReturnResponse: true });
};
