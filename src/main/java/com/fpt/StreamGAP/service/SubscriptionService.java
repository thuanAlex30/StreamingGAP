package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.entity.Subscription;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    // Phương thức lấy tất cả các gói đăng ký
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll(); // Lấy tất cả các gói đăng ký từ cơ sở dữ liệu
    }

    public boolean isPremiumExpired(User user) {
        if (user.getSubscriptionExpirationDate() != null) {
            Date currentDate = new Date();
            return currentDate.after(user.getSubscriptionExpirationDate());
        }
        return false; // Nếu không hết hạn hoặc không có ngày hết hạn thì trả về false
    }


    public Subscription getSubscriptionById(Integer id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }
}
