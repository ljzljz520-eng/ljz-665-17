package org.jeecg.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.device.entity.DeviceAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
public interface IDeviceAttachmentService extends IService<DeviceAttachment> {

    /**
     * 查询某台设备的全部附件
     */
    List<DeviceAttachment> listByDeviceId(String deviceId);

    /**
     * 上传附件并绑定到指定设备
     *
     * @param deviceId       设备ID
     * @param attachmentType 附件类型 photo/manual/contract/other
     * @param file           文件
     */
    DeviceAttachment upload(String deviceId, String attachmentType, MultipartFile file) throws Exception;

    /**
     * 删除单个附件（记录 + 物理文件），需校验归属
     */
    void deleteAttachment(String id);

    /**
     * 删除设备时级联清理：删除该设备下全部附件记录，并尽力删除物理文件。
     * 与设备删除处于同一事务内，保证不留孤儿记录。
     */
    void deleteCascadeByDeviceId(String deviceId);
}
