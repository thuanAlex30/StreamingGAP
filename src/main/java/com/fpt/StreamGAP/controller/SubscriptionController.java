package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.SubscriptionDTO;
import com.fpt.StreamGAP.entity.Subscription;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.service.SubscriptionService;
import com.fpt.StreamGAP.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserManagementService userManagementService;

    // API để lấy danh sách tất cả các gói đăng ký
    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        List<SubscriptionDTO> subscriptionDTOs = subscriptions.stream()
                .map(subscription -> {
                    SubscriptionDTO dto = new SubscriptionDTO();
                    dto.setId(subscription.getId());
                    dto.setName(subscription.getName());
                    dto.setPrice(subscription.getPrice());
                    dto.setDescription(subscription.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(subscriptionDTOs);
    }

    @PostMapping("/upgrade")
    public ResponseEntity<String> upgradeToPremium() {
        // Lấy user hiện tại từ SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userManagementService.getUserByUsername(userDetails.getUsername());

        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = optionalUser.get();

        // Kiểm tra nếu user đã có gói Premium thì không thể nâng cấp
        if (user.getSubscription() != null && user.getSubscription().getName().equals("Premium")) {
            return ResponseEntity.status(400).body("You are already on Premium subscription.");
        }

        // Kiểm tra nếu user đang có gói Base hoặc chưa có gói, thì mới cho phép nâng cấp lên Premium
        if (user.getSubscription() == null || user.getSubscription().getName().equals("Base")) {
            Subscription premiumSubscription = subscriptionService.getSubscriptionById(2); //Premium có id là 2
            user.setSubscription(premiumSubscription);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 10);  // Tgian
            user.setSubscriptionExpirationDate(calendar.getTime());

            userManagementService.saveUser(user);
            return ResponseEntity.ok("Your account has been upgraded to Premium. It will expire in 10 seconds.");
        }

        return ResponseEntity.status(400).body("Invalid subscription status.");
    }

    @GetMapping("/isPremium")
    public ResponseEntity<Boolean> isUserPremium() {
        // Lấy thông tin người dùng hiện tại
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userManagementService.getUserByUsername(userDetails.getUsername());

        // Kiểm tra người dùng có tồn tại không
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(404).body(false);
        }

        User user = optionalUser.get();

        // Kiểm tra người dùng có gói đăng ký Premium không và gói đăng ký có hết hạn không
        boolean isPremium = user.getSubscription() != null &&
                "Premium".equals(user.getSubscription().getName()) &&
                !subscriptionService.isPremiumExpired(user);

        return ResponseEntity.ok(isPremium);
    }

}
