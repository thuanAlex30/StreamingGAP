package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.PaymentDTO;
import com.fpt.StreamGAP.entity.Album;
import com.fpt.StreamGAP.entity.Subscription;
import com.fpt.StreamGAP.repository.ArtistRepository;
import com.fpt.StreamGAP.service.AlbumService;
import com.fpt.StreamGAP.service.PaymentService;
import com.fpt.StreamGAP.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {
    @Autowired
    private PaymentService vnPayService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private SubscriptionService subscriptionService;

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }
    @PostMapping("/submitOrder/album")
    public ResponseEntity<String> submitOrderBuyAlbum(@RequestParam Integer albumId, HttpServletRequest request) {
        Album album = albumService.getById(albumId);
        System.out.println(album);
        if(album == null){
            return ResponseEntity.status(204).body("Not found Album");
        }

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(
                album.getPrice(),
                album.getTitle(),
                baseUrl
        );
        return ResponseEntity.ok(vnpayUrl);
    }

    @PostMapping("/submitOrder/subscription")
    public ResponseEntity<String> submitOrderSubscription(@RequestParam Integer subscriptionId, HttpServletRequest request) {
        Subscription subscription = subscriptionService.getSubscriptionById(subscriptionId);
        if(subscription == null){
            return ResponseEntity.status(204).body("Not found Subscription");
        }

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(
                subscription.getPrice(),
                subscription.getName(),
                baseUrl
        );
        return ResponseEntity.ok(vnpayUrl);
    }

    @GetMapping("/vnpay-payment")
    public ResponseEntity<?> handlePayment(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);

        PaymentDTO paymentDTO = new PaymentDTO(
                request.getParameter("vnp_OrderInfo"),
                request.getParameter("vnp_Amount"),
                request.getParameter("vnp_PayDate"),
                request.getParameter("vnp_TransactionNo")
        );

        if (paymentStatus == 1) {
            return ResponseEntity.ok(paymentDTO);
        } else {
            return ResponseEntity.status(400).body("Payment failed");
        }
    }
}