package org.jeecg.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.device.entity.DeviceAttachment;

import java.util.List;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
public interface IDeviceAttachmentService extends IService<DeviceAttachment> {

    /**
     * 查询设备附件列表（含上传人姓名）
     *
     * @param deviceId 设备档案ID
     * @return 附件列表
     */
    List<DeviceAttachment> listByDeviceId(String deviceId);

    /**
     * 保存附件
     *
     * @param attachment 附件信息
     */
    void saveAttachment(DeviceAttachment attachment);

    /**
     * 删除单个附件，同时删除物理文件
     *
     * @param id 附件ID
     */
    void deleteAttachment(String id);

    /**
     * 按设备档案ID删除全部附件（含物理文件），用于设备删除时级联清理
     *
     * @param deviceIds 设备档案ID集合
     */
    void deleteByDeviceIds(List<String> deviceIds);
}
