package com.restaurant.controller;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.repository.OrderRepository;
import com.restaurant.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private JwtUtil jwtUtil;

    // Place an order — validates customer JWT manually
    @PostMapping
    public ResponseEntity<?> placeOrder(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody OrderRequest request) {

        if (!isValidCustomerToken(authHeader)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized. Please log in first."));
        }

        Order order = new Order();
        order.setCustomerName(request.customerName());
        order.setCustomerContact(request.customerContact());
        order.setItems(request.items());
        order.setTotalAmount(request.totalAmount());
        order.setNotes(request.notes());
        order.setStatus("PENDING");

        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(saved);
    }

    // Get orders for the logged-in customer
    @GetMapping("/my")
    public ResponseEntity<?> myOrders(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam String contact) {

        if (!isValidCustomerToken(authHeader)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(
            orderRepository.findByCustomerContactIgnoreCaseOrderByCreatedAtDesc(contact)
        );
    }

    // Admin: get all orders
    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> allOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    // Admin: update order status
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(body.get("status"));
            return ResponseEntity.ok(orderRepository.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }

    private boolean isValidCustomerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            return jwtUtil.validateToken(token) && username.startsWith("customer_");
        } catch (Exception e) {
            return false;
        }
    }

    record OrderRequest(
        String customerName,
        String customerContact,
        List<OrderItem> items,
        Double totalAmount,
        String notes
    ) {}
}
