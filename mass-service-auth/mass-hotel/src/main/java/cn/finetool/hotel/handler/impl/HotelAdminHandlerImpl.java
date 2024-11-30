package cn.finetool.hotel.handler.impl;

import cn.finetool.api.service.RechargePlanAPIService;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.CheckRoomInfoVO;
import cn.finetool.common.vo.CpMerchantVO;
import cn.finetool.hotel.handler.HotelAdminHandler;
import cn.finetool.hotel.mapper.HotelMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelAdminHandlerImpl implements HotelAdminHandler {

    @Resource
    private HotelMapper hotelMapper;

    @Resource
    private RechargePlanAPIService rechargePlanAPIService;

    @Override
    public Response getHotelReserveRoomBookingList(Integer hotelId) {
        // 根据 HotelId 查询 所有预定房间信息
        List<CheckRoomInfoVO> roombookingList= hotelMapper.queryHotelReserveRoomBookingList(hotelId);
        roombookingList.stream()
                .map( roombookingVO -> {
                     // 找到 phone
                    Integer roomDateId = roombookingVO.getRoomBooking().getRoomDateId();
                    String phone =  hotelMapper.queryUserColumn(roomDateId);
                    roombookingVO.setPhone(phone);
                     return roombookingVO;
                }).toList();
        return Response.success(roombookingList);
    }

    @Override
    public Response queryCooperationMerchantList() {
        List<CpMerchantVO> merchantList = new ArrayList<>();
        //暂时只有酒店
        List<Hotel> hotelList = hotelMapper.selectList(null);
        merchantList = hotelList.stream()
                .map(hotel -> {
                    CpMerchantVO merchantVO = new CpMerchantVO();
                    merchantVO.setMerchantId(hotel.getMerchantId());
                    merchantVO.setMerchantName(hotel.getHotelName());
                    merchantVO.setMerchantType(CodeSign.MERCHANT_HotelPrefix.getCode());
                    merchantVO.setCity(hotel.getCity());
                    merchantVO.setAddress(hotel.getAddress());
                    merchantVO.setPhoneNumber(hotel.getPhoneNumber());
                    merchantVO.setMerchantStatus(hotel.getStatus());
                    return merchantVO;
                }).toList();
        return Response.success(merchantList);
    }

    @Override
    public Response updateStatus(PlanDto planDto) {
        rechargePlanAPIService.updateRechargePlanStatus(planDto.getPlanId(), planDto.getStatus());
        return Response.success("更新成功");
    }
}
