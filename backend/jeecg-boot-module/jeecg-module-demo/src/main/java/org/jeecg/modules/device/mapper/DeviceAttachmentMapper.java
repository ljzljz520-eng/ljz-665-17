package org.jeecg.modules.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.device.entity.DeviceAttachment;

import java.util.List;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
public interface DeviceAttachmentMapper extends BaseMapper<DeviceAttachment> {

    /**
     * 根据设备ID查询附件列表
     */
    @Select("SELECT * FROM device_attachment WHERE device_id = #{deviceId} ORDER BY create_time DESC")
    List<DeviceAttachment> selectByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 根据设备ID删除全部附件（删除设备时级联清理，避免孤儿记录）
     */
    @Delete("DELETE FROM device_attachment WHERE device_id = #{deviceId}")
    int deleteByDeviceId(@Param("deviceId") String deviceId);
}
