import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/device/deviceInfo/list',
  save = '/device/deviceInfo/add',
  edit = '/device/deviceInfo/edit',
  get = '/device/deviceInfo/queryById',
  delete = '/device/deviceInfo/delete',
  deleteBatch = '/device/deviceInfo/deleteBatch',
  attachmentList = '/device/deviceAttachment/list',
  attachmentUpload = '/device/deviceAttachment/upload',
  attachmentDelete = '/device/deviceAttachment/delete',
}

/** 设备档案-分页列表 */
export const getDeviceList = (params) => defHttp.get({ url: Api.list, params });

/** 设备档案-新增/编辑 */
export const saveOrUpdateDevice = (params, isUpdate) => {
  return defHttp.post({ url: isUpdate ? Api.edit : Api.save, params });
};

/** 设备档案-详情 */
export const getDeviceById = (params) => defHttp.get({ url: Api.get, params });

/** 设备档案-删除（级联删除附件） */
export const deleteDevice = (params) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true });
};

/** 设备档案-批量删除（级联删除附件） */
export const batchDeleteDevice = (params) => {
  return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true });
};

/** 设备附件-列表 */
export const getAttachmentList = (deviceId: string) => {
  return defHttp.get({ url: Api.attachmentList, params: { deviceId } });
};

/** 设备附件-上传 */
export const uploadAttachment = (params: { file: File; deviceId: string; attachmentType: string }, onUploadProgress?: (e: ProgressEvent) => void) => {
  return defHttp.uploadFile(
    {
      url: Api.attachmentUpload,
      onUploadProgress,
    },
    {
      file: params.file,
      name: 'file',
      data: { deviceId: params.deviceId, attachmentType: params.attachmentType },
    },
    // 返回完整响应体（含 success/message/result），由页面自行处理提示
    { isReturnResponse: true }
  );
};

/** 设备附件-删除 */
export const deleteAttachment = (id: string) => {
  return defHttp.delete({ url: Api.attachmentDelete, data: { id } }, { joinParamsToUrl: true });
};
