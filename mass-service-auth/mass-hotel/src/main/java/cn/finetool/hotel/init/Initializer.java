package cn.finetool.hotel.init;

import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.vo.HotelVo;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.service.HotelService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class Initializer {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private HotelMapper hotelMapper;

    /** ============= 初始化缓存酒店的经纬度信息 =========== */
    @PostConstruct
    public void InitHotelGeo(){

       List<HotelVo> hotelVoList = hotelMapper.getHotelGeoList();

       hotelVoList.forEach(hotelVo -> {
          redisTemplate.opsForGeo().add(RedisCache.HOTEL_LOCATION_LIST,
                  new Point(hotelVo.getHotelLng(),hotelVo.getHotelLat()), hotelVo.getHotelId().toString());
       });
    }
}
