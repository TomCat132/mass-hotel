package cn.finetool.activity.service;

import cn.finetool.activity.bo.PointExchangeBO;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.po.PointExchange;
import cn.finetool.common.po.PointUsageRecord;
import cn.finetool.common.po.SignReward;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.PointProDuctVO;
import cn.finetool.common.vo.PointUsageRecordVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;

public interface VoucherService extends IService<Voucher> {

    Response addVoucher(VoucherDto voucherDto) throws JsonProcessingException;
    
    /**
     * 获取所有类型的活动券列表
     *
     * @param merchantId 商户编号
     */
    Response getAllCategoryVoucherList(String merchantId);

    /**
     * @param voucherType 类型
     * @param voucherId   活动券编号
     * @param status      状态
     */
    void updateVoucherStatus(Integer voucherType, String voucherId, Integer status);

    /**
     * 获取有效的活动券列表
     */
    Response getValidVoucherList();

    /**
     * 根据活动券编号获取活动券基本信息
     * @param voucherId：活动券编号
     */
    VoucherDto getVoucherBaseInfo(String voucherId);

    /**
     * 更新活动券状态为：已使用
     * @param voucherId 活动券编号
     * @param status 装填
     */
    void usedVoucher(String voucherId, Integer status);

    /**
     * 活动券编号
     * @param voucherId 活动券编号
     */
    Response deleteVoucherByVoucherId(String voucherId);

    /**
     * AP: 设置签到奖励
     * @param signReward: 签到奖励 PO
     */
    Response signRewardSetting(SignReward signReward);

    /**
     * AP: 修改奖励内容
     * @param rewardId: 奖励编号
     */
    Response changeRewardContent(String rewardId, String content);

    /**
     * AP: 按月获取奖励内容列表
     * @param date: 日期 yyyy-MM
     */
    Response getRewardContentListByMonth(String date);

    /**
     * C端: 获取限时活动列表
     */
    Response getTimeLimitedActivities();

    /**
     * 判断用户是否已领取活动券
     * @param activityId: 活动编号
     */
    Response isReceivedVoucher(String activityId);

    /**
     * C端: 获取活动详情
     * @param activityId: 活动编号
     */
    Response getActivityInfo(String activityId);

    /**
     * AP：获取平台活动券列表
     * @return
     */
    Response getPlatFormVoucherList();

    /**
     * AP: 添加积分兑换商品
     * @param pointExchange
     * @return
     */
    String addPointsMallProduct(PointExchange pointExchange);

    /**
     * AP: 用户兑换商品
     * @param pointExchangeBO
     * @return
     */
    String userRedeemProduct(PointExchangeBO pointExchangeBO);

    /**
     * AP: 获取积分商城商品列表
     * @return
     */
    List<PointProDuctVO> getPointMallProductList();

    /**
     * C端：
     * @return
     */
    List<PointUsageRecordVO> pointExchangeRecordList();
}
