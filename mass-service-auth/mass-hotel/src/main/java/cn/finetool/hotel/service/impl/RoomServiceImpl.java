package cn.finetool.hotel.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.RoomInfoVo;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import cn.finetool.hotel.service.RoomService;
import cn.finetool.hotel.strategy.discountStrategy.RoomPricingContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static cn.finetool.common.util.Response.success;
import static cn.finetool.hotel.HotelApplication.ID_WORKER;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private RoomInfoMapper roomInfoMapper;
    @Resource
    private RoomPricingContext roomPricingContext;
    @Resource
    private AccountAPIService accountAPIService;
    @Resource
    private HotelMapper hotelMapper;


    @Override
    public Response addRoomInfo(RoomDto roomDto) {
        try {
            // TODO: 代优化 （简单添加数据）
            String userId = StpUtil.getLoginIdAsString();
            String merchantId = accountAPIService.findMerchantIdOfUserId(userId);
            Hotel hotel = hotelMapper.selectOne(new QueryWrapper<Hotel>()
                    .eq("merchant_id", merchantId));
            Room room = new Room();
            room.setRoomDesc(JsonUtil.toJsonString(roomDto.getRoomDesc()));
            room.setRoomName(roomDto.getRoomName());
            room.setRoomId(SysEnum.ROOM_PREFIX.code() + ID_WORKER.nextId());
            room.setRoomType(roomDto.getRoomType());
            room.setHotelId(roomDto.getHotelId());
            room.setBasicPrice(roomDto.getBasicPrice());
            room.setHotelId(hotel.getHotelId());
            this.save(room);
            return success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessRuntimeException(BusinessErrors.SYSTEM_ERROR, "添加失败");
        }

    }

    @Override
    public Response queryRoomInfo(String roomId) {
        // TODO: 代优化 （简单查询数据）
        // 需要字段 roomId,roomName,roomAvatarList,roomType,oldPrice,price,roomDesc
        RoomInfoVo roomInfo = roomMapper.queryRoomInfoByDate(roomId, TimeUtil.currentDate());
        return success(roomInfo);
    }

    @Override
    public List<Integer> queryResidualRoomInfo(String roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return roomInfoMapper.queryResidualRoomInfo(roomId,checkInDate,checkOutDate);
    }

    @Override
    public Response calculatePrice(RoomBookingDto roombookingDto) {
        return success(calculatePayAmount(roombookingDto));
    }

    private BigDecimal calculatePayAmount(RoomBookingDto roombookingDto) {
        // 计算天数
        LocalDate checkInDate = roombookingDto.getCheckInDate();
        LocalDate checkOutDate = roombookingDto.getCheckOutDate();
        int liveCount = (int) checkOutDate.toEpochDay() - (int) checkInDate.toEpochDay();
        BigDecimal price = roomInfoMapper.getRoomPrice(roombookingDto.getRoomId(),checkInDate,liveCount);
        //设置临时价格，进行各种优惠活动计算
        roombookingDto.setTempPrice(price);
        // TODO: 会员折扣，优惠券 等 规则计算
        price = roomPricingContext.calculatePrice(roombookingDto);
        return price;
    }


}
