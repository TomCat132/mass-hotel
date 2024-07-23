package cn.finetool.account.controller;

import cn.finetool.account.service.UserService;
import cn.finetool.common.po.User;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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




    @PutMapping("/payorder/{orderId}")
    public Response payOrder(@PathVariable("orderId") String orderId){
        return userService.payOrder(orderId);
    }



}
