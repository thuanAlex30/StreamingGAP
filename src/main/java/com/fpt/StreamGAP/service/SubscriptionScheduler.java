package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.entity.Subscription;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.SubscriptionRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final UserRepo userRepo;  // Dùng để lấy thông tin người dùng
    private final SubscriptionService subscriptionService;  // Để lấy thông tin các gói subscription

    // Phương thức kiểm tra và tự động chuyển về gói Base nếu gói Premium hết hạn
    @Scheduled(fixedRate = 60000) // ms
    public void checkAndDowngradeExpiredPremiumUsers() {
        List<User> users = userRepo.findAll();

        for (User user : users) {
            if (user.getSubscriptionExpirationDate() != null) {
                Date currentDate = new Date();

                // Kiểm tra nếu gói Premium của người dùng đã hết hạn
                if (currentDate.after(user.getSubscriptionExpirationDate())) {
                    // Nếu gói đã hết hạn, chuyển về gói Base
                    Subscription baseSubscription = subscriptionService.getSubscriptionById(1); //Base có id = 1
                    user.setSubscription(baseSubscription);
                    user.setSubscriptionExpirationDate(null); // Reset ngày hết hạn

                    userRepo.save(user);
                    System.out.println("User " + user.getUsername() + " has been downgraded to Base subscription.");
                }
            }
        }
    }
}
