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
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Initializer {

    private static final Logger LOGGER = Logger.getLogger(Initializer.class.getName());

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

        LOGGER.info("初始化缓存酒店的经纬度信息");
        // 清空缓存

        redisTemplate.delete(RedisCache.HOTEL_LOCATION_LIST);

       List<HotelVo> hotelVoList = hotelMapper.getHotelGeoList();

       hotelVoList.forEach(hotelVo -> {
          redisTemplate.opsForGeo().add(RedisCache.HOTEL_LOCATION_LIST,
                  new Point(hotelVo.getHotelLng(),hotelVo.getHotelLat()), hotelVo.getHotelId().toString());
       });
    }


    /** ============= 初始化缓存酒店的每日房间信息 =========== */
    /**
     * 生成当月的每日房间信息，只执行一次
     */
    @Scheduled(cron = "0 0 2 1 * ?")
//    @PostConstruct
    public void InitRoomDateInfo() {
        //获取月份 临时脚本
//        LocalDate now = LocalDate.now();
//        int month = now.getMonthValue();
//        Boolean b = redisTemplate.hasKey(String.valueOf(month));
//        if (b){
//            return;
//        }
        // 获取所有酒店每日的具体房间信息 roomId,roomInfoId,id
        List<RoomInfo> roomInfoList = roomInfoMapper.getCanUseRoomInfoList();

        // 获取当前月份的第一天和最后一天
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        // 创建一个包含当前月份所有日期的列表
        List<LocalDate> datesInMonth = new ArrayList<>();
        for (LocalDate date = firstDayOfMonth; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
            datesInMonth.add(date);
        }

        // 添加当月的具体房间信息到信息表中 id,roomId,date
        List<RoomDate> roomDateList = new ArrayList<>();
        for (RoomInfo roomInfo : roomInfoList) {
            for (LocalDate date : datesInMonth) {
                RoomDate roomDate = new RoomDate();
                roomDate.setRiId(roomInfo.getId());
                roomDate.setDate(date);
                // 设置临时id, 后续清除
                roomDate.setTempRoomId(roomInfo.getRoomId());
                roomDateList.add(roomDate);
            }
        }

        // 收集房间id
        List<String> roomIdList = roomDateList.stream()
                .map(RoomDate::getTempRoomId)
                .distinct() // 去重
                .toList();

        // 获取对应具体房间的基础价格  roomId,basic_price
        Map<String, BigDecimal> roomPriceMap = roomMapper.getRoomPriceList(roomIdList).stream().collect(Collectors.toMap(Room::getRoomId, Room::getBasicPrice));

        // 设置 price
        roomDateList.forEach(roomDate -> {
            roomDate.setPrice(roomPriceMap.get(roomDate.getTempRoomId()));
        });

        // 批量插入
        roomDateMapper.insert(roomDateList);
        log.info("初始化缓存酒店的每月房间信息完成");
    }
}
