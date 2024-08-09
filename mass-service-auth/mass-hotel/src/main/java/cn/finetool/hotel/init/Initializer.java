package cn.finetool.hotel.init;

import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomDate;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.vo.HotelVo;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.mapper.RoomDateMapper;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import cn.finetool.hotel.service.HotelService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class Initializer {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private HotelMapper hotelMapper;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private RoomDateMapper roomDateMapper;

    /** ============= 初始化缓存酒店的经纬度信息 =========== */
    @PostConstruct
    public void InitHotelGeo(){
       List<HotelVo> hotelVoList = hotelMapper.getHotelGeoList();

       hotelVoList.forEach(hotelVo -> {
          redisTemplate.opsForGeo().add(RedisCache.HOTEL_LOCATION_LIST,
                  new Point(hotelVo.getHotelLng(),hotelVo.getHotelLat()), hotelVo.getHotelId().toString());
       });
    }


    /** ============= 初始化缓存酒店的每日房间信息 =========== */
    @Scheduled(cron = "1 0 0 * * ?")
    public void InitRoomDateInfo(){
        //1. 获取所有酒店每日的具体房间信息 roomId,roomInfoId,id
        List<RoomInfo> roomInfoList =roomInfoMapper.getCanUseRoomInfoList();

        //2. 添加今日的具体房间信息到信息表中 id,roomId,roomInfoId
        List<RoomDate> roomDateList = roomInfoList.stream().map(roomInfo -> {
            RoomDate roomDate = new RoomDate();
            roomDate.setRiId(roomInfo.getId());
            roomDate.setDate(LocalDate.now());
            //设置临时id,后续清除
            roomDate.setTempRoomId(roomInfo.getRoomId());
            return roomDate;
        }).toList();

        //2.1 收集房间id
        List<String> roomIdList = roomInfoList.parallelStream()
                .map(RoomInfo::getRoomId)
                .toList();

        //2.2 获取对应具体房间的基础价格  roomId,basic_price
        List<Room> roomPriceList = roomMapper.getRoomPriceList(roomIdList);

        //2.3 批量插入具体房间当日信息数据 添加date
        roomDateList.forEach(roomDate -> {
            roomPriceList.stream().filter(room -> room.getRoomId().equals(roomDate.getTempRoomId()))
                    .findFirst()
                    .ifPresent(room -> roomDate.setPrice(room.getBasicPrice()));
        });
        roomDateMapper.insert(roomDateList);
        log.info("初始化缓存酒店的每日房间信息完成");
    }
}
