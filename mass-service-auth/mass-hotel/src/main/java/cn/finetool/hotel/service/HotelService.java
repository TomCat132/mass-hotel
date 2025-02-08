package cn.finetool.hotel.service;

import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.dto.QueryRoomTypeDto;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

public interface HotelService extends IService<Hotel> {

    Response addHotelInfo(Hotel hotel);

    Response getNearByHotelList(Double userLng, Double userLat, Double queryRange);

    Response getHotelRoomTypeList(QueryRoomTypeDto queryRoomTypeDto);

    RoomOrderBaseInfo getBookedRoomBaseInfo(String orderId, Integer roomId);

    /**
     * 校验计算订单金额
     * @param orderPayDto
     * @return
     */
    BigDecimal caculatePayAmount(OrderPayDto orderPayDto);

    /** ========= 根据关键词搜索相关酒店信息 ========== */
    Response getHotelRoomList(String keyword);
}
