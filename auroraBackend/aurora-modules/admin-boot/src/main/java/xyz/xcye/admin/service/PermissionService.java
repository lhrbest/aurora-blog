package xyz.xcye.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.xcye.admin.po.Permission;
import xyz.xcye.admin.pojo.PermissionPojo;
import xyz.xcye.core.enums.RegexEnum;
import xyz.xcye.core.enums.ResponseStatusCodeEnum;
import xyz.xcye.core.exception.permission.PermissionException;
import xyz.xcye.core.util.BeanUtils;
import xyz.xcye.core.util.lambda.AssertUtils;
import xyz.xcye.data.entity.Condition;
import xyz.xcye.data.entity.PageData;
import xyz.xcye.data.util.PageUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author qsyyke
 * @date Created in 2022/5/4 21:34
 */

@Service
public class PermissionService {

    @Autowired
    private AuroraPermissionService auroraPermissionService;

    public int physicalDeletePermission(long uid) {
        return auroraPermissionService.deleteById(uid);
    }

    public int batchPhysicalDeletePermission(PermissionPojo permission) {
        List<Long> uidList = permission.getPermissionList().stream().map(PermissionPojo::getUid).collect(Collectors.toList());
        return auroraPermissionService.batchDelete(uidList);
    }

    public void insertPermission(PermissionPojo permission) {
        Objects.requireNonNull(permission,"方法路径信息不能为null");
        // 判断path是否符合规范，必须是GET:Path这种形式 不支持中文路径
        AssertUtils.stateThrow(matchesResourcePath(permission.getPath()), () -> new PermissionException(ResponseStatusCodeEnum.PERMISSION_RESOURCE_NOT_RIGHT));
        if (judgeSimilarPermissionPath(permission.getPath())) {
            throw new PermissionException("此资源路径已经存在");
        }
        auroraPermissionService.insert(BeanUtils.copyProperties(permission, Permission.class));
    }

    public void batchInsertPermission(PermissionPojo permission) {
        Objects.requireNonNull(permission,"方法路径信息不能为null");
        Objects.requireNonNull(permission.getPermissionList(),"方法路径信息不能为null");
        List<PermissionPojo> permissionList = permission.getPermissionList();
        for (PermissionPojo pojo : permissionList) {
            if (!StringUtils.hasLength(pojo.getPath())) {
                continue;
            }
            if (!matchesResourcePath(pojo.getPath())) {
                continue;
            }
            if (judgeSimilarPermissionPath(pojo.getPath())) {
                continue;
            }
            if (!StringUtils.hasLength(pojo.getName())) {
                pojo.setName(pojo.getPath());
            }
            auroraPermissionService.insert(BeanUtils.copyProperties(pojo, Permission.class));
        }
        // 判断path是否符合规范，必须是GET:Path这种形式 不支持中文路径
    }

    public int updatePermission(PermissionPojo permission) {
        Objects.requireNonNull(permission, "资源路径权限信息不能为null");
        if (StringUtils.hasLength(permission.getPath())) {
            AssertUtils.stateThrow(matchesResourcePath(permission.getPath()),
                    () -> new PermissionException(ResponseStatusCodeEnum.PERMISSION_RESOURCE_NOT_RIGHT));
        }else {
            // 没有path
            permission.setPath(null);
        }
        return auroraPermissionService.updateById(BeanUtils.copyProperties(permission, Permission.class));
    }

    public PageData<Permission> queryListPermissionByCondition(Condition<Long> condition) {
        return auroraPermissionService.queryListByCondition(condition);
    }

    private boolean matchesResourcePath(String resourcePath) {
        return Pattern.matches(RegexEnum.REST_FUL_PATH.getRegex(),resourcePath);
    }

    public Permission queryPermissionByUid(long uid) {
        return auroraPermissionService.queryById(uid);
    }

    private boolean judgeSimilarPermissionPath(String permissionPath) {
        Permission permission = new Permission();
        permission.setPath(permissionPath);
        List<Permission> permissions = auroraPermissionService.queryListByWhere(permission);
        if (permissions != null) {
            return !permissions.isEmpty();
        }
        return true;
    }
}
