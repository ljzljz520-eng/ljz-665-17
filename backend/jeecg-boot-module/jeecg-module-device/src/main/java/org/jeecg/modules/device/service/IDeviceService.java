package org.jeecg.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.device.entity.Device;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
public interface IDeviceService extends IService<Device> {

    /**
     * 删除设备，同时清理其下附件记录（同事务，杜绝孤儿附件）
     *
     * @param id 设备ID
     */
    void deleteDeviceCascade(String id);

    /**
     * 批量删除设备，同时清理各设备下的附件记录（同事务）
     *
     * @param idList 设备ID集合
     */
    void deleteDeviceBatchCascade(Collection<? extends Serializable> idList);
}
