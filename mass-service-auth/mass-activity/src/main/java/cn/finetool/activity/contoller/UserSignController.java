package cn.finetool.activity.contoller;

import cn.finetool.activity.service.UserSignService;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usersign")
@Api(tags = "签到活动管理")
public class UserSignController {

    @Resource
    UserSignService userSignService;

    @ApiOperation(value = "用户签到", notes = "用户签到")
    @PostMapping
    public Response userSign(){
        return userSignService.userSign();
    }
}
