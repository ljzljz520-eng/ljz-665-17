package org.jeecg.modules.device.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.base.entity.JeecgEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "设备档案")
@TableName("device_archive")
public class DeviceArchive extends JeecgEntity implements Serializable {
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

    /** 安装位置 */
    @Schema(description = "安装位置")
    private String location;

    /** 启用日期 */
    @Schema(description = "启用日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date useDate;

    /** 设备状态（1正常 0停用） */
    @Schema(description = "设备状态")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 附件列表（非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "附件列表")
    private List<DeviceAttachment> attachmentList;
}
