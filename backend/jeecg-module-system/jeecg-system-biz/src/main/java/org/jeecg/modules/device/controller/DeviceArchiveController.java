package org.jeecg.modules.device.controller;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.device.entity.DeviceArchive;
import org.jeecg.modules.device.service.IDeviceArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Tag(name = "设备档案")
@RestController
@RequestMapping("/device/archive")
@Slf4j
public class DeviceArchiveController extends JeecgController<DeviceArchive, IDeviceArchiveService> {

    @Autowired
    private IDeviceArchiveService deviceArchiveService;

    /**
     * 分页列表查询
     */
    @GetMapping(value = "/list")
    public Result<IPage<DeviceArchive>> queryPageList(DeviceArchive deviceArchive,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<DeviceArchive> queryWrapper = QueryGenerator.initQueryWrapper(deviceArchive, req.getParameterMap());
        queryWrapper.orderByDesc("create_time");
        Page<DeviceArchive> page = new Page<>(pageNo, pageSize);
        IPage<DeviceArchive> pageList = deviceArchiveService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 查询设备详情（含附件列表）
     */
    @GetMapping(value = "/queryById")
    public Result<DeviceArchive> queryById(@RequestParam(name = "id") String id) {
        DeviceArchive device = deviceArchiveService.getDetailById(id);
        if (device == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(device);
    }

    /**
     * 添加设备档案
     */
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody DeviceArchive deviceArchive) {
        deviceArchiveService.saveDevice(deviceArchive);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑设备档案
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody DeviceArchive deviceArchive) {
        deviceArchiveService.updateDevice(deviceArchive);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除设备档案（级联删除附件，不保留孤儿记录）
     */
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        deviceArchiveService.deleteDeviceById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除设备档案（级联删除附件）
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        deviceArchiveService.deleteDeviceBatch(idList);
        return Result.OK("批量删除成功!");
    }
}
