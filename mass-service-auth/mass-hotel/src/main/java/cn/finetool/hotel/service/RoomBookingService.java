package cn.finetool.hotel.service;

import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface RoomBookingService extends IService<RoomBooking> {

    /** =========== 查询订单（手机号/订单号） ========== */
    Response queryRoomBooking(Integer queryType, String queryValue);
    /** =========== 开始处理入住 ========== */
    Response startHandleCheckIn(Integer id);
    /** =========== 检查房间情况 ========== */
    Response checkRoomDateInfo(Integer id);
    /** =========== 确认缴纳押金 ========== */
    Response receiveDeposit(String id);
    /** =========== 完成处理入住 ========== */
    Response finishHandleCheckIn(Integer id, Integer type, String doorKey) throws JsonProcessingException;
    /** =========== 绑定门禁卡 ========== */
    Response bindingDoorKey(Integer id, String doorKey);
    /** =========== 解绑门禁卡 ========== */
    Response unBindingDoorKey(Integer id);
    /** =========== 结束处理入住 ========== */
    Response endCheckInRoomOrder(Integer id);
    /** =========== 取消房间预定 ========== */
    Response cancelRoomBooking(Integer id);
    /** =========== C端:开始办理入住（线上） ========== */
    Response startHandleCheckInOnline(Integer id);
}
