package cn.finetool.hotel.handler;

import cn.finetool.common.dto.AvatarDto;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.util.Response;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface HotelAdminService {

    /**
     * Web后台：用户端
     *
     * @param merchantId
     * @return
     */
    Response getHotelReserveRoomBookingList(String merchantId);

    /**
     * Web后台：sys端
     *
     * @return
     */
    Response queryCooperationMerchantList();

    Response updateStatus(PlanDto planDto);

    Response merchantInfo(String merchantId);

    Response getWillFinishOrderList(String merchantId);

    Response startFinishRoomOut(Integer id);
    /** ========= 根据房间日期ID获取房间价格 ========= */
    BigDecimal getRoomDatePriceById(Integer roomDateId);
    /** ========= 茶轩房间名称列表 ========= */
    Response getRoomNameList(String merchantId);
    /** ========= 根据房间ID获取房间信息VO ========= */
    Response getRoomInfoVO(String roomId);
    /** ========= 更新房间宣传图片 ========= */
    Response updateRoomImage(String roomId, List<MultipartFile> filelist, List<String> deleteIds);
}
