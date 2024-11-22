package cn.finetool.account.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.finetool.account.service.RoleService;
import cn.finetool.common.po.Role;

import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
@Api(tags = "角色管理")
public class RoleController {

    @Resource
    private RoleService roleService;

    @ApiOperation(value = "新增角色", notes = "新增角色")
//    @SaCheckRole("super_admin")
    @PostMapping("/add")
    public Response addRole(@RequestBody Role role){
        return roleService.addRole(role);
    }


}
