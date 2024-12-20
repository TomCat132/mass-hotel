package cn.finetool.hotel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.QueryRoomTypeDto;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.po.RoomDate;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.HotelVo;
import cn.finetool.common.vo.RoomInfoVo;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.mapper.RoomBookingMapper;
import cn.finetool.hotel.mapper.RoomDateMapper;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import cn.finetool.hotel.service.HotelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private HotelMapper hotelMapper;
    @Resource
    private RoomDateMapper roomDateMapper;
    @Resource
    private RoomInfoMapper roomInfoMapper;
    @Resource
    private RoomBookingMapper roomBookingMapper;
    @Resource
    private RoomMapper roomMapper;

    @Override
    public Response addHotelInfo(Hotel hotel) {
        // TODO: 代优化 （简单添加数据);
        save(hotel);
        return Response.success("添加成功");
    }

    @Override
    public Response getNearByHotelList(Double userLng, Double userLat, Double queryRange) {
        // 1. 用户当前位置经纬度
        Point userPoint = new Point(userLng, userLat);

         queryRange = queryRange * 1000; // 转换为米

        // 2. 存储用户位置到 Redis
        // 临时存储用户位置，用于计算距离
        String userKey = StpUtil.getLoginIdAsString();

        redisTemplate.opsForGeo().add(RedisCache.HOTEL_LOCATION_LIST, new RedisGeoCommands.GeoLocation<>(userKey, userPoint));

        // 2. 查询半径为 queryRange 的圆形范围
        Circle circle = new Circle(userPoint, queryRange);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> nearbyHotels = redisTemplate.opsForGeo().radius(RedisCache.HOTEL_LOCATION_LIST, circle);
        if (nearbyHotels == null || nearbyHotels.getContent().isEmpty()) {
            return Response.error("附近没有入驻的酒店哦~");
        }

        // 3. 计算用户到每个酒店的距离
        List<HotelVo> hotelVoList = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> result : nearbyHotels) {

            String hotelIdStr = (String) result.getContent().getName();

            // 查询酒店的经纬度
            List<Point> hotelPoints = redisTemplate.opsForGeo().position(RedisCache.HOTEL_LOCATION_LIST, hotelIdStr);
            Point hotelPoint = Objects.requireNonNull(hotelPoints).getFirst();
            hotelPoint = new Point(hotelPoint.getX(), hotelPoint.getY());

            // 使用 GEODIST 命令计算用户和酒店之间的距离，单位为千米
            Distance distance = redisTemplate.opsForGeo().distance(RedisCache.HOTEL_LOCATION_LIST, userKey, hotelIdStr, RedisGeoCommands.DistanceUnit.KILOMETERS);
            if (distance == null) {
                continue; // 忽略无法计算距离的酒店
            }

            // 根据id 获取酒店详细地址 、 最低价格 、 距离
            // 判断是否是临时用户位置 酒店id：自增主键 用户ID: 1010
            if (hotelIdStr.startsWith("1010")){
                continue;
            }
            HotelVo hotelVo = hotelMapper.queryHotelInfo(Integer.parseInt(hotelIdStr));
            hotelVo.setHotelLng(hotelPoint.getX());
            hotelVo.setHotelLat(hotelPoint.getY());
            hotelVo.setDistance(distance.getValue());
            // 计算最低价格
            Integer hotelId = hotelVo.getHotelId();
            // 根据Id获取 roomIdList，遍历查询 roomInfo 的主键List,根据主键 List去查询 roomDate 当日最低价格
            BigDecimal minPrice = roomDateMapper.queryHotelMinPrice(hotelId, LocalDate.now());
            if (minPrice != null){
                hotelVo.setMinPrice(minPrice);
                hotelVoList.add(hotelVo);
            }


        }

        // 删除临时存储的用户位置
        redisTemplate.opsForGeo().remove(RedisCache.HOTEL_LOCATION_LIST, userKey);

        return Response.success(hotelVoList);
    }

    @Override
    public Response getHotelRoomTypeList(QueryRoomTypeDto queryRoomTypeDto) {
        // TODO: 代优化 （简单查询数据）
        // 根据hotelId 查询 room表（room_name,room_desc,room_type,room_id）
        // room_id -> room_info表 (id,status = 1)
        // id -> room_date表 (count(status = 0), price(min))统计今日房间类型 剩余可用的 具体房间数量
        // 显示字段: room_name,room_type,room_desc,today_Price(最低价格)

        // 1. 将 checkOutDate 减一天，得到实际的入住日期
        LocalDate checkOutDate = queryRoomTypeDto.getCheckOutDate().minusDays(1);
        List<RoomInfoVo> roomInfoVoList = hotelMapper.queryHotelRoomTypeList(queryRoomTypeDto.getHotelId(),
                queryRoomTypeDto.getCheckInDate(),
                checkOutDate);


        return Response.success(roomInfoVoList);
    }

    @Override
    public RoomOrderBaseInfo getBookedRoomBaseInfo(String orderId, Integer roomDateId) {
        RoomOrderBaseInfo roomOrderBaseInfo = new RoomOrderBaseInfo();
        // 具体房间信息
        RoomDate roomDate = roomDateMapper.selectOne(new QueryWrapper<RoomDate>()
                .eq("id", roomDateId));
        RoomInfo roomInfo = roomInfoMapper.selectOne(new QueryWrapper<RoomInfo>()
                .eq("id", roomDate.getRiId()));
        // 房间类型信息
        Room room = roomMapper.selectOne(new QueryWrapper<Room>()
                .eq("room_id", roomInfo.getRoomId()));
        // 房间预定信息
        RoomBooking roomBooking = roomBookingMapper.selectOne(new QueryWrapper<RoomBooking>()
                .eq("order_id", orderId));
        // 房间所属酒店信息
        Hotel hotel = hotelMapper.selectOne(new QueryWrapper<Hotel>()
                .eq("hotel_id", room.getHotelId()));
        roomOrderBaseInfo.setRoomInfo(roomInfo);
        roomOrderBaseInfo.setRoom(room);
        roomOrderBaseInfo.setRoomBooking(roomBooking);
        roomOrderBaseInfo.setHotel(hotel);
        return roomOrderBaseInfo;
    }
}
