package br.com.order.service;

import br.com.order.domain.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderValidatorService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "orders")
    public void orderValidator(String orderContent) {

        try {
            Order order = objectMapper.readValue(orderContent, Order.class);
            order.validate();

            log.info("Order received: {}", order.getId());

            if (order.validationMessages.isEmpty()) {
                sendMessageToOrderFinisher("{\"status\": \"ORDER_VALID\", id:" + order.getId() + "}");
            } else {
                log.info("Order is not valid");

                sendMessageToOrderFinisher("{\"status\": \"ORDER_NOT_VALID\", id:" + order.getId() + ", "
                        + order.getValidationMessagesAsJson() + "}");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "order-finisher")
    public void orderFinisher(String orderFinisher) {
        try {
            log.info("Finishing order: {}", orderFinisher);
        } catch (Exception e) {
            throw e;
        }
    }

    private void sendMessageToOrderFinisher(String message) {
        try {
            jmsTemplate.convertAndSend("order-finisher", message);
        } catch (Exception e) {
            throw e;
        }
    }
}
