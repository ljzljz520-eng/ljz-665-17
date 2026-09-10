package org.jeecg.modules.device.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.device.entity.DeviceArchive;
import org.jeecg.modules.device.entity.DeviceAttachment;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
public interface IDeviceArchiveService extends IService<DeviceArchive> {

    /**
     * 查询设备详情（含附件列表）
     *
     * @param id 设备档案ID
     * @return 设备详情
     */
    DeviceArchive getDetailById(String id);

    /**
     * 新增设备档案
     *
     * @param deviceArchive 设备档案
     */
    void saveDevice(DeviceArchive deviceArchive);

    /**
     * 编辑设备档案
     *
     * @param deviceArchive 设备档案
     */
    void updateDevice(DeviceArchive deviceArchive);

    /**
     * 删除设备档案，同时级联删除全部附件，避免产生孤儿记录
     *
     * @param id 设备档案ID
     */
    void deleteDeviceById(String id);

    /**
     * 批量删除设备档案，同时级联删除全部附件
     *
     * @param ids 设备档案ID集合
     */
    void deleteDeviceBatch(List<String> ids);
}
