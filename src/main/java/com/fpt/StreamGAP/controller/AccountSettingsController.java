package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.AccountSettingsDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.AccountSettings;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.AccountSettingsRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import com.fpt.StreamGAP.service.AccountSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account-settings")
public class AccountSettingsController {

    @Autowired
    private AccountSettingsService accountSettingsService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AccountSettingsRepository accountSettingsRepository;

    // Phương thức để lấy tên người dùng hiện tại
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }


    @GetMapping
    public ReqRes getAllAccountSettings() {
        String currentUsername = getCurrentUsername();
        List<AccountSettings> accountSettingsList = accountSettingsService.getAllAccountSettings();

        List<AccountSettingsDTO> accountSettingsDTOs = accountSettingsList.stream()
                .filter(accountSettings -> accountSettings.getUser().getUsername().equals(currentUsername))
                .map(accountSettings -> {
                    AccountSettingsDTO dto = new AccountSettingsDTO();
                    dto.setAccount_settings_id(accountSettings.getAccount_settings_id());
                    dto.setUser_id(accountSettings.getUser().getUser_id());
                    dto.setPrivacy(accountSettings.getPrivacy());
                    dto.setEmail_notifications(accountSettings.getEmail_notifications());
                    dto.setVolume_level(accountSettings.getVolume_level());
                    dto.setSleep_timer(accountSettings.getSleep_timer());
                    return dto;
                })
                .collect(Collectors.toList());

        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Account settings retrieved successfully");
        response.setAccountSettingsList(accountSettingsDTOs);
        return response;
    }


    @PostMapping
    @Transactional
    public ResponseEntity<ReqRes> createAccountSettings(@RequestBody AccountSettings accountSettings) {
        String currentUsername = getCurrentUsername(); // Lấy tên người dùng hiện tại

        if (currentUsername == null) {
            ReqRes response = new ReqRes();
            response.setStatusCode(401);
            response.setMessage("User not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        // Lấy user từ userRepo bằng tên người dùng hiện tại
        Optional<User> userOptional = userRepo.findByUsername(currentUsername);
        if (userOptional.isEmpty()) {
            ReqRes response = new ReqRes();
            response.setStatusCode(404);
            response.setMessage("User not found");
            return ResponseEntity.status(404).body(response);
        }

        User user = userOptional.get();
        accountSettings.setUser(user);

        Optional<AccountSettings> existingSettings = accountSettingsService.getAccountSettingsByUserId(user.getUser_id());
        if (existingSettings.isPresent()) {
            ReqRes response = new ReqRes();
            response.setStatusCode(409);
            response.setMessage("Account settings already exist for this user");
            return ResponseEntity.status(409).body(response);
        }

        AccountSettings savedAccountSettings = accountSettingsService.createAccountSettings(accountSettings);

        AccountSettingsDTO dto = new AccountSettingsDTO();
        dto.setAccount_settings_id(savedAccountSettings.getAccount_settings_id());
        dto.setUser_id(savedAccountSettings.getUser().getUser_id());
        dto.setPrivacy(savedAccountSettings.getPrivacy());
        dto.setEmail_notifications(savedAccountSettings.getEmail_notifications());
        dto.setVolume_level(savedAccountSettings.getVolume_level());
        dto.setSleep_timer(savedAccountSettings.getSleep_timer());

        ReqRes response = new ReqRes();
        response.setStatusCode(201);
        response.setMessage("Account settings created successfully");
        response.setAccountSettingsList(List.of(dto));
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<ReqRes> updateAccountSettings(@RequestBody AccountSettings accountSettings) {
        String currentUsername = getCurrentUsername();
        Optional<AccountSettings> existingSettings = accountSettingsService.getAllAccountSettings().stream()
                .filter(settings -> settings.getUser().getUsername().equals(currentUsername))
                .findFirst();

        if (existingSettings.isPresent()) {
            Integer accountSettingsId = existingSettings.get().getAccount_settings_id();
            return accountSettingsService.updateAccountSettings(accountSettingsId, accountSettings)
                    .map(updatedSettings -> {
                        AccountSettingsDTO dto = new AccountSettingsDTO();
                        dto.setAccount_settings_id(updatedSettings.getAccount_settings_id());
                        dto.setUser_id(updatedSettings.getUser().getUser_id());
                        dto.setPrivacy(updatedSettings.getPrivacy());
                        dto.setEmail_notifications(updatedSettings.getEmail_notifications());
                        dto.setVolume_level(updatedSettings.getVolume_level());
                        dto.setSleep_timer(updatedSettings.getSleep_timer());

                        ReqRes response = new ReqRes();
                        response.setStatusCode(200);
                        response.setMessage("Account settings updated successfully");
                        response.setAccountSettingsList(List.of(dto));
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        ReqRes response = new ReqRes();
                        response.setStatusCode(404);
                        response.setMessage("Account settings not found or you are not authorized to update these settings");
                        return ResponseEntity.status(404).body(response);
                    });
        } else {
            ReqRes response = new ReqRes();
            response.setStatusCode(404);
            response.setMessage("Account settings not found or you are not authorized to update these settings");
            return ResponseEntity.status(404).body(response);
        }
    }



    @DeleteMapping
    @Transactional
    public ResponseEntity<ReqRes> deleteAccountSettings() {
        String currentUsername = getCurrentUsername();
        Optional<AccountSettings> existingSettings = accountSettingsService.getAllAccountSettings().stream()
                .filter(settings -> settings.getUser().getUsername().equals(currentUsername))
                .findFirst();

        if (existingSettings.isPresent()) {
            accountSettingsService.deleteAccountSettings(existingSettings.get().getAccount_settings_id());
            ReqRes response = new ReqRes();
            response.setStatusCode(200);
            response.setMessage("Account settings deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            ReqRes response = new ReqRes();
            response.setStatusCode(404);
            response.setMessage("Account settings not found or you are not authorized to delete these settings");
            return ResponseEntity.status(404).body(response);
        }
    }


}
