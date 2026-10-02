package org.jeecg.modules.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.device.entity.DeviceInfo;
import org.jeecg.modules.device.mapper.DeviceInfoMapper;
import org.jeecg.modules.device.service.IDeviceAttachmentService;
import org.jeecg.modules.device.service.IDeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfo> implements IDeviceInfoService {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private IDeviceAttachmentService deviceAttachmentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceCascade(String id) {
        deviceInfoMapper.deleteById(id);
        // 级联清理附件记录，删除设备后不留孤儿数据；物理文件尽力删除
        deviceAttachmentService.deleteCascadeByDeviceId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceBatchCascade(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            deviceInfoMapper.deleteById(id);
            deviceAttachmentService.deleteCascadeByDeviceId(id.toString());
        }
    }
}
