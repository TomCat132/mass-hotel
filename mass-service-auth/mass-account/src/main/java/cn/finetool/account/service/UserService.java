package cn.finetool.account.service;

import cn.finetool.common.po.User;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {

    Response<User> register(User user);

    Response login(User user);

    Response payOrder(String orderId);

    Response<User> getUserInfo();


}
