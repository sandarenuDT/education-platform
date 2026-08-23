package com.lms.backend.service;

import com.lms.backend.dto.request.CreateOrderRequest;
import com.lms.backend.dto.response.OrderResponse;
import com.lms.backend.exception.ResourceNotFoundException;
import com.lms.backend.mapper.OrderMapper;
import com.lms.backend.model.Book;
import com.lms.backend.model.Order;
import com.lms.backend.model.Recording;
import com.lms.backend.model.Teacher;
import com.lms.backend.model.User;
import com.lms.backend.model.enums.ItemType;
import com.lms.backend.model.enums.OrderStatus;
import com.lms.backend.repository.BookRepository;
import com.lms.backend.repository.OrderRepository;
import com.lms.backend.repository.RecordingRepository;
import com.lms.backend.repository.TeacherRepository;
import com.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final RecordingRepository recordingRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final AccessControlService accessControlService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        BookRepository bookRepository,
                        RecordingRepository recordingRepository,
                        UserRepository userRepository,
                        TeacherRepository teacherRepository,
                        AccessControlService accessControlService) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.recordingRepository = recordingRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.accessControlService = accessControlService;
    }

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        // Resolve the item and its price/teacher server-side — NEVER trust a
        // price sent from the client, or a student could pay whatever they want.
        BigDecimal amount;
        Long teacherId;

        if (request.getItemType() == ItemType.BOOK) {
            Book book = bookRepository.findById(request.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
            amount = book.getPrice();
            teacherId = book.getTeacher().getId();
        } else {
            Recording recording = recordingRepository.findById(request.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recording not found"));
            amount = recording.getPrice();
            teacherId = recording.getTeacher().getId();
        }

        User userRef = userRepository.getReferenceById(userId);
        Teacher teacherRef = teacherRepository.getReferenceById(teacherId);

        Order order = new Order();
        order.setUser(userRef);
        order.setTeacher(teacherRef);
        order.setItemType(request.getItemType());
        order.setItemId(request.getItemId());
        order.setAmount(amount);

        // --- STAND-IN FOR REAL PAYMENT (Step 9) ---
        // No payment gateway integrated yet. Every order is marked PAID
        // immediately so we can test the full order -> access-grant chain.
        // When Step 9 happens, this becomes: status = PENDING, call
        // PaymentProvider.createCheckout(), then a webhook/callback marks it
        // PAID and calls accessControlService.grantAccess() from there instead.
        order.setStatus(OrderStatus.PAID);
        order.setPaymentProvider("MANUAL");

        order = orderRepository.save(order);

        accessControlService.grantAccess(userId, request.getItemType(), request.getItemId(), order.getId());

        return OrderMapper.toResponse(order);
    }
}