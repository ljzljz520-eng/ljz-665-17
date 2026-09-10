package org.jeecg.modules.device.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.device.entity.DeviceArchive;
import org.jeecg.modules.device.mapper.DeviceArchiveMapper;
import org.jeecg.modules.device.service.IDeviceArchiveService;
import org.jeecg.modules.device.service.IDeviceAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Slf4j
@Service
public class DeviceArchiveServiceImpl extends ServiceImpl<DeviceArchiveMapper, DeviceArchive> implements IDeviceArchiveService {

    @Autowired
    private IDeviceAttachmentService deviceAttachmentService;

    @Override
    public DeviceArchive getDetailById(String id) {
        DeviceArchive device = this.baseMapper.selectById(id);
        if (device != null) {
            device.setAttachmentList(deviceAttachmentService.listByDeviceId(id));
        }
        return device;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDevice(DeviceArchive deviceArchive) {
        this.baseMapper.insert(deviceArchive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDevice(DeviceArchive deviceArchive) {
        this.baseMapper.updateById(deviceArchive);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceById(String id) {
        List<String> ids = new ArrayList<>(1);
        ids.add(id);
        deleteDeviceBatch(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceBatch(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 先级联清理附件（含物理文件），再删除设备档案，事务保证不留下孤儿记录
        deviceAttachmentService.deleteByDeviceIds(ids);
        this.baseMapper.deleteBatchIds(ids);
    }
}
