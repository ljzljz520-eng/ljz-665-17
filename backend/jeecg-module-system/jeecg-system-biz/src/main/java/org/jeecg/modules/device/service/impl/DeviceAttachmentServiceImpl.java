package org.jeecg.modules.device.service.impl;

import java.io.File;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.device.entity.DeviceAttachment;
import org.jeecg.modules.device.mapper.DeviceAttachmentMapper;
import org.jeecg.modules.device.service.IDeviceAttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Slf4j
@Service
public class DeviceAttachmentServiceImpl extends ServiceImpl<DeviceAttachmentMapper, DeviceAttachment> implements IDeviceAttachmentService {

    /** 本地上传根目录（本地存储方式时用于删除物理文件） */
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    /** 存储方式：local 本地 / alioss 阿里云OSS */
    @Value(value = "${jeecg.uploadType}")
    private String uploadType;

    @Override
    public List<DeviceAttachment> listByDeviceId(String deviceId) {
        return this.baseMapper.queryByDeviceId(deviceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttachment(DeviceAttachment attachment) {
        this.baseMapper.insert(attachment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttachment(String id) {
        DeviceAttachment attachment = this.baseMapper.selectById(id);
        if (attachment == null) {
            return;
        }
        this.baseMapper.deleteById(id);
        // 数据库记录删除成功后再清理物理文件，清理失败仅记录日志，不影响主流程
        deletePhysicalFile(attachment.getFilePath());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByDeviceIds(List<String> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<DeviceAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DeviceAttachment::getDeviceId, deviceIds);
        List<DeviceAttachment> attachments = this.baseMapper.selectList(wrapper);
        if (attachments.isEmpty()) {
            return;
        }
        // 先删除附件关系记录，再逐个清理物理文件
        this.baseMapper.delete(wrapper);
        for (DeviceAttachment attachment : attachments) {
            deletePhysicalFile(attachment.getFilePath());
        }
    }

    /**
     * 删除物理文件（本地存储），OSS 等云存储暂不主动删除
     *
     * @param filePath 文件相对路径
     */
    private void deletePhysicalFile(String filePath) {
        if (oConvertUtils.isEmpty(filePath) || !CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
            return;
        }
        try {
            // 防御路径穿越
            String safePath = filePath.replace("..", "");
            File file = new File(uploadpath + File.separator + safePath);
            if (file.exists() && file.isFile()) {
                if (!file.delete()) {
                    log.warn("设备附件物理文件删除失败：{}", filePath);
                }
            }
        } catch (Exception e) {
            log.warn("设备附件物理文件删除异常：{}，{}", filePath, e.getMessage());
        }
    }
}
