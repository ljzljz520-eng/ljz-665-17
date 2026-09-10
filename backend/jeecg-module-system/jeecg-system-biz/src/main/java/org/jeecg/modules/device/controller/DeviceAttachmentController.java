package org.jeecg.modules.device.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.device.entity.DeviceArchive;
import org.jeecg.modules.device.entity.DeviceAttachment;
import org.jeecg.modules.device.service.IDeviceArchiveService;
import org.jeecg.modules.device.service.IDeviceAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 设备附件
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Tag(name = "设备附件")
@RestController
@RequestMapping("/device/attachment")
@Slf4j
public class DeviceAttachmentController {

    @Autowired
    private IDeviceAttachmentService deviceAttachmentService;
    @Autowired
    private IDeviceArchiveService deviceArchiveService;

    /**
     * 查询指定设备的附件列表（返回文件名、上传人、上传时间等信息）
     */
    @GetMapping(value = "/listByDevice")
    public Result<List<DeviceAttachment>> listByDevice(@RequestParam(name = "deviceId") String deviceId) {
        if (oConvertUtils.isEmpty(deviceId)) {
            return Result.error("设备ID不能为空");
        }
        return Result.OK(deviceAttachmentService.listByDeviceId(deviceId));
    }

    /**
     * 上传附件（文件本身先走 /sys/common/upload，此接口仅保存附件关系记录）
     * 请求体：deviceId、attachmentType、fileName、filePath、fileSize
     */
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody DeviceAttachment attachment) {
        if (oConvertUtils.isEmpty(attachment.getDeviceId()) || oConvertUtils.isEmpty(attachment.getFilePath())) {
            return Result.error("设备ID或文件路径不能为空");
        }
        DeviceArchive device = deviceArchiveService.getById(attachment.getDeviceId());
        if (device == null) {
            return Result.error("关联的设备档案不存在，无法添加附件");
        }
        deviceAttachmentService.saveAttachment(attachment);
        return Result.OK("附件上传成功！");
    }

    /**
     * 删除附件
     */
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        deviceAttachmentService.deleteAttachment(id);
        return Result.OK("附件删除成功！");
    }
}
