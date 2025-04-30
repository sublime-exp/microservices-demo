package com.sub.microservices.order.service;

import com.sub.microservices.order.client.InventoryClient;
import com.sub.microservices.order.dto.OrderRequest;
import com.sub.microservices.order.event.OrderPlacedEvent;
import com.sub.microservices.order.model.Order;
import com.sub.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        boolean isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (isProductInStock) {
            Order order = new Order()
                    .setOrderNumber(UUID.randomUUID().toString())
                    .setPrice(orderRequest.price())
                    .setQuantity(orderRequest.quantity())
                    .setSkuCode(orderRequest.skuCode());
            orderRepository.save(order);

            OrderPlacedEvent event = new OrderPlacedEvent();
            event.setOrderNumber(order.getOrderNumber());
            event.setEmail(orderRequest.userDetails().email());
            event.setFirstName(orderRequest.userDetails().firstName());
            event.setLastName(orderRequest.userDetails().lastName());
            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", event);
            kafkaTemplate.send("order-placed", event);
            log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", event);

        } else {
            throw new RuntimeException("Product with SkuCode "
                    + orderRequest.skuCode() + " is not in stock");
        }
    }
}
