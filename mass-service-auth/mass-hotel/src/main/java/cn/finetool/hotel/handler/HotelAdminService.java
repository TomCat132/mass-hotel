package cn.finetool.hotel.handler;

import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.util.Response;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface HotelAdminService {
    
    /** ========= 获取酒店房间预订列表 ========= */
    Response getHotelReserveRoomBookingList(String merchantId);
    /** ========= 查询合作商户列表 ========= */
    Response queryCooperationMerchantList();
    /** ========= 更新商户状态 ========= */
    Response updateStatus(PlanDto planDto);
    /** ========= 查询商户详情页面 ========= */
    Response merchantInfo(String merchantId);
    /** ========= 退房办理订单信息列表 ========= */
    Response getWillFinishOrderList(String merchantId);
    /** ========= 开始办理入住 ========= */
    Response startFinishRoomOut(Integer id);
    /** ========= 根据房间日期ID获取房间价格 ========= */
    BigDecimal getRoomDatePriceById(Integer roomDateId);
    /** ========= 茶轩房间名称列表 ========= */
    Response getRoomNameList(String merchantId);
    /** ========= 根据房间ID获取房间信息VO ========= */
    Response getRoomInfoVO(String roomId);
    /** ========= 更新房间宣传图片 ========= */
    Response updateRoomImage(String id, List<MultipartFile> filelist, List<String> deleteIds);
    /** ========= 根据房间ID获取房间信息 ========= */
    Response findRoomInfoById(String id, String queryTime);
    /** ========= 根据订单ID更新房间预定信息状态 ========= */
    void updateRoomBookingStatus(String orderId, Integer status);
    /** ========= 根据房间日期ID更新房间价格 ========= */
    Response updateRoomDatePrice(Integer id, BigDecimal newPrice);
    /** ========= 根据房间ID获取房间图片列表 ========= */
    Response findRoomInfoAvatarList(String id);
    /** ========= 更新房间信息 ========= */
    Response updateRoom(RoomDto roomDto);
    /** ========= 删除房间类型相关的所有配置信息 ========= */
    Response deleteRoom(String roomId);
    /** ========= 根据日期获取可预订的房间列表 ========= */
    Response getCanReserveRoomByDate(String merchantId, String queryDate);
    /** ========= 根据日期获取已预订的房间列表 ========= */
    Response getReservedRoomByDate(String merchantId, String queryDate);
    /** ========= 根据日期获取正在使用中的房间列表 ========= */
    Response getUsingRoomByDate(String merchantId, String queryDate);
    /** ========= 根据日期获取空闲中的房间列表 ========= */
    Response getCleaningRoomByDate(String merchantId, String queryDate);
}
