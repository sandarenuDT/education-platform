package com.lms.backend.controller.student;

import com.lms.backend.dto.request.CreateOrderRequest;
import com.lms.backend.dto.response.OrderResponse;
import com.lms.backend.security.CustomUserDetails;
import com.lms.backend.service.OrderService;
import com.lms.backend.util.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Any logged-in user can hit this — no @PreAuthorize role restriction, since
// nothing stops a TEACHER_ADMIN from also buying another teacher's content
// as a student. What matters is WHO is buying, resolved from the token.
@RestController
@RequestMapping(ApiPaths.STUDENT_ORDERS)
public class StudentOrderController {

    private final OrderService orderService;

    @Autowired
    public StudentOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}