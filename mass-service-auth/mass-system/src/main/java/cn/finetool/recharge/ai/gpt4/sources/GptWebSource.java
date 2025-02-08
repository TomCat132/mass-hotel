package cn.finetool.recharge.ai.gpt4.sources;

import cn.finetool.common.util.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class GptWebSource {
    
    private static final String BASE_URL = "https://seeyon.chat/api";
    private static final String API_KEY = "YOUR_API_KEY"; // 请替换为实际的API密钥
    
//    /** =======                                     ======== **/
//    @PostMapping("/request-handle")
//    public Response requestHandle(){
//        
//    }
}
