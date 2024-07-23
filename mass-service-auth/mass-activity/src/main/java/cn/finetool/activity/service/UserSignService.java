package cn.finetool.activity.service;

import cn.finetool.common.po.UserSign;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserSignService extends IService<UserSign> {
    Response userSign();

    boolean isUserSign();
}
