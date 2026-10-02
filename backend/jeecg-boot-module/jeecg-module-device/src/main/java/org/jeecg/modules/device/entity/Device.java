package org.jeecg.modules.device.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.base.entity.JeecgEntity;
import org.jeecgframework.poi.excel.annotation.Excel;
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
@Schema(description = "设备档案")
@TableName("biz_device")
public class Device extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备编号（业务唯一）
     */
    @Excel(name = "设备编号", width = 20)
    @Schema(description = "设备编号")
    private String deviceCode;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称", width = 25)
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备型号（数据库列名 device_model，规避 model 在部分数据库中作为保留字的问题）
     */
    @TableField("device_model")
    @Excel(name = "设备型号", width = 20)
    @Schema(description = "设备型号")
    private String model;

    /**
     * 生产厂商
     */
    @Excel(name = "生产厂商", width = 25)
    @Schema(description = "生产厂商")
    private String manufacturer;

    /**
     * 安装位置
     */
    @Excel(name = "安装位置", width = 25)
    @Schema(description = "安装位置")
    private String location;

    /**
     * 启用日期
     */
    @Excel(name = "启用日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "启用日期")
    private Date useDate;

    /**
     * 设备状态：1正常 0停用（对应字典，可扩展）
     */
    @Excel(name = "设备状态", width = 15)
    @Schema(description = "设备状态")
    private String status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
