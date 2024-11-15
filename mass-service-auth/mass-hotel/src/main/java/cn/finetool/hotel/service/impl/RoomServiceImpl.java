package cn.finetool.hotel.service.impl;


import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.Room;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.vo.RoomInfoVo;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import cn.finetool.hotel.service.RoomService;
import cn.finetool.hotel.strategy.discountStrategy.RoomPricingContext;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private static final ObjectMapper JSONUTIL = new ObjectMapper();

    private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(7, 0);


    @Resource
    private RedissonClient redissonClient;


    @Resource
    private RoomMapper roomMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;



    @Resource
    private RoomPricingContext roomPricingContext;


    @Override
    public Response addRoomInfo(RoomDto roomDto) {
        try {
            // TODO: 代优化 （简单添加数据）
            Room room = new Room();
            room.setRoomDesc(JSONUTIL.writeValueAsString(roomDto.getRoomDesc()));
            room.setRoomName(roomDto.getRoomName());
            room.setRoomId(CodeSign.RoomTypePrefix.getCode() +String.valueOf(idWorker.nextId()));
            room.setRoomType(roomDto.getRoomType());
            room.setHotelId(roomDto.getHotelId());
            room.setBasicPrice(roomDto.getBasicPrice());

            this.save(room);
            return Response.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessRuntimeException(BusinessErrors.SYSTEM_ERROR, "添加失败");
        }

    }

    @Override
    public Response queryRoomInfo(String roomId) {
        // TODO: 代优化 （简单查询数据）
        // 需要字段 roomId,roomName,roomAvatarList,roomType,oldPrice,price,roomDesc
        RoomInfoVo roomInfo = roomMapper.queryRoomInfoByDate(roomId, LocalDate.now());

        return Response.success(roomInfo);
    }

    @Override
    public List<Integer> queryResidualRoomInfo(String roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return roomInfoMapper.queryResidualRoomInfo(roomId,checkInDate,checkOutDate);
    }

    @Override
    public Response calculatePrice(RoomBookingDto roombookingDto) {

        // 计算天数
        LocalDate checkInDate = roombookingDto.getCheckInDate();
        LocalDate checkOutDate = roombookingDto.getCheckOutDate();
        int liveCount = (int) checkOutDate.toEpochDay() - (int) checkInDate.toEpochDay();
        BigDecimal price = roomInfoMapper.getRoomPrice(roombookingDto.getRoomId(),checkInDate,liveCount);
        //设置临时价格，进行各种优惠活动计算
        roombookingDto.setTempPrice(price);
        // TODO: 会员折扣，优惠券 等 规则计算
        price = roomPricingContext.calculatePrice(roombookingDto);
        return Response.success(price);

    }


}
