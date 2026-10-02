package org.jeecg.modules.device.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.device.entity.DeviceAttachment;
import org.jeecg.modules.device.service.IDeviceAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description: 设备附件（设备照片 / 说明书 / 维保合同）
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "设备附件")
@RestController
@RequestMapping("/device/deviceAttachment")
public class DeviceAttachmentController {

    @Autowired
    private IDeviceAttachmentService deviceAttachmentService;

    /**
     * 附件列表
     */
    @Operation(summary = "设备附件-列表查询")
    @GetMapping(value = "/list")
    public Result<List<DeviceAttachment>> list(@RequestParam(name = "deviceId") String deviceId) {
        return Result.OK(deviceAttachmentService.listByDeviceId(deviceId));
    }

    /**
     * 上传附件（multipart/form-data）
     * 参数：file 文件、deviceId 设备ID、attachmentType photo/manual/contract/other
     */
    @AutoLog(value = "设备附件-上传")
    @Operation(summary = "设备附件-上传")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Result<DeviceAttachment> upload(@RequestPart("file") MultipartFile file,
                                           @RequestParam("deviceId") String deviceId,
                                           @RequestParam(value = "attachmentType", required = false) String attachmentType) throws Exception {
        DeviceAttachment attachment = deviceAttachmentService.upload(deviceId, attachmentType, file);
        return Result.OK("上传成功！", attachment);
    }

    /**
     * 删除附件（前端需二次确认后调用）
     */
    @AutoLog(value = "设备附件-删除")
    @Operation(summary = "设备附件-删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        deviceAttachmentService.deleteAttachment(id);
        return Result.OK("删除成功!");
    }
}
