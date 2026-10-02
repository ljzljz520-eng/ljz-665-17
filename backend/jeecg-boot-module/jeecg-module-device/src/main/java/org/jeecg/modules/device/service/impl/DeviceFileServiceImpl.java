package org.jeecg.modules.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.device.entity.DeviceFile;
import org.jeecg.modules.device.mapper.DeviceFileMapper;
import org.jeecg.modules.device.service.IDeviceFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class DeviceFileServiceImpl extends ServiceImpl<DeviceFileMapper, DeviceFile> implements IDeviceFileService {

    /**
     * 本地上传根目录（uploadType=local 时物理文件存放在此，仅用于尽力清理）
     */
    @Value("${jeecg.path.upload:}")
    private String uploadPath;

    @Override
    public List<DeviceFile> listByDeviceId(String deviceId) {
        return baseMapper.selectFilesByDeviceId(deviceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(String id, String deviceId) {
        DeviceFile deviceFile = baseMapper.selectById(id);
        if (deviceFile == null) {
            return false;
        }
        // 归属校验：附件必须属于当前设备
        if (deviceId != null && !deviceId.equals(deviceFile.getDeviceId())) {
            return false;
        }
        baseMapper.deleteById(id);
        // 物理文件尽力删除，失败不影响数据库事务
        deletePhysicalFileQuietly(deviceFile);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DeviceFile> deleteByDeviceId(String deviceId) {
        List<DeviceFile> files = baseMapper.selectFilesByDeviceId(deviceId);
        baseMapper.deleteByDeviceId(deviceId);
        // 物理文件尽力删除，失败不影响数据库事务
        for (DeviceFile file : files) {
            deletePhysicalFileQuietly(file);
        }
        return files;
    }

    /**
     * 尽力删除本地上传的物理文件；OSS/MinIO 等远端存储仅清理数据库记录。
     */
    private void deletePhysicalFileQuietly(DeviceFile deviceFile) {
        try {
            String relativePath = deviceFile.getFilePath();
            if (relativePath == null || relativePath.isEmpty() || uploadPath == null || uploadPath.isEmpty()) {
                return;
            }
            if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
                return;
            }
            File file = new File(uploadPath + File.separator + relativePath);
            if (file.exists() && !file.delete()) {
                log.warn("设备附件物理文件删除失败: {}", file.getAbsolutePath());
            }
        } catch (Exception e) {
            log.warn("设备附件物理文件删除异常: {}", e.getMessage());
        }
    }
}
