package cn.finetool.recharge.service;

import cn.finetool.common.po.RechargePlans;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface RechargePlanService extends IService<RechargePlans> {

    Response addChargePlan(RechargePlans rechargePlans) throws JsonProcessingException;

    boolean updateRechargePlanStatus(Integer planId);

    Response validRechargePlanList();
}
