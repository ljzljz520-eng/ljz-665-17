package org.jeecg.modules.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.base.entity.JeecgEntity;

import java.io.Serializable;

/**
 * @Description: 设备附件（设备照片 / 说明书 / 维保合同）
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("device_attachment")
@Schema(description = "设备附件")
public class DeviceAttachment extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 设备档案ID */
    @Schema(description = "设备档案ID")
    private String deviceId;

    /** 附件类型：photo-设备照片 manual-说明书 contract-维保合同 other-其他 */
    @Schema(description = "附件类型")
    private String attachmentType;

    /** 文件名称（上传时的原始文件名） */
    @Schema(description = "文件名称")
    private String fileName;

    /** 文件存储相对路径（/sys/common/static 可访问） */
    @Schema(description = "文件路径")
    private String filePath;

    /** 文件大小（字节） */
    @Schema(description = "文件大小")
    private Long fileSize;
}
