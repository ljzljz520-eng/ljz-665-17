import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/device/device/list',
  save = '/device/device/add',
  edit = '/device/device/edit',
  get = '/device/device/queryById',
  delete = '/device/device/delete',
  deleteBatch = '/device/device/deleteBatch',
  // 附件相关
  fileList = '/device/deviceFile/listByDevice',
  fileUpload = '/device/deviceFile/upload',
  fileDelete = '/device/deviceFile/delete',
}

/**
 * 设备分页列表
 */
export const getDeviceList = (params) => {
  return defHttp.get({ url: Api.list, params });
};

/**
 * 新增/编辑设备
 */
export const saveOrUpdateDevice = (params, isUpdate) => {
  return defHttp.post({ url: isUpdate ? Api.edit : Api.save, params });
};

/**
 * 设备详情
 */
export const getDeviceById = (params) => {
  return defHttp.get({ url: Api.get, params });
};

/**
 * 删除设备（后端同事务级联删除附件）
 */
export const deleteDevice = (id: string) => {
  return defHttp.delete({ url: Api.delete, params: { id } }, { joinParamsToUrl: true });
};

/**
 * 批量删除设备
 */
export const batchDeleteDevice = (ids: string) => {
  return defHttp.delete({ url: Api.deleteBatch, params: { ids } }, { joinParamsToUrl: true });
};

/**
 * 查询设备附件列表
 */
export const getDeviceFileList = (deviceId: string) => {
  return defHttp.get({ url: Api.fileList, params: { deviceId } });
};

/**
 * 上传设备附件
 */
export const uploadDeviceFile = (params, options?: any) => {
  return defHttp.uploadFile({ url: Api.fileUpload }, params, options);
};

/**
 * 删除设备附件
 */
export const deleteDeviceFile = (id: string, deviceId: string) => {
  return defHttp.delete({ url: Api.fileDelete, params: { id, deviceId } }, { joinParamsToUrl: true });
};

export const deviceFileUploadUrl = Api.fileUpload;
