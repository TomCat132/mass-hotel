package cn.finetool.activity.contoller;

import cn.finetool.activity.service.UserSignService;
import cn.finetool.common.util.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usersign")
@Api(tags = "签到活动管理")
public class UserSignController {

    @Resource
    UserSignService userSignService;

    @ApiOperation(value = "用户签到", notes = "用户签到")
    @PostMapping
    public Response userSign() throws JsonProcessingException {
        return userSignService.userSign();
    }

    @ApiOperation(value = "查询用户是否签到", notes = "查询用户是否签到")
    @GetMapping("/signed")
    public boolean isUserSign() {
        return userSignService.isUserSign();
    }
    
    @GetMapping("/list")
    @ApiOperation(value = "查询用户签到记录", notes = "查询用户签到记录")
    public Response userSignList(@RequestParam("userId") String userId){
        return userSignService.userSignList(userId);
    }
}
