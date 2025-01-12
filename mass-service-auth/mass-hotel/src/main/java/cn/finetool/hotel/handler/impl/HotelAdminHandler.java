package cn.finetool.hotel.handler.impl;

import cn.finetool.api.service.OssAPIService;
import cn.finetool.api.service.RechargePlanAPIService;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.ChatMessage;
import cn.finetool.common.po.FileUrl;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.po.RoomDate;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.CheckRoomInfoVO;
import cn.finetool.common.vo.CpMerchantVO;
import cn.finetool.common.vo.RoomInfoVo;
import cn.finetool.common.vo.SingleRoomInfoVO;
import cn.finetool.hotel.handler.HotelAdminService;
import cn.finetool.hotel.mapper.ChatMessageMapper;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.mapper.RoomBookingMapper;
import cn.finetool.hotel.mapper.RoomDateMapper;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

import static cn.finetool.common.util.Response.success;
import static cn.finetool.hotel.HotelApplication.ID_WORKER;

@Service
public class HotelAdminHandler implements HotelAdminService {

    @Resource
    private HotelMapper hotelMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private RoomInfoMapper roomInfoMapper;
    @Resource
    private RoomBookingMapper roomBookingMapper;
    @Resource
    private RoomDateMapper roomDateMapper;
    @Resource
    private RechargePlanAPIService rechargePlanAPIService;
    @Resource
    private OssAPIService ossAPIService;
    @Resource
    private ChatMessageMapper chatMessageMapper;
    


    @Override
    public Response getHotelReserveRoomBookingList(String merchantId) {
        // 根据 HotelId 查询 所有预定房间信息
        List<CheckRoomInfoVO> roombookingList = hotelMapper.queryHotelReserveRoomBookingList(merchantId);
        roombookingList.stream()
                .map(roombookingVO -> {
                    // 找到 phone
                    Integer roomDateId = roombookingVO.getRoomBooking().getRoomDateId();
                    String phone = hotelMapper.queryUserColumn(roomDateId);
                    roombookingVO.setPhone(phone);
                    // 找到房间号
                    return roombookingVO;
                }).toList();
        return success(roombookingList);
    }

    @Override
    public Response queryCooperationMerchantList() {
        List<CpMerchantVO> merchantList = new ArrayList<>();
        //暂时只有酒店
        List<Hotel> hotelList = hotelMapper.selectList(null);
        merchantList = hotelList.stream()
                .map(hotel -> {
                    CpMerchantVO merchantVO = new CpMerchantVO();
                    merchantVO.setMerchantId(hotel.getMerchantId());
                    merchantVO.setMerchantName(hotel.getHotelName());
                    merchantVO.setMerchantType(SysEnum.MERCHANT_HOTEL_PREFIX.code());
                    merchantVO.setCity(hotel.getCity());
                    merchantVO.setAddress(hotel.getAddress());
                    merchantVO.setPhoneNumber(hotel.getPhoneNumber());
                    merchantVO.setMerchantStatus(hotel.getStatus());
                    return merchantVO;
                }).toList();
        return success(merchantList);
    }

    @Override
    public Response updateStatus(PlanDto planDto) {
        rechargePlanAPIService.updateRechargePlanStatus(planDto.getPlanId(), planDto.getStatus());
        return success("更新成功");
    }

    @Override
    public Response merchantInfo(String merchantId) {
        //目前只从 tb_hotel 表中查询数据 merchantId 前缀:1001
        //截取 merchantId 前缀
        Long prefix = Long.valueOf(merchantId.substring(0, 4));
        Object baseInfo = null;
        if (Strings.equals(prefix.toString(), SysEnum.MERCHANT_HOTEL_PREFIX.code())) {
            //根据 merchantId 查询 hotel 信息、room 信息
            Hotel hotel = hotelMapper.selectOne(new QueryWrapper<Hotel>().eq("merchant_id", merchantId));
            List<Room> roomList = new ArrayList<>();
            List<Room> roomBaseInfoList = roomMapper.selectList(new QueryWrapper<Room>()
                    .eq("hotel_id", hotel.getHotelId()));
            roomBaseInfoList.forEach(room -> {
                List<RoomInfo> roomInfoList = roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                        .eq("room_id", room.getRoomId()));
                room.addAll(roomInfoList);
                roomList.add(room);
            });
            hotel.setRoomList(roomList);

            Map<String, Object> result = new HashMap<>();
            result.put("hotel", hotel);
            result.put("merchantType", SysEnum.MERCHANT_HOTEL_PREFIX.getCode());

            baseInfo = result;
        }

        return success(baseInfo);
    }


    @Override
    public Response getWillFinishOrderList(String merchantId) {
        List<CheckRoomInfoVO> willFinishOrderList = hotelMapper.queryWillFinishOrderList(merchantId);
        willFinishOrderList = willFinishOrderList.stream()
                .map(roombookingVO -> {
                    // 找到 phone
                    Integer roomDateId = roombookingVO.getRoomBooking().getRoomDateId();
                    String phone = hotelMapper.queryUserColumn(roomDateId);
                    roombookingVO.setPhone(phone);
                    return roombookingVO;
                }).toList();
        return success(willFinishOrderList);
    }

    @Override
    public Response startFinishRoomOut(Integer id) {
        roomBookingMapper.changeStatus(id, Status.ROOMBOOKING_CHECK_OUT_WAIT_CHECK.getCode());
        RoomBooking roomBooking = roomBookingMapper.selectById(id);
        RoomDate roomDate = roomDateMapper.selectById(roomBooking.getRoomDateId());
        RoomInfo roomInfo = roomInfoMapper.selectById(roomDate.getRiId());
        roomInfoMapper.changeStatus(id, Status.ROOM_INFO_CLEANING.getCode());
        return success("开始办理退房");
    }

    @Override
    public BigDecimal getRoomDatePriceById(Integer roomDateId) {
        RoomDate roomDate = roomDateMapper.selectById(roomDateId);
        return roomDate.getPrice();
    }

    @Override
    public Response getRoomNameList(String merchantId) {
        List<RoomInfoVo> roomInfoVoList = roomMapper.getRoomNameList(merchantId);
        if (CollectionUtils.isNotEmpty(roomInfoVoList)) {
            return success(roomInfoVoList);
        }
        return success(Collections.emptyList());
    }

    @Override
    public Response getRoomInfoVO(String roomId) {
        RoomInfoVo roomInfoVo = new RoomInfoVo();
        Room room = roomMapper.selectOne(new QueryWrapper<Room>()
                .eq("room_id", roomId));
        roomInfoVo.setRoomId(room.getRoomId());
        roomInfoVo.setRoomDesc(room.getRoomDesc());
        roomInfoVo.setRoomType(room.getRoomType());
        roomInfoVo.setOldPrice(room.getBasicPrice());
        List<RoomInfo> roomInfoList = roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                .eq("room_id", roomId));
        List<String> roomIds = Collections.singletonList(roomId);
        List<FileUrl> imageListByUniqueIds = ossAPIService.findImageListByUniqueIds(roomIds);
        if (CollectionUtils.isNotEmpty(imageListByUniqueIds)) {
            roomInfoVo.setRoomAvatarList(imageListByUniqueIds);
        }
        // 查询房间类型的具体信息列表
        if (CollectionUtils.isNotEmpty(roomInfoList)) {
            // 查询所有的 roomInfoIds (1002前缀)
//            List<String> roomInfoIds = roomInfoList.stream()
//                    .map(RoomInfo::getId)
//                    .collect(Collectors.toList());


//            Map<String, List<FileUrl>> roomInfoFileUrlListMap = FunUtil.groupBy(fileUrlList, FileUrl::getUniqueId);
//            Map<String, List<String>> roomInfoImageListMap = roomInfoFileUrlListMap.entrySet()
//                    .stream()
//                    .collect(Collectors.toMap(Map.Entry::getKey,
//                            entry -> entry.getValue().stream()
//                                    .map(FileUrl::getUrlImage)
//                                    .collect(Collectors.toList())));
            // 根据 roomInfoId 将图片放入 roomInfoList 中
//            List<RoomInfo> roomInfoListHasImage = roomInfoList.stream()
//                    .map(roomInfo -> {
//                        List<FileUrl> roomInfoImageList = roomInfoFileUrlListMap.get(roomInfo.getId());
//                        if (CollectionUtils.isNotEmpty(roomInfoImageList)) {
//                            roomInfo.setAvatarList(roomInfoImageList);
//                        }
//                        return roomInfo;
//                    })
//                    .collect(Collectors.toList());

            // 组装 RoomInfoVo
            roomInfoVo.setRoomInfoList(roomInfoList);
        }
        return success(roomInfoVo);
    }

    @Override
    public Response updateRoomImage(String id, List<MultipartFile> avatarList, List<String> deleteIds) {
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            ossAPIService.deleteByIds(deleteIds);
        }
        if (CollectionUtils.isNotEmpty(avatarList)) {
            ossAPIService.batchUploadImage(avatarList, id);
        }
        return success("更新成功");
    }

    @Override
    public Response findRoomInfoById(String id, String queryTime) {
        SingleRoomInfoVO singleRoomInfoVO = new SingleRoomInfoVO();
        // 查询 roomInfo
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        // 根据 roomInfoIds 查询图片
        List<FileUrl> fileUrlList = ossAPIService.findImageListByUniqueIds(Collections.singletonList(roomInfo.getId()));
        if (CollectionUtils.isNotEmpty(fileUrlList)) {
            roomInfo.setAvatarList(fileUrlList);
        }
        singleRoomInfoVO.setRoomInfo(roomInfo);
        // 查询 房间当月信息以及是否有用户预定
        // queryTime: String yyyy-MM 格式
        // date: LocalDate yyyy-MM-dd
        YearMonth queryDate = TimeUtil.parseToYearMonth(queryTime);
        // 获取当年当月第一天和最后一天
        LocalDate firstDayOfMonth = queryDate.atDay(1);
        LocalDate lastDayOfMonth = queryDate.atEndOfMonth();
        List<RoomDate> roomDates = roomDateMapper.selectList(new QueryWrapper<RoomDate>()
                .eq("ri_id", id)
                .between("date", firstDayOfMonth, lastDayOfMonth));
        if (CollectionUtils.isNotEmpty(roomDates)) {
            singleRoomInfoVO.setRoomDateList(roomDates);
        }
        return success(singleRoomInfoVO);
    }

    @Override
    public void updateRoomBookingStatus(String orderId, Integer status) {
        roomBookingMapper.changeStatusByOrderId(orderId, status);
    }

    @Override
    public Response updateRoomDatePrice(Integer id, BigDecimal newPrice) {
        roomDateMapper.update(new UpdateWrapper<RoomDate>()
                .set("price", newPrice)
                .eq("id", id));
        return success("价格已更新");
    }

    @Override
    public Response findRoomInfoAvatarList(String id) {
        List<FileUrl> fileUrlList = ossAPIService.findImageListByUniqueIds(Collections.singletonList(id));
        if (CollectionUtils.isNotEmpty(fileUrlList)) {
            return success(fileUrlList);
        }
        return success(Collections.emptyList());
    }

    @Override
    public Response updateRoom(RoomDto roomDto) {
        roomMapper.update(new UpdateWrapper<Room>()
                .set("room_desc", roomDto.getRoomDescStr())
                .set("room_type", roomDto.getRoomType())
                .set("basic_price", roomDto.getBasicPrice())
                .eq("room_id", roomDto.getRoomId()));
        return success("已保存");
    }

    @Override
    public Response deleteRoom(String roomId) {

        List<RoomInfo> roomInfoList = roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                .eq("room_id", roomId));
        if (CollectionUtils.isNotEmpty(roomInfoList)) {
            List<String> roomInfoIds = roomInfoList.stream()
                    .map(RoomInfo::getId)
                    .collect(Collectors.toList());
            // 再删除 roomInfo
            // TODO： 有问题需要修挂
            roomInfoMapper.delete(new QueryWrapper<RoomInfo>()
                    .in("id", roomInfoIds));
            roomInfoIds.add(roomId);
            // 删除 roomInfo 相关的 roomDate
            roomDateMapper.delete(new QueryWrapper<RoomDate>()
                    .in("ri_id", roomInfoIds));
            // 删除 roomInfo,room 相关的图片
            ossAPIService.deleteByIds(roomInfoIds);
            // 删除 room
            roomMapper.delete(new QueryWrapper<Room>()
                    .eq("room_id", roomId));
        }
        return success("操作成功");
    }

    @Override
    public Response getCanReserveRoomByDate(String merchantId, String queryDate) {
        int count = getRoomUsageOfDifferentSituationByDate(merchantId, queryDate, Status.ROOM_DATE_CAN_USE.code());
        return success(count);
    }

    @Override
    public Response getReservedRoomByDate(String merchantId, String queryDate) {
        int count = getRoomUsageOfDifferentSituationByDate(merchantId, queryDate, Status.ROOM_DATE_RESERVED.code());
        return success(count);
    }

    @Override
    public Response getUsingRoomByDate(String merchantId, String queryDate) {
        int count = getRoomUsageOfDifferentSituationByDate(merchantId, queryDate, Status.ROOMBOOKING_CHECK_IN.code());
        return success(count);
    }

    @Override
    public Response getCleaningRoomByDate(String merchantId, String queryDate) {
        int count = getRoomUsageOfDifferentSituationByDate(merchantId, queryDate, Status.ROOM_INFO_CLEANING.code());
        return success(count);
    }

    @Override
    public String findMerchantIdByOrderId(String orderId) {
        // 截取订单前4位
        String prefix = orderId.substring(0, 4);
        // 酒店订单前缀
        if (Strings.equals(SysEnum.ROOM_ORDER_PREFIX.code(), prefix)){
           return roomBookingMapper.findMerchantIdByOrderId(orderId);
        }
        
        return "";
    }

    @SneakyThrows
    @Override
    public void saveWebSocketMessage(String message, String receiverId) {
        Map messageMap = JsonUtil.fromJsonString(message, Map.class);
        String chatId = (String) messageMap.get("chatId");
        String senderId = (String) messageMap.get("senderId");
        String senderTime = (String) messageMap.get("senderTime");
        String messageContent = (String) messageMap.get("message");
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(SysEnum.CHAT_MESSAGE_PREFIX.code() + ID_WORKER.nextId());
        chatMessage.setChatId(chatId);
        chatMessage.setSenderId(senderId);
        chatMessage.setSenderTime(TimeUtil.parseToLocalDateTime(senderTime));
        chatMessage.setReceiverId(receiverId);
        chatMessage.setMessage(messageContent);
        chatMessage.setReadState(Status.MESSAGE_UNREAD.code());
        chatMessageMapper.insert(chatMessage);
    }


    private int getRoomUsageOfDifferentSituationByDate(String merchantId, String queryDate, Integer status) {
        //先查所有merchantId的room、roomInfo -> roomDate
        Hotel hotel = hotelMapper.selectOne(new QueryWrapper<Hotel>()
                .eq("merchant_id", merchantId));
        // 查询商户所有的roomIds
        List<String> roomIds = roomMapper.selectList(new QueryWrapper<Room>()
                        .eq("hotel_id", hotel.getHotelId()))
                .stream()
                .map(Room::getRoomId)
                .collect(Collectors.toList());
        // 查询可预定的房间
        if (Strings.equals(Status.ROOM_DATE_CAN_USE.code(), status)) {
            List<String> roomInfoPrimaryIds = roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                            .in("room_id", roomIds))
                    .stream()
                    .map(RoomInfo::getId)
                    .collect(Collectors.toList());
            return roomDateMapper.selectList(new QueryWrapper<RoomDate>()
                    .in("ri_id", roomInfoPrimaryIds)
                    .eq("status", Status.ROOM_DATE_CAN_USE.code())
                    .eq("date", TimeUtil.parseToLocalDate(queryDate))).size();
        }// 查看已预定的房间 (room_date)
        else if (Strings.equals(Status.ROOM_DATE_RESERVED.code(), status)) {
            List<String> roomInfoPrimaryIds = roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                            .in("room_id", roomIds))
                    .stream()
                    .map(RoomInfo::getId)
                    .collect(Collectors.toList());
            return roomDateMapper.selectList(new QueryWrapper<RoomDate>()
                    .in("ri_id", roomInfoPrimaryIds)
                    .eq("status", Status.ROOM_DATE_RESERVED.code())
                    .eq("date", TimeUtil.parseToLocalDate(queryDate))).size();
        }// 查看入住中的数量
        else if (Strings.equals(Status.ROOMBOOKING_CHECK_IN.code(), status)) {
            List<String> roomInfoPrimaryIds = roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                            .in("room_id", roomIds))
                    .stream()
                    .map(RoomInfo::getId)
                    .collect(Collectors.toList());
            List<Integer> roomDatePrimaryIds = roomDateMapper.selectList(new QueryWrapper<RoomDate>()
                            .in("ri_id", roomInfoPrimaryIds))
                    .stream()
                    .map(RoomDate::getId)
                    .collect(Collectors.toList());
            return roomBookingMapper.selectList(new QueryWrapper<RoomBooking>()
                    .in("room_date_id", roomDatePrimaryIds)
                    .eq("status", Status.ROOMBOOKING_CHECK_IN.code())).size();
        }// 查询清洁中的房间 - 只查看当天
        else if (Strings.equals(Status.ROOM_INFO_CLEANING.code(), status)) {
            return roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                    .in("room_id", roomIds)
                    .eq("status", Status.ROOM_INFO_CLEANING.code())).size();
        }
        return 0;
    }
}
