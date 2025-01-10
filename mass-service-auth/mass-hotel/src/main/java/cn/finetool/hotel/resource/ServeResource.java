package cn.finetool.hotel.resource;

import cn.finetool.common.configuration.AppContext;
import cn.finetool.common.po.UserRequest;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.handler.ServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/serve")
@Api(tags = "服务资源接口")
public class ServeResource {
    
    private final ServeService serveService = AppContext.getBean(ServeService.class);
    
    @PostMapping("/create-request")
    @ApiOperation(value = "用户请求服务", notes = "用户请求服务")
    public Response userCreateRequest(@RequestBody UserRequest userRequest){
       return serveService.createUserRequest(userRequest);
    }
    
    @GetMapping("/get-request-list")
    @ApiOperation(value = "获取未处理的用户请求列表", notes = "PMS：首页获取未处理的用户请求列表")
    public Response getNotHandleRequestList(@RequestParam("merchantId") String merchantId){
        return serveService.getNotHandleRequestList(merchantId);
    }
    
    @PostMapping("/handle-request")
    @ApiOperation(value = "开始处理用户请求", notes = "处理用户请求")
    public Response startHandleRequest(@RequestParam("requestId") String requestId){
        return serveService.startHandleRequest(requestId);
    }
    

}
