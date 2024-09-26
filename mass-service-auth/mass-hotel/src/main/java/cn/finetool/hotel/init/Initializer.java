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
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Scheduled(cron = "0 0 1 * * ?")
    public void InitRoomDateInfo(){
        //获取所有酒店每日的具体房间信息 roomId,roomInfoId,id
        List<RoomInfo> roomInfoList =roomInfoMapper.getCanUseRoomInfoList();

        //添加今日的具体房间信息到信息表中 id,roomId,date
        List<RoomDate> roomDateList = roomInfoList.stream().map(roomInfo -> {
            RoomDate roomDate = new RoomDate();
            roomDate.setRiId(roomInfo.getId());
            roomDate.setDate(LocalDate.now());
            //设置临时id,后续清除
            roomDate.setTempRoomId(roomInfo.getRoomId());
            return roomDate;
        }).toList();

        //收集房间id
        List<String> roomIdList = roomDateList.parallelStream()
                .map(RoomDate::getTempRoomId)
                .toList();

        //获取对应具体房间的基础价格  roomId,basic_price
        Map<String, BigDecimal> roomPriceMap = roomMapper.getRoomPriceList(roomIdList).stream().collect(Collectors.toMap(Room::getRoomId, Room::getBasicPrice));

        //设置 price
        roomDateList.forEach(roomDate -> {
            roomDate.setPrice(roomPriceMap.get(roomDate.getTempRoomId()));
        });
        //批量插入
        roomDateMapper.insert(roomDateList);
        log.info("初始化缓存酒店的每日房间信息完成");
    }
}
