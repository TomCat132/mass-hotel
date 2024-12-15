package cn.finetool.hotel.service;

import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RoomBookingService extends IService<RoomBooking> {

    /** =========== 查询订单（手机号/订单号） ========== */
    Response queryRoomBooking(Integer queryType, String queryValue);

    /** =========== 开始处理入住 ========== */
    Response startHandleCheckIn(Integer id);

    /** =========== 检查房间情况 ========== */
    Response checkRoomDateInfo(Integer id);

    Response receiveDeposit(String id);

    Response finishHandleCheckIn(Integer id, Integer type, String doorKey);

    Response bindingDoorKey(Integer id, String doorKey);

    Response unBindingDoorKey(Integer id);
}
