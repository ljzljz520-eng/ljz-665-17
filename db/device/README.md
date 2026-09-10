# 设备档案 - 附件功能 增量脚本

设备档案模块（含设备附件区）的数据库增量脚本。应用代码不依赖外键做级联，
但为防止直接操作数据库产生孤儿数据，附件表仍建立了 `ON DELETE CASCADE` 外键作为兜底。

## 脚本清单

| 文件 | 适用数据库 |
| --- | --- |
| `device_archive_mysql.sql` | MySQL 5.7 / 8.x |
| `device_archive_sqlserver.sql` | SQL Server 2019+（本仓库 docker-compose 默认） |

## Docker Compose（SQL Server）部署执行方式

`db-init` 容器只在首次初始化时转换并执行 `backend/db/jeecgboot-mysql-5.7.sql`，
**增量脚本需要手动执行一次**。数据库名与 compose 中保持一致（默认 `temp1218`）。

在宿主机执行（容器名为 `jeecg-sqlserver`，sa 密码见 compose 环境变量）：

```bash
docker cp db/device/device_archive_sqlserver.sql jeecg-sqlserver:/tmp/device.sql
docker exec -it jeecg-sqlserver \
  /opt/mssql-tools18/bin/sqlcmd -S localhost,1433 -U sa -P 'Supcon1304' -C \
  -d temp1218 -f 65001 -i /tmp/device.sql
```

执行内容：

1. 建表 `device_archive`（设备档案）、`device_attachment`（设备附件，含外键 + 级联删除）；
2. 新增菜单：设备管理 / 设备档案，以及查询、详情、新增、编辑、删除、批量删除、上传附件、删除附件按钮权限；
3. 将以上权限授权给内置「管理员(admin)」角色。其他角色请到【系统管理 - 角色管理】中授权。

执行后**重新登录**（或刷新菜单缓存）即可看到“设备管理”菜单。

## 本地开发（MySQL）

```bash
mysql -u root -p jeecg-boot < db/device/device_archive_mysql.sql
```

## 级联删除说明（孤儿记录防护）

双重保障：

1. **应用层**：`DeviceArchiveServiceImpl#deleteDeviceBatch` 在同一事务
   （`@Transactional(rollbackFor = Exception.class)`）内先调用
   `DeviceAttachmentService#deleteByDeviceIds` 删除附件关系记录并尽力清理物理文件，
   再删除设备档案。任一步失败整体回滚；
2. **数据库层**：`fk_device_attachment_device` 外键 `ON DELETE CASCADE`，
   即使绕过应用直接删除设备，附件关系记录也会被数据库自动清理。
