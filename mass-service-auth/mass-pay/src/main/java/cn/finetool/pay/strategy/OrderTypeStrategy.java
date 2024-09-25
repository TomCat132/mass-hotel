package cn.finetool.pay.strategy;

public interface OrderTypeStrategy {

    Object queryOrder(String orderId);

    boolean equalsPayAmount(String orderId,Integer userPayAmount);

    boolean verifyOrderIsPayAmount(String orderId);

    void handleOrder(String orderId);

    void deleteOrderFlag(String orderId);
}
