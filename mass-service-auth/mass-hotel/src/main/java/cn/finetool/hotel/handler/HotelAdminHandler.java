package cn.finetool.hotel.handler;

import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.util.Response;

public interface HotelAdminHandler {

    Response getHotelReserveRoomBookingList(Integer hotelId);

    Response queryCooperationMerchantList();

    Response updateStatus(PlanDto planDto);
}
