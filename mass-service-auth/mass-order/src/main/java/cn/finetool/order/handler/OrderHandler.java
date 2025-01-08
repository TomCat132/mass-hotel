package cn.finetool.order.handler;

import cn.finetool.api.service.HotelAPIService;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.Strings;
import cn.finetool.common.vo.OrderVO;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import cn.finetool.order.mapper.OrderStatusMapper;
import cn.finetool.order.service.OrderService;
import cn.finetool.order.mapper.RechargeOrderMapper;
import cn.finetool.order.mapper.RoomOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.finetool.common.enums.SysEnum.ROOM_ORDER_PREFIX;
import static cn.finetool.common.util.Response.success;

@Service
public class OrderHandler implements OrderService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrderHandler.class);
    @Resource
    private RechargeOrderMapper rechargeOrderMapper;
    @Resource
    private RoomOrderMapper roomOrderMapper;
    @Resource
    private OrderStatusMapper orderStatusMapper;
    @Resource
    private HotelAPIService hotelAPIService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void deleteOrder(String orderId) {
        //截取订单号前4位,比较查询订单类型
        String prefix = orderId.substring(0, 4);
        if (Strings.equals(prefix, SysEnum.RECHARGE_ORDER_PREFIX.code())) {
            rechargeOrderMapper.update(new UpdateWrapper<RechargeOrder>()
                    .set("is_deleted", Status.IS_DELETED.getCode())
                    .eq("order_id", orderId));
            LOGGER.info("用户逻辑删除充值订单:{}", orderId);
        } else if (Strings.equals(prefix, ROOM_ORDER_PREFIX.code())) {
            roomOrderMapper.update(new UpdateWrapper<RoomOrder>()
                    .set("is_deleted", Status.IS_DELETED.getCode())
                    .eq("order_id", orderId));
            LOGGER.info("用户逻辑删除房间预定订单:{}", orderId);
        }
    }

    @Override
    public Response getAppRechargeOrderList() {
        // 获取应用所有充值订单
        List<OrderVO> rechargeOrderList = rechargeOrderMapper.getAppOrderList();
        return success(rechargeOrderList);
    }

    @Override
    public List<OrderVO> getMerchantOrderList(String merchantId) {
        List<OrderVO> merchantOrderList = new ArrayList<>();
        // （目前）查 room_order ， 后期可能有积分兑换的酒店入住订单
        merchantOrderList.addAll(queryMerchantRoomOrderList(merchantId));
        return merchantOrderList;
    }

    @Override
    public RoomOrderBaseInfo getOrderBaseInfo(String orderId) {
        RoomOrderBaseInfo roomOrderBaseInfo = new RoomOrderBaseInfo();
        RoomOrder roomOrder = roomOrderMapper.selectOne(new QueryWrapper<RoomOrder>()
                .eq("order_id", orderId));
        OrderStatus orderStatus = orderStatusMapper.selectOne(new QueryWrapper<OrderStatus>()
                .eq("order_id", orderId));
        // 酒店相关信息
        roomOrderBaseInfo = hotelAPIService.getBookedRoomBaseInfo(orderId, roomOrder.getRoomDateId());
        roomOrderBaseInfo.setRoomOrder(roomOrder);
        roomOrderBaseInfo.setOrderStatus(orderStatus);

        return roomOrderBaseInfo;
    }

    @Override
    public String findMerchantIdByOrderId(String orderId) {
        return roomOrderMapper.findMerchantIdByOrderId(orderId);
    }

    @Override
    public String findUserIdByOrderId(String orderId) {
        // 截取订单号前4位,比较查询订单类型
        String prefix = orderId.substring(0, 4);
        if (Strings.equals(ROOM_ORDER_PREFIX.code(), prefix)) {
            return roomOrderMapper.findUserIdByOrderId(orderId);
        }
        return "";
    }

    @Override
    public void updateEvaluateStatus(String orderId, Integer isEvaluate) {
        // 截取订单号前4位,比较查询订单类型
        String prefix = orderId.substring(0, 4);
        // 酒店订单
        if (Strings.equals(prefix, ROOM_ORDER_PREFIX.code())) {
            roomOrderMapper.update(new UpdateWrapper<RoomOrder>()
                    .set("is_evaluate", isEvaluate)
                    .eq("order_id", orderId));
        }
    }

    @Override
    public OrderVO findOrderInfoByOrderId(String orderId) {
        // 截取订单号前4位,比较查询订单类型
        String prefix = orderId.substring(0, 4);
        SysEnum sysEnum = SysEnum.getEnumCategory(prefix);
        if (Objects.nonNull(sysEnum)) {
            switch (sysEnum) {
                case ROOM_ORDER_PREFIX:
                    //目前只需要 is_evaluate
                    RoomOrder roomOrder = roomOrderMapper.selectOne(new QueryWrapper<RoomOrder>()
                            .eq("order_id", orderId));
                    OrderVO orderVO = new OrderVO();
                    orderVO.setIsEvaluate(roomOrder.getIsEvaluate());
                    return orderVO;
                case RECHARGE_ORDER_PREFIX:
                    // 充值订单暂时不做处理
                    return null;
                default:
                    return null;
            }
        }
        
        throw new BusinessRuntimeException("订单不存在");
    }

    private List<OrderVO> queryMerchantRoomOrderList(String merchantId) {
        List<OrderVO> roomOrderList = roomOrderMapper.queryMerchantRoomOrderList(merchantId);
        roomOrderList.forEach(orderVO -> {
            orderVO.setOrderType(ROOM_ORDER_PREFIX.code());
        });
        return roomOrderList;
    }

}
