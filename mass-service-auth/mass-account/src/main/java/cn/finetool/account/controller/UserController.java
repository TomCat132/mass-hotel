package cn.finetool.account.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.service.UserService;
import cn.finetool.common.dto.PasswordDto;
import cn.finetool.common.po.User;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
@Slf4j
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
    public Response editAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        return userService.editAvatar(file,request);
    }

    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PutMapping("/update-pwd")
    public Response editPassword(@RequestBody PasswordDto passwordDto){
        return userService.editPassword(passwordDto);
    }

    @ApiOperation(value = "管理员登录", notes = "管理员登录")
    @PostMapping("/admin/login")
    public Response adminLogin(@RequestBody User user){
        return userService.adminLogin(user);
    }

    @SaCheckRole(value = {"user"},mode = SaMode.OR)
    @ApiOperation(value = "用户领取优惠券", notes = "用户领取优惠券")
    @PostMapping("/getVoucher")
    public Response getVoucher(@RequestParam("voucherId") String voucherId){
        return userService.getVoucher(voucherId);
    }


    @GetMapping("/orderlist")
    @ApiOperation(value = "获取所有类型的订单列表", notes = "获取所有类型的订单列表")
    public Response getOrderList() {
        return userService.getOrderList();
    }

    @PutMapping("/deleteOrderById/{orderId}")
    @ApiOperation(value = "删除订单", notes = "删除订单")
    public Response deleteOrderById(@PathVariable("orderId") String orderId){
        return userService.deleteOrderById(orderId);
    }

    @PostMapping("/checkPwd")
    @ApiOperation(value = "校验密码", notes = "校验密码")
    public Response checkPwd(@RequestParam("oldPwd") String oldPwd){
        return userService.checkPwd(oldPwd);
    }


}
