package org.jeecg.modules.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.device.entity.DeviceFile;

import java.util.List;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
public interface DeviceFileMapper extends BaseMapper<DeviceFile> {

    /**
     * 按设备查询附件（关联用户表回填上传人姓名）
     *
     * @param deviceId 设备ID
     * @return 附件列表
     */
    List<DeviceFile> selectFilesByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 删除指定设备下的全部附件记录（设备删除时联动清理，避免孤儿记录）
     *
     * @param deviceId 设备ID
     * @return 删除条数
     */
    int deleteByDeviceId(@Param("deviceId") String deviceId);
}
