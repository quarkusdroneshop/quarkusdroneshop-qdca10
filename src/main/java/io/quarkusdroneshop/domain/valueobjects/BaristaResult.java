package io.quarkusdroneshop.domain.valueobjects;

import io.quarkusdroneshop.barista.domain.EightySixEvent;

public class BaristaResult {

    private OrderUp orderUp;

    private EightySixEvent eightySixEvent;

    private boolean isEightySixed;

    public BaristaResult(OrderUp orderUp) {
        this.orderUp = orderUp;
    }

    public BaristaResult(EightySixEvent eightySixEvent) {
        this.eightySixEvent = eightySixEvent;
        this.isEightySixed = true;
    }

    public EightySixEvent getEightySixEvent() {
        return eightySixEvent;
    }

    public void setEightySixEvent(EightySixEvent eightySixEvent) {
        this.eightySixEvent = eightySixEvent;
    }

    public OrderUp getOrderUp() {
        return orderUp;
    }

    public void setOrderUp(OrderUp orderUp) {
        this.orderUp = orderUp;
    }

    public boolean isEightySixed() {
        return isEightySixed;
    }

    public void setEightySixed(boolean eightySixed) {
        isEightySixed = eightySixed;
    }
}
