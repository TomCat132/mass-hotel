package cn.finetool.hotel.handler;

import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.util.Response;

public interface HotelAdminService {

    /**
     * Web后台：用户端
     *
     * @param hotelId
     * @return
     */
    Response getHotelReserveRoomBookingList(Integer hotelId);

    /**
     * Web后台：sys端
     *
     * @return
     */
    Response queryCooperationMerchantList();

    Response updateStatus(PlanDto planDto);

    Response merchantInfo(String merchantId);
}
