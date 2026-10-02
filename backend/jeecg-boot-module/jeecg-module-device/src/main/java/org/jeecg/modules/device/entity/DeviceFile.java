package org.jeecg.modules.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 设备附件（设备照片 / 说明书 / 维保合同等）
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "设备附件")
@TableName("biz_device_file")
public class DeviceFile implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;

    /**
     * 所属设备ID
     */
    @Schema(description = "所属设备ID")
    private String deviceId;

    /**
     * 附件名称（展示用，取上传时的原始文件名）
     */
    @Excel(name = "附件名称", width = 40)
    @Schema(description = "附件名称")
    private String fileName;

    /**
     * 文件存储相对路径（/sys/common/upload 返回值）
     */
    @Schema(description = "文件存储路径")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件类型（扩展名，如 jpg/pdf/docx）
     */
    @Excel(name = "文件类型", width = 15)
    @Schema(description = "文件类型")
    private String fileType;

    /**
     * 附件分类：photo 设备照片 / manual 说明书 / contract 维保合同 / other 其他
     */
    @Excel(name = "附件分类", width = 15)
    @Schema(description = "附件分类")
    private String category;

    /**
     * 上传人账号（由拦截器自动填充）
     */
    @Excel(name = "上传人", width = 15)
    @Schema(description = "上传人账号")
    private String createBy;

    /**
     * 上传人姓名（查询时关联 sys_user 回填，非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "上传人姓名")
    private String createByName;

    /**
     * 上传时间
     */
    @Excel(name = "上传时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "上传时间")
    private Date createTime;
}
