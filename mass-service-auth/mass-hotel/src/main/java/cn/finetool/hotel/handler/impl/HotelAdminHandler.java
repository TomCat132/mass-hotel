package cn.finetool.hotel.handler.impl;

import cn.finetool.api.service.RechargePlanAPIService;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.CheckRoomInfoVO;
import cn.finetool.common.vo.CpMerchantVO;
import cn.finetool.hotel.handler.HotelAdminService;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelAdminHandler implements HotelAdminService {

    @Resource
    private HotelMapper hotelMapper;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private RechargePlanAPIService rechargePlanAPIService;

    @Override
    public Response getHotelReserveRoomBookingList(Integer hotelId) {
        // 根据 HotelId 查询 所有预定房间信息
        List<CheckRoomInfoVO> roombookingList = hotelMapper.queryHotelReserveRoomBookingList(hotelId);
        roombookingList.stream()
                .map(roombookingVO -> {
                    // 找到 phone
                    Integer roomDateId = roombookingVO.getRoomBooking().getRoomDateId();
                    String phone = hotelMapper.queryUserColumn(roomDateId);
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

    @Override
    public Response merchantInfo(String merchantId) {
        //目前只从 tb_hotel 表中查询数据 merchantId 前缀:1001
        //截取 merchantId 前缀
        Long prefix = Long.valueOf(merchantId.substring(0, 4));
        Object baseInfo = null;
        if (prefix == CodeSign.MERCHANT_HotelPrefix.getCode()) {
            //根据 merchantId 查询 hotel 信息、room 信息
            Hotel hotel = hotelMapper.selectOne(new QueryWrapper<Hotel>().eq("merchant_id", merchantId));
            List<Room> roomList = new ArrayList<>();
            List<Room> roomBaseInfoList = roomMapper.selectList(new QueryWrapper<Room>()
                    .eq("hotel_id", hotel.getHotelId()));
            roomBaseInfoList.forEach(room -> {
                List<RoomInfo> roomInfoList = roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                        .eq("room_id", room.getRoomId()));
                room.addAll(roomInfoList);
                roomList.add(room);
            });
            hotel.setRoomList(roomList);

            Map<String, Object> result = new HashMap<>();
            result.put("hotel", hotel);
            result.put("merchantType", CodeSign.MERCHANT_HotelPrefix.getCode());

            baseInfo = result;
        }

        return Response.success(baseInfo);
    }
}
