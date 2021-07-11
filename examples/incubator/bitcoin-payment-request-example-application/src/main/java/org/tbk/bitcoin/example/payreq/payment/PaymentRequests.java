package org.tbk.bitcoin.example.payreq.payment;

import org.jmolecules.ddd.types.Association;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.tbk.bitcoin.example.payreq.order.Order;

import java.util.Optional;

interface PaymentRequests extends PagingAndSortingRepository<PaymentRequest, PaymentRequest.PaymentRequestId> {

    /**
     * Returns the payment registered for the given {@link Order}.
     */
    default Optional<PaymentRequest> findByOrder(Order.OrderId id) {
        return findByOrder(Association.forId(id));
    }

    Optional<PaymentRequest> findByOrder(Association<Order, Order.OrderId> order);
}