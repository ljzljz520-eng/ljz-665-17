package org.jeecg.modules.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.mapper.DeviceMapper;
import org.jeecg.modules.device.service.IDeviceFileService;
import org.jeecg.modules.device.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

    /**
     * 附件服务：设备删除时同事务联动清理附件
     */
    @Autowired
    @Lazy
    private IDeviceFileService deviceFileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceCascade(String id) {
        // 先删附件关系，再删设备，保证不会留下 device_id 悬空的孤儿记录
        deviceFileService.deleteByDeviceId(id);
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceBatchCascade(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            deviceFileService.deleteByDeviceId(id.toString());
            baseMapper.deleteById(id);
        }
    }
}
