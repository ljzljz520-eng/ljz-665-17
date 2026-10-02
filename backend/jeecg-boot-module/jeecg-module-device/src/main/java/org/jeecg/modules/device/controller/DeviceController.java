package org.jeecg.modules.device.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @Description: 设备档案
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Tag(name = "设备档案")
@RestController
@RequestMapping("/device/device")
@Slf4j
public class DeviceController extends JeecgController<Device, IDeviceService> {

    @Autowired
    private IDeviceService deviceService;

    /**
     * 分页列表查询
     */
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Device device,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Device> queryWrapper = QueryGenerator.initQueryWrapper(device, req.getParameterMap());
        queryWrapper.orderByDesc("create_time");
        Page<Device> page = new Page<>(pageNo, pageSize);
        IPage<Device> pageList = deviceService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 新增设备
     */
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Device device) {
        deviceService.save(device);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑设备
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody Device device) {
        deviceService.updateById(device);
        return Result.OK("编辑成功！");
    }

    /**
     * 通过id删除设备（同事务级联删除附件，杜绝孤儿附件记录）
     */
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        deviceService.deleteDeviceCascade(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除设备（同事务级联删除附件）
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        deviceService.deleteDeviceBatchCascade(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询设备详情
     */
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        Device device = deviceService.getById(id);
        return Result.OK(device);
    }
}
