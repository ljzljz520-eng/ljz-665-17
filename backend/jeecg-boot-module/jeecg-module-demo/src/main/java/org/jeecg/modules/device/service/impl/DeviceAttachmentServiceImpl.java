package org.jeecg.modules.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.MinioUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.device.entity.DeviceAttachment;
import org.jeecg.modules.device.entity.DeviceInfo;
import org.jeecg.modules.device.mapper.DeviceAttachmentMapper;
import org.jeecg.modules.device.mapper.DeviceInfoMapper;
import org.jeecg.modules.device.service.IDeviceAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Slf4j
@Service
public class DeviceAttachmentServiceImpl extends ServiceImpl<DeviceAttachmentMapper, DeviceAttachment> implements IDeviceAttachmentService {

    /** 允许的附件类型 */
    private static final List<String> ALLOWED_TYPES = List.of("photo", "manual", "contract", "other");

    @Autowired
    private DeviceAttachmentMapper deviceAttachmentMapper;
    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    /** 本地：local minio：minio 阿里：alioss */
    @Value("${jeecg.uploadType}")
    private String uploadType;

    @Value("${jeecg.path.upload}")
    private String uploadPath;

    @Override
    public List<DeviceAttachment> listByDeviceId(String deviceId) {
        return deviceAttachmentMapper.selectByDeviceId(deviceId);
    }

    @Override
    public DeviceAttachment upload(String deviceId, String attachmentType, MultipartFile file) throws Exception {
        if (oConvertUtils.isEmpty(deviceId)) {
            throw new JeecgBootException("设备ID不能为空");
        }
        DeviceInfo device = deviceInfoMapper.selectById(deviceId);
        if (device == null) {
            throw new JeecgBootException("设备档案不存在，无法上传附件");
        }
        if (file == null || file.isEmpty()) {
            throw new JeecgBootException("上传文件不能为空");
        }
        if (oConvertUtils.isEmpty(attachmentType) || !ALLOWED_TYPES.contains(attachmentType)) {
            attachmentType = "other";
        }
        // 按设备隔离存储目录，便于追溯与清理
        String bizPath = "device" + File.separator + deviceId;
        String savePath;
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
            savePath = CommonUtils.uploadLocal(file, bizPath, uploadPath);
        } else {
            savePath = CommonUtils.upload(file, bizPath, uploadType);
        }
        if (oConvertUtils.isEmpty(savePath)) {
            throw new JeecgBootException("文件上传失败");
        }

        DeviceAttachment attachment = new DeviceAttachment();
        attachment.setDeviceId(deviceId);
        attachment.setAttachmentType(attachmentType);
        attachment.setFileName(CommonUtils.getFileName(file.getOriginalFilename()));
        attachment.setFilePath(savePath);
        attachment.setFileSize(file.getSize());
        deviceAttachmentMapper.insert(attachment);
        return attachment;
    }

    @Override
    public void deleteAttachment(String id) {
        DeviceAttachment attachment = deviceAttachmentMapper.selectById(id);
        if (attachment == null) {
            throw new JeecgBootException("附件不存在或已被删除");
        }
        // 先删除数据库记录，保证不留下孤儿数据；物理文件删除失败仅记录日志
        deviceAttachmentMapper.deleteById(id);
        deletePhysicalFile(attachment);
    }

    /**
     * 删除设备时级联调用：删除该设备下所有附件记录与物理文件。
     * 由设备删除事务调用，保证设备与附件记录在同一事务内提交，
     * 物理文件删除失败仅记录日志，不影响数据一致性。
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void deleteCascadeByDeviceId(String deviceId) {
        List<DeviceAttachment> attachments = deviceAttachmentMapper.selectByDeviceId(deviceId);
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        deviceAttachmentMapper.deleteByDeviceId(deviceId);
        for (DeviceAttachment attachment : attachments) {
            deletePhysicalFile(attachment);
        }
    }

    /**
     * 尽力删除物理文件，失败不影响数据库事务
     */
    private void deletePhysicalFile(DeviceAttachment attachment) {
        String filePath = attachment.getFilePath();
        if (oConvertUtils.isEmpty(filePath)) {
            return;
        }
        try {
            if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
                // 库内存的是统一使用 "/" 的相对路径，删除前转换为本地路径分隔符
                File file = new File(uploadPath + File.separator + filePath.replace("/", File.separator));
                if (file.exists() && !file.delete()) {
                    log.warn("设备附件物理文件删除失败: {}", filePath);
                }
            } else if (CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)) {
                String prefix = MinioUtil.getMinioUrl() + MinioUtil.getBucketName() + "/";
                if (filePath.startsWith(prefix)) {
                    MinioUtil.removeObject(MinioUtil.getBucketName(), filePath.substring(prefix.length()));
                }
            }
            // alioss 由平台统一管理（官方工具默认不开放删除），这里仅清理数据库关系
        } catch (Exception e) {
            log.warn("设备附件物理文件删除异常: {}, {}", filePath, e.getMessage());
        }
    }
}
