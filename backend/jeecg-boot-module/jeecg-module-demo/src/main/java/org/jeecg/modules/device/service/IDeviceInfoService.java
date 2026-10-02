package org.jeecg.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.device.entity.DeviceInfo;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
public interface IDeviceInfoService extends IService<DeviceInfo> {

    /**
     * 删除设备档案，同时级联删除附件记录与物理文件
     */
    void deleteDeviceCascade(String id);

    /**
     * 批量删除设备档案，同时级联删除附件记录与物理文件
     */
    void deleteDeviceBatchCascade(Collection<? extends Serializable> idList);
}
