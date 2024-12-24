package cn.finetool.account.sources;

import cn.finetool.account.service.AccountService;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@Api(tags = "账户Web资源接口")
public class AccountWebSource {

    @Resource
    private AccountService accountService;
    
    @GetMapping("/message-box-list")
    @ApiOperation(value = "获取用户消息列表", notes = "获取用户消息列表")
    public Response getUserMessageBoxList(){
        return accountService.getUserMessageBoxList();
    }
    
    
    @GetMapping("/unread-message-count")
    @ApiOperation(value = "获取未读消息数量", notes = "获取未读消息数量")
    public Response getUnreadMessageCount(){
        return accountService.getUnreadMessageCount();
    }
    
    @PutMapping("/tag-all-message-read")
    @ApiOperation(value = "标记所有消息为已读", notes = "标记所有消息为已读")
    public Response tagAllMessageRead(){
        return accountService.tagAllMessageRead();
    }
}
