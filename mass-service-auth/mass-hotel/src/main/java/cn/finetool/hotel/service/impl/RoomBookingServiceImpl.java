package cn.finetool.hotel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.ExamineDto;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.CheckRoomInfoVO;
import cn.finetool.hotel.mapper.RoomBookingMapper;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import cn.finetool.hotel.service.RoomBookingService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomBookingServiceImpl extends ServiceImpl<RoomBookingMapper, RoomBooking> implements RoomBookingService {

    @Resource
    private RoomBookingMapper roomBookingMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Response queryRoomBooking(Integer queryType, String queryValue) {
        // 通过userId 获取缓存的 hotelId
        String userId = StpUtil.getLoginIdAsString();
        String hotelId = (String) redisTemplate.opsForValue().get(RedisCache.USER_MERCHANT_BINDING + userId);
        List<RoomBooking> roomBookingList = new ArrayList<>();
        // 查询时绑定hotelId,避免越权
        roomBookingList = roomBookingMapper.queryRoomBookingList(queryType, queryValue,hotelId);
        if (roomBookingList.isEmpty()) {
            return Response.error("未查询到订单信息");
        }
        return Response.success(roomBookingList);
    }

    @Override
    public Response startHandleCheckIn(String orderId) {
        // 更爱 status : 已预定 -》 办理中
        roomBookingMapper.changeStatus(orderId, Status.ROOMBOOKING_DOING.getCode());
        return Response.success("开始办理入住");
    }

    @Override
    public Response checkRoomDateInfo(Integer id) {
        // 查数据
        RoomBooking roomBooking = roomBookingMapper.selectById(id);
        RoomInfo roomInfo = roomInfoMapper.selectById(roomBooking.getRoomDateId());
        Room room = roomMapper.selectById(roomInfo.getRoomId());
        CheckRoomInfoVO checkRoomInfoVO = new CheckRoomInfoVO();
        checkRoomInfoVO.setRoomBooking(roomBooking);
        checkRoomInfoVO.setRoom(room);
        checkRoomInfoVO.setStatus(roomInfo.getStatus());
        return Response.success(checkRoomInfoVO);
    }

    @Override
    public Response receiveDeposit(String id) {
        roomBookingMapper.update(new UpdateWrapper<RoomBooking>()
                .eq("id",id)
                .set("security_deposit",true));
        return Response.success("已确认缴纳押金");
    }

    @Override
    public Response finishHandleCheckIn(ExamineDto examineDto) {
        // 有一步手续未完成禁止确认入住
        if (!examineDto.isConfirmRoom() || !examineDto.isConfirmDeposit()
        || !examineDto.isBindingDoorKey()){
            return Response.error("请先完成手续确认");
        }
        // status : 办理中 -》 入住中
        roomBookingMapper.changeStatus(examineDto.getOrderId(), Status.ROOMBOOKING_CHECK_IN.getCode());
        return Response.success("已确认");
    }

    @Override
    public Response bindingDoorKey(Integer id,String doorKey) {

        roomBookingMapper.update(new UpdateWrapper<RoomBooking>()
                .set("door_key",doorKey)
                .eq("id",id));
        return Response.success("门禁卡绑定成功");
    }

}
