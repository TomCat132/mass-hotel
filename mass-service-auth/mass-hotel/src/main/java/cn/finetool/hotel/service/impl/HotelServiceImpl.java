package cn.finetool.hotel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.HotelVo;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.service.HotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private HotelMapper hotelMapper;

    @Override
    public Response addHotelInfo(Hotel hotel) {

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
            return Response.error("No hotels found within the specified range.");
        }

        // 3. 计算用户到每个酒店的距离
        List<HotelVo> hotelVoList = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> result : nearbyHotels) {

            String hotelIdStr = (String) result.getContent().getName();


            // 查询酒店的经纬度
            List<Point> hotelPoints = redisTemplate.opsForGeo().position(RedisCache.HOTEL_LOCATION_LIST, hotelIdStr);
            Point hotelPoint = hotelPoints.get(0);
            hotelPoint = new Point(hotelPoint.getX(), hotelPoint.getY());

            // 使用 GEODIST 命令计算用户和酒店之间的距离，单位为千米
            Distance distance = redisTemplate.opsForGeo().distance(RedisCache.HOTEL_LOCATION_LIST, userKey, hotelIdStr, RedisGeoCommands.DistanceUnit.KILOMETERS);
            if (distance == null) {
                continue; // 忽略无法计算距离的酒店
            }

            // 根据id 获取酒店详细地址 、 最低价格 、 距离
            HotelVo hotelVo = hotelMapper.queryHotelInfo(Integer.parseInt(hotelIdStr));
            if (hotelVo == null){
                log.info("新添加的临时用户位置，不进行查询");
                continue;
            }
            hotelVo.setHotelLng(hotelPoint.getX());
            hotelVo.setHotelLat(hotelPoint.getY());
            hotelVo.setDistance(distance.getValue());

            hotelVoList.add(hotelVo);
        }

        // 删除临时存储的用户位置
        redisTemplate.opsForGeo().remove(RedisCache.HOTEL_LOCATION_LIST, userKey);

        return Response.success(hotelVoList);
    }
}
