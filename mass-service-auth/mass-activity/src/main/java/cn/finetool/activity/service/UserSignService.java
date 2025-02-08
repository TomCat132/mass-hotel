package cn.finetool.activity.service;

import cn.finetool.common.po.UserSign;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserSignService extends IService<UserSign> {
    /** ========= 用户签到 ========= */
    Response userSign() throws JsonProcessingException;
    /** ========= 判断用户是否签到 ========= */
    boolean isUserSign();
    /** ========= 查询用户签到记录 ========= */
    Response userSignList(String userId);
}
