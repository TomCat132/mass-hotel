package cn.finetool.order.service.impl;

import cn.finetool.api.service.HotelAPIService;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.order.mapper.RoomOrderMapper;
import cn.finetool.order.service.RoomOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class RoomOrderServiceImpl extends ServiceImpl<RoomOrderMapper, RoomOrder> implements RoomOrderService {

    @Resource
    HotelAPIService hotelAPIService;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public Response createRoomOrder(RoomOrder roomOrder) {
        LocalDateTime nowTime = LocalDateTime.now();

        Integer roomCount =  hotelAPIService.queryResidualRoomInfo(roomOrder.getRoomId(), nowTime.toLocalDate());
        if (roomCount == 0){
            throw new BusinessRuntimeException(BusinessErrors.RUNTIME_ERROR,"房间已售罄");
        }
        RLock roomLock = redissonClient.getLock(RedisCache.ROOM_ORDER_LOCK + roomOrder.getRoomId());

        try {
            boolean isGetLock = roomLock.tryLock(2000,1000, TimeUnit.MICROSECONDS);
            if (isGetLock){
                // 获取锁成功, 创建订单
                // TODO

                return null;
            } else{
                // 获取锁失败
                throw new BusinessRuntimeException(BusinessErrors.RUNTIME_ERROR,"系统繁忙，请稍后重试");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            roomLock.unlock();
        }
    }
}
