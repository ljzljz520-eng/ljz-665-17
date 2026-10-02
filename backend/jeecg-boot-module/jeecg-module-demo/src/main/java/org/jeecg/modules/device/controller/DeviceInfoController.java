package org.jeecg.modules.device.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.device.entity.DeviceInfo;
import org.jeecg.modules.device.service.IDeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "设备档案")
@RestController
@RequestMapping("/device/deviceInfo")
public class DeviceInfoController extends JeecgController<DeviceInfo, IDeviceInfoService> {

    @Autowired
    private IDeviceInfoService deviceInfoService;

    /**
     * 分页列表查询
     */
    @Operation(summary = "设备档案-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> list(DeviceInfo deviceInfo,
                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                          HttpServletRequest req) {
        QueryWrapper<DeviceInfo> queryWrapper = QueryGenerator.initQueryWrapper(deviceInfo, req.getParameterMap());
        queryWrapper.orderByDesc("create_time");
        Page<DeviceInfo> page = new Page<>(pageNo, pageSize);
        IPage<DeviceInfo> pageList = deviceInfoService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     */
    @AutoLog(value = "设备档案-添加")
    @Operation(summary = "设备档案-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody DeviceInfo deviceInfo) {
        if (deviceInfo.getDeviceCode() == null || deviceInfo.getDeviceCode().trim().isEmpty()) {
            deviceInfo.setDeviceCode("DEV" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date())
                    + (int) (Math.random() * 900 + 100));
        }
        deviceInfoService.save(deviceInfo);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     */
    @AutoLog(value = "设备档案-编辑", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "设备档案-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody DeviceInfo deviceInfo) {
        deviceInfoService.updateById(deviceInfo);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除（级联删除附件，不保留孤儿记录）
     */
    @AutoLog(value = "设备档案-删除")
    @Operation(summary = "设备档案-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        deviceInfoService.deleteDeviceCascade(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除（级联删除附件，不保留孤儿记录）
     */
    @AutoLog(value = "设备档案-批量删除")
    @Operation(summary = "设备档案-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        deviceInfoService.deleteDeviceBatchCascade(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询设备详情
     */
    @Operation(summary = "设备档案-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        DeviceInfo deviceInfo = deviceInfoService.getById(id);
        if (deviceInfo == null) {
            return Result.error("设备不存在");
        }
        return Result.OK(deviceInfo);
    }
}
