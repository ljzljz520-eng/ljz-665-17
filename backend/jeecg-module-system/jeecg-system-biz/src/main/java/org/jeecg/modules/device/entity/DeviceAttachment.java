package org.jeecg.modules.device.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.base.entity.JeecgEntity;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "设备附件")
@TableName("device_attachment")
public class DeviceAttachment extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 设备档案ID */
    @Schema(description = "设备档案ID")
    private String deviceId;

    /** 附件类型（photo照片 manual说明书 contract维保合同 other其他） */
    @Schema(description = "附件类型")
    private String attachmentType;

    /** 文件名称（原始文件名） */
    @Schema(description = "文件名称")
    private String fileName;

    /** 文件存储路径（上传接口返回的相对路径） */
    @Schema(description = "文件存储路径")
    private String filePath;

    /** 文件大小（字节） */
    @Schema(description = "文件大小")
    private Long fileSize;

    /** 上传人名称（非数据库字段，冗余展示用） */
    @TableField(exist = false)
    @Schema(description = "上传人名称")
    private String uploadByName;
}
