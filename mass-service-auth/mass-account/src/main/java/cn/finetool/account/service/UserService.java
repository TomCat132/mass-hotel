package cn.finetool.account.service;

import cn.finetool.common.dto.PasswordDto;
import cn.finetool.common.po.User;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface UserService extends IService<User> {

    Response<User> register(User user);

    Response login(User user);

    Response<User> getUserInfo();

    Response logout();

    Response editAvatar(MultipartFile file);

    Response editPassword(PasswordDto passwordDto);

    void updateUserInfo(String userId, BigDecimal totalAmount);
}
