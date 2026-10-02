/**
 * 设备档案
 */
export interface DeviceModel {
  id?: string;
  deviceCode?: string;
  deviceName?: string;
  model?: string;
  manufacturer?: string;
  location?: string;
  useDate?: string;
  status?: string;
  remark?: string;
  createBy?: string;
  createTime?: string;
}

/**
 * 设备附件
 */
export interface DeviceFileModel {
  id: string;
  deviceId: string;
  fileName: string;
  filePath: string;
  fileSize?: number;
  fileType?: string;
  category: 'photo' | 'manual' | 'contract' | 'other';
  createBy?: string;
  createByName?: string;
  createTime?: string;
}
