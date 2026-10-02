package org.jeecg.modules.device.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.entity.DeviceFile;
import org.jeecg.modules.device.service.IDeviceFileService;
import org.jeecg.modules.device.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Date;
import java.util.List;

/**
 * @Description: 设备附件（设备照片 / 说明书 / 维保合同）
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Tag(name = "设备附件")
@RestController
@RequestMapping("/device/deviceFile")
@Slf4j
public class DeviceFileController extends JeecgController<DeviceFile, IDeviceFileService> {

    @Autowired
    private IDeviceFileService deviceFileService;
    @Autowired
    private IDeviceService deviceService;

    @Value("${jeecg.path.upload:}")
    private String uploadPath;
    @Value("${jeecg.uploadType:local}")
    private String uploadType;

    /**
     * 查询某台设备的附件列表（展示文件名、上传人、上传时间）
     */
    @GetMapping(value = "/listByDevice")
    public Result<List<DeviceFile>> listByDevice(@RequestParam(name = "deviceId") String deviceId) {
        return Result.OK(deviceFileService.listByDeviceId(deviceId));
    }

    /**
     * 上传附件并绑定到指定设备
     *
     * @param deviceId 设备ID
     * @param category 附件分类：photo/manual/contract/other
     */
    @PostMapping(value = "/upload")
    public Result<DeviceFile> upload(HttpServletRequest request,
                                     @RequestParam(name = "deviceId") String deviceId,
                                     @RequestParam(name = "category", required = false, defaultValue = "other") String category) {
        Device device = deviceService.getById(deviceId);
        if (device == null) {
            return Result.error("设备不存在，无法上传附件");
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        // 复用平台统一上传能力（本地 / OSS / MinIO），并经过平台的文件安全校验
        String bizPath = "device";
        String savePath;
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
            savePath = CommonUtils.uploadLocal(file, bizPath, uploadPath);
        } else {
            savePath = CommonUtils.upload(file, bizPath, uploadType);
        }
        if (oConvertUtils.isEmpty(savePath)) {
            return Result.error("文件上传失败");
        }

        String orgName = CommonUtils.getFileName(file.getOriginalFilename());
        DeviceFile deviceFile = new DeviceFile()
                .setDeviceId(deviceId)
                .setFileName(orgName)
                .setFilePath(savePath)
                .setFileSize(file.getSize())
                .setCategory(category);
        // 上传人、上传时间（MybatisInterceptor 也会按字段名兜底填充）
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (loginUser != null) {
            deviceFile.setCreateBy(loginUser.getUsername());
        }
        deviceFile.setCreateTime(new Date());
        String fileType = "";
        if (orgName != null && orgName.contains(SymbolConstant.SPOT)) {
            fileType = orgName.substring(orgName.lastIndexOf(SymbolConstant.SPOT) + 1);
        }
        deviceFile.setFileType(fileType);
        deviceFileService.save(deviceFile);
        // 回填含上传人姓名的完整信息返回前端
        DeviceFile saved = deviceFileService.listByDeviceId(deviceId).stream()
                .filter(f -> f.getId().equals(deviceFile.getId()))
                .findFirst().orElse(deviceFile);
        return Result.OK("上传成功", saved);
    }

    /**
     * 删除附件（前端需二次确认后调用）
     *
     * @param id       附件ID
     * @param deviceId 设备ID，用于归属校验
     */
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id,
                            @RequestParam(name = "deviceId", required = false) String deviceId) {
        boolean ok = deviceFileService.deleteFile(id, deviceId);
        return ok ? Result.OK("删除成功！") : Result.error("附件不存在或不属于该设备");
    }
}
