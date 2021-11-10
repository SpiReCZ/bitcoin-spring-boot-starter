package org.tbk.bitcoin.example.payreq.order;

import lombok.NonNull;
import lombok.Value;


@Value(staticConstructor = "of")
public class OrderCompleted implements OrderStateChanged {

    @NonNull
    Order.OrderId orderId;

    public String toString() {
        return "OrderInProgressEvent";
    }
}