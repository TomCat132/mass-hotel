package cn.finetool.hotel.handler.impl;

import cn.finetool.api.service.OssAPIService;
import cn.finetool.api.service.RechargePlanAPIService;
import cn.finetool.common.dto.AvatarDto;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.FileUrl;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.po.RoomDate;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.FunUtil;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.Strings;
import cn.finetool.common.vo.CheckRoomInfoVO;
import cn.finetool.common.vo.CpMerchantVO;
import cn.finetool.common.vo.RoomInfoVo;
import cn.finetool.hotel.handler.HotelAdminService;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.mapper.RoomBookingMapper;
import cn.finetool.hotel.mapper.RoomDateMapper;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

import static cn.finetool.common.util.Response.success;

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
        roomBookingMapper.changeStatus(id, Status.ROOMBOOKING_CHECK_OUT.getCode());
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
        if (CollectionUtils.isNotEmpty(roomInfoVoList)){
            return success(roomInfoVoList);
        }
        return success(Collections.emptyList());
    }

    @Override
    public Response getRoomInfoVO(String roomId) {
        RoomInfoVo roomInfoVo = new RoomInfoVo();
        List<RoomInfo> roomInfoList = roomInfoMapper.selectList(new QueryWrapper<RoomInfo>()
                .eq("room_id", roomId));
        // 查询房间类型的具体信息列表
        if (CollectionUtils.isNotEmpty(roomInfoList)){
            // 查询所有的 roomInfoIds (1002前缀)
            List<String> roomInfoIds = roomInfoList.stream()
                    .map(RoomInfo::getId)
                    .collect(Collectors.toList());
            // 根据 roomInfoIds 查询图片
            List<FileUrl> fileUrlList = ossAPIService.findImageListByUniqueIds(roomInfoIds);
            Map<String, List<FileUrl>> roomInfoFileUrlListMap = FunUtil.groupBy(fileUrlList, FileUrl::getUniqueId);
//            Map<String, List<String>> roomInfoImageListMap = roomInfoFileUrlListMap.entrySet()
//                    .stream()
//                    .collect(Collectors.toMap(Map.Entry::getKey,
//                            entry -> entry.getValue().stream()
//                                    .map(FileUrl::getUrlImage)
//                                    .collect(Collectors.toList())));
            // 根据 roomInfoId 将图片放入 roomInfoList 中
            List<RoomInfo> roomInfoListHasImage = roomInfoList.stream()
                    .map(roomInfo -> {
                        List<FileUrl> roomInfoImageList = roomInfoFileUrlListMap.get(roomInfo.getId());
                        if (CollectionUtils.isNotEmpty(roomInfoImageList)) {
                            roomInfo.setAvatarList(roomInfoImageList);
                        }
                        return roomInfo;
                    })
                    .collect(Collectors.toList());
            Room room = roomMapper.selectOne(new QueryWrapper<Room>()
                    .eq("room_id", roomId));
            // 组装 RoomInfoVo
            roomInfoVo.setRoomInfoList(roomInfoListHasImage);
            roomInfoVo.setRoomId(room.getRoomId());
            roomInfoVo.setRoomDesc(room.getRoomDesc());
            roomInfoVo.setRoomType(room.getRoomType()); 
            roomInfoVo.setOldPrice(room.getBasicPrice());
            List<FileUrl> imageListByUniqueIds = ossAPIService.findImageListByUniqueIds(Collections.singletonList(roomId));
            if (CollectionUtils.isNotEmpty(imageListByUniqueIds)){
                roomInfoVo.setRoomAvatarList(imageListByUniqueIds);
            }
            // 从 roomInfoList 中取出第一个作为房间图片
            return success(roomInfoVo);
        }
        return success(Collections.emptyList());
    }

    @Override
    public Response updateRoomImage(String roomId, List<MultipartFile> avatarList, List<String> deleteIds) {
        if (CollectionUtils.isNotEmpty(deleteIds)){
            ossAPIService.deleteByIds(deleteIds);
        }
        if (CollectionUtils.isNotEmpty(avatarList)){
            ossAPIService.batchUploadImage(avatarList, roomId);
        }
        return success("更新成功");
    }




}
