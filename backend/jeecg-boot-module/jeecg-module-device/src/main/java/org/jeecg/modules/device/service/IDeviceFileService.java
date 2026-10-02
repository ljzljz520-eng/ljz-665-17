package org.jeecg.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.device.entity.DeviceFile;

import java.util.List;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
public interface IDeviceFileService extends IService<DeviceFile> {

    /**
     * 查询某台设备的全部附件（含上传人姓名）
     *
     * @param deviceId 设备ID
     * @return 附件列表
     */
    List<DeviceFile> listByDeviceId(String deviceId);

    /**
     * 删除单个附件（校验归属设备）
     *
     * @param id       附件ID
     * @param deviceId 所属设备ID
     * @return 是否删除成功
     */
    boolean deleteFile(String id, String deviceId);

    /**
     * 删除设备下全部附件记录，返回被删除的附件（用于尽力清理物理文件）
     *
     * @param deviceId 设备ID
     * @return 被删除附件列表
     */
    List<DeviceFile> deleteByDeviceId(String deviceId);
}
