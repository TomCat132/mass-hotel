package cn.finetool.order.handler;

import cn.finetool.api.service.HotelAPIService;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.OrderVO;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import cn.finetool.order.mapper.OrderStatusMapper;
import cn.finetool.order.service.OrderService;
import cn.finetool.order.mapper.RechargeOrderMapper;
import cn.finetool.order.mapper.RoomOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderHandler implements OrderService {

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
        if (prefix.equals(CodeSign.RechargeOrderPrefix.getCode())){
            rechargeOrderMapper.update(new UpdateWrapper<RechargeOrder>()
                    .set("is_deleted", Status.IS_DELETED.getCode())
                    .eq("order_id",orderId));
        } else if(prefix.equals(CodeSign.HotelOrderPrefix.getCode())){
            roomOrderMapper.update(new UpdateWrapper<RoomOrder>()
                    .set("is_deleted", Status.IS_DELETED.getCode())
                    .eq("order_id",orderId));
        }
    }

    @Override
    public Response getAppRechargeOrderList() {
        // 获取应用所有充值订单
        List<OrderVO> rechargeOrderList = rechargeOrderMapper.getAppOrderList();
        return Response.success(rechargeOrderList);
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
        roomOrderBaseInfo = hotelAPIService.getBookedRoomBaseInfo(orderId,roomOrder.getRoomDateId());
        roomOrderBaseInfo.setRoomOrder(roomOrder);
        roomOrderBaseInfo.setOrderStatus(orderStatus);
        
        return roomOrderBaseInfo;
    }

    private List<OrderVO> queryMerchantRoomOrderList(String merchantId) {
        List<OrderVO> roomOrderList = roomOrderMapper.queryMerchantRoomOrderList(merchantId);
        roomOrderList.forEach( orderVO -> {
            orderVO.setOrderType(CodeSign.HotelOrderPrefix.getCode());
        });
        return roomOrderList;
    }


}
