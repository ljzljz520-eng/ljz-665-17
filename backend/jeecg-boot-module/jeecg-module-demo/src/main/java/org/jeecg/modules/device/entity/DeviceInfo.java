package org.jeecg.modules.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.base.entity.JeecgEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("device_info")
@Schema(description = "设备档案")
public class DeviceInfo extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 设备编号 */
    @Schema(description = "设备编号")
    private String deviceCode;

    /** 设备名称 */
    @Schema(description = "设备名称")
    private String deviceName;

    /** 设备型号 */
    @Schema(description = "设备型号")
    private String deviceModel;

    /** 生产厂家 */
    @Schema(description = "生产厂家")
    private String manufacturer;

    /** 出厂序列号 */
    @Schema(description = "出厂序列号")
    private String serialNumber;

    /** 安装位置 */
    @Schema(description = "安装位置")
    private String location;

    /** 启用日期 */
    @Schema(description = "启用日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date useDate;

    /** 质保到期日 */
    @Schema(description = "质保到期日")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warrantyDate;

    /** 设备状态（1正常 2维修中 3停用） */
    @Schema(description = "设备状态")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
