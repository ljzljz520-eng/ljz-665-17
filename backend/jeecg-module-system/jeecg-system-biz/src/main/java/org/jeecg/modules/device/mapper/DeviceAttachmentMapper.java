package org.jeecg.modules.device.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.device.entity.DeviceAttachment;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
public interface DeviceAttachmentMapper extends BaseMapper<DeviceAttachment> {

    /**
     * 查询设备附件列表，并关联用户表带出上传人真实姓名
     *
     * @param deviceId 设备档案ID
     * @return 附件列表
     */
    List<DeviceAttachment> queryByDeviceId(@Param("deviceId") String deviceId);
}
