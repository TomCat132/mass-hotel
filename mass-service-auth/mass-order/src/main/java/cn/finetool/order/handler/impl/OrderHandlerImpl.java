package cn.finetool.order.handler.impl;

import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.order.handler.OrderHandler;
import cn.finetool.order.mapper.RechargeOrderMapper;
import cn.finetool.order.mapper.RoomOrderMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OrderHandlerImpl implements OrderHandler {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;

    @Resource
    private RoomOrderMapper roomOrderMapper;

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
}
