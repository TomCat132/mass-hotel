package cn.finetool.hotel.handler;

import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.util.Response;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface HotelAdminService {

    /**
     * Web后台：用户端
     *
     * @param merchantId
     * @return
     */
    Response getHotelReserveRoomBookingList(String merchantId);

    /**
     * Web后台：sys端
     *
     * @return
     */
    Response queryCooperationMerchantList();

    Response updateStatus(PlanDto planDto);

    Response merchantInfo(String merchantId);

    Response getWillFinishOrderList(String merchantId);

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
}
