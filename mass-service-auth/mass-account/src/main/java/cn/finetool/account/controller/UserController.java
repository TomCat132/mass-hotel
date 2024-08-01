package cn.finetool.account.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.service.UserService;
import cn.finetool.common.dto.PasswordDto;
import cn.finetool.common.po.User;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "注册用户", notes = "注册用户")
    @PostMapping("/register")
    public Response<User> register(@RequestBody User user){
        return userService.register(user);
    }

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("/login")
    public Response login(@RequestBody User user){
        return userService.login(user);
    }

    @ApiOperation(value = "用户获取个人信息", notes = "用户获取个人信息")
    @GetMapping
    public Response<User> getUserInfo(){
        return userService.getUserInfo();
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录")
    @PostMapping("/logout")
    public Response logout(){
        return userService.logout();
    }

    @ApiOperation(value = "修改头像", notes = "修改头像")
    @PostMapping("/editavatar")
    public Response editAvatar(@RequestParam("file") MultipartFile file){
        return userService.editAvatar(file);
    }

    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PutMapping("/updatepwd")
    public Response editPassword(@RequestBody PasswordDto passwordDto){
        return userService.editPassword(passwordDto);
    }



}
