package com.restaurant.controller;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.repository.OrderRepository;
import com.restaurant.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private OrderController controller;

    @Test
    void placeOrderReturnsUnauthorizedForInvalidToken() {
        OrderController.OrderRequest request = new OrderController.OrderRequest("A", "a@mail.com", List.of(), 10.0, "");

        ResponseEntity<?> response = controller.placeOrder(null, request);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void placeOrderSavesAndReturnsOrderForValidCustomerToken() {
        OrderController.OrderRequest request = new OrderController.OrderRequest(
                "Anu",
                "anu@mail.com",
                List.of(new OrderItem(1L, "Soup", 1, 6.5)),
                6.5,
                "No onion"
        );
        Order saved = new Order();
        saved.setId(100L);

        mockCustomerTokenValid();
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        ResponseEntity<?> response = controller.placeOrder("Bearer token", request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(saved, response.getBody());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order persisted = captor.getValue();
        assertEquals("Anu", persisted.getCustomerName());
        assertEquals("PENDING", persisted.getStatus());
        assertEquals(6.5, persisted.getTotalAmount());
    }

    @Test
    void myOrdersReturnsUnauthorizedWhenTokenInvalid() {
        ResponseEntity<?> response = controller.myOrders("Bearer bad", "anu@mail.com");

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void myOrdersReturnsCustomerOrdersWhenTokenValid() {
        Order order = new Order();
        order.setCustomerContact("anu@mail.com");

        mockCustomerTokenValid();
        when(orderRepository.findByCustomerContactIgnoreCaseOrderByCreatedAtDesc("anu@mail.com"))
                .thenReturn(List.of(order));

        ResponseEntity<?> response = controller.myOrders("Bearer token", "anu@mail.com");

        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        List<Order> body = (List<Order>) response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void allOrdersReturnsAllRecords() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order(), new Order()));

        ResponseEntity<List<Order>> response = controller.allOrders();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void updateStatusReturnsUpdatedOrderWhenFound() {
        Order existing = new Order();
        existing.setStatus("PENDING");
        when(orderRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);

        ResponseEntity<?> response = controller.updateStatus(2L, Map.of("status", "READY"));

        assertEquals(200, response.getStatusCode().value());
        Order body = (Order) response.getBody();
        assertNotNull(body);
        assertEquals("READY", body.getStatus());
    }

    @Test
    void updateStatusReturnsNotFoundWhenOrderMissing() {
        when(orderRepository.findById(9L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.updateStatus(9L, Map.of("status", "READY"));

        assertEquals(404, response.getStatusCode().value());
        assertTrue(response.getBody() == null);
    }

    private void mockCustomerTokenValid() {
        when(jwtUtil.extractUsername("token")).thenReturn("customer_anu@mail.com");
        when(jwtUtil.validateToken("token")).thenReturn(true);
    }
}

