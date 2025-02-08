package cn.finetool.order.handler;

import cn.finetool.api.service.AccountAPIService;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.api.service.OssAPIService;
import cn.finetool.common.enums.PayType;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.OrderVO;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import cn.finetool.order.mapper.OrderStatusMapper;
import cn.finetool.order.service.OrderService;
import cn.finetool.order.mapper.RechargeOrderMapper;
import cn.finetool.order.mapper.RoomOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;
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
    @Resource
    private AccountAPIService accountAPIService;
    @Resource
    private OssAPIService ossAPIService;

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

    @Override
    public void handleOrder(String orderId, Integer status) {
        // status 为即将执行的动作 
        orderStatusMapper.changeOrderStatus(orderId, status, TimeUtil.now(), null);
        // 处理订单: 金额返还（根据支付方式）
        OrderStatus orderStatus = orderStatusMapper.selectOne(new QueryWrapper<OrderStatus>()
                .eq("order_id", orderId));
        // 先判断订单类型
        String prefix = orderId.substring(4);
        if (Strings.equals(ROOM_ORDER_PREFIX.code(), prefix)) {
            RoomOrder roomOrder = roomOrderMapper.selectOne(new QueryWrapper<RoomOrder>()
                    .eq("order_id", orderId));
            if (Strings.equals(PayType.ACCOUNT_PAY.code(), orderStatus.getPayType())) {
                //账户余额支付, 直接返还账户余额
                accountAPIService.returnAccountBalance(roomOrder.getUserId(), roomOrder.getUserPayAmount(), true);
            }
        }

    }

    @Override
    public RoomOrderBaseInfo findOrderBaseInfoByUserId(String userId) {
        // 查询最新的入住订单数据
        RoomOrder roomOrder = roomOrderMapper.selectOne(new QueryWrapper<RoomOrder>()
                .eq("user_id", userId)
                .orderByDesc("create_time")
                .last("limit 1"));
        if (Objects.nonNull(roomOrder)) {
            RoomOrderBaseInfo orderBaseInfo = getOrderBaseInfo(roomOrder.getOrderId());
            orderBaseInfo.setExtraData(new HashMap<>());
            // 查询房间一张宣传图
            String image = ossAPIService.findImageByUniqueId(orderBaseInfo.getRoomInfo().getId());
            if (Strings.isNotBlank(image)) {
                orderBaseInfo.getExtraData().put("image", image);
            }
            // TODO:查询是否有其它服务订单
            return orderBaseInfo;
        }
        return null;
    }

    @Override
    public List<RoomOrderBaseInfo> findOrderBaseInfoListByUserId(String userId) {
        List<String> orderIds = roomOrderMapper.selectList(new QueryWrapper<RoomOrder>()
                        .eq("user_id", userId))
                .stream()
                .map(RoomOrder::getOrderId)
                .collect(Collectors.toList());
        List<RoomOrderBaseInfo> roomOrderBaseInfoList = new ArrayList<>();
        for (String orderId : orderIds) {
            RoomOrderBaseInfo orderBaseInfo = getOrderBaseInfo(orderId);
            // 查询房间一张宣传图
            if (Objects.nonNull(orderBaseInfo)) {
                orderBaseInfo.setExtraData(new HashMap<>());
                String image = ossAPIService.findImageByUniqueId(orderBaseInfo.getRoomInfo().getId());
                if (Strings.isNotBlank(image)) {
                    orderBaseInfo.getExtraData().put("image", image);
                }
                roomOrderBaseInfoList.add(orderBaseInfo);
            }
        }
        //过滤掉用户已删除的订单
        roomOrderBaseInfoList = roomOrderBaseInfoList.stream()
                .filter(orderBaseInfo -> Strings.equals(Status.NOT_DELETED.code(), orderBaseInfo.getRoomOrder().getIsDeleted()))
                .collect(Collectors.toList());
        return roomOrderBaseInfoList;
    }

    private List<OrderVO> queryMerchantRoomOrderList(String merchantId) {
        List<OrderVO> roomOrderList = roomOrderMapper.queryMerchantRoomOrderList(merchantId);
        roomOrderList.forEach(orderVO -> {
            orderVO.setOrderType(ROOM_ORDER_PREFIX.code());
        });
        return roomOrderList;
    }

}
