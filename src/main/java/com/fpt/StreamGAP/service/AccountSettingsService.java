package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.entity.AccountSettings;
import com.fpt.StreamGAP.repository.AccountSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountSettingsService {

    @Autowired
    private AccountSettingsRepository accountSettingsRepository;


    public List<AccountSettings> getAllAccountSettings() {
        return accountSettingsRepository.findAll();
    }


    public Optional<AccountSettings> getAccountSettingsByUserId(Integer account_settings_id) {
        return accountSettingsRepository.findById(account_settings_id);
    }


    public AccountSettings createAccountSettings(AccountSettings accountSettings) {
        return accountSettingsRepository.save(accountSettings);
    }


    public Optional<AccountSettings> updateAccountSettings(Integer account_settings_id, AccountSettings accountSettings) {
        return accountSettingsRepository.findById(account_settings_id).map(existingSettings -> {
            existingSettings.setPrivacy(accountSettings.getPrivacy());
            existingSettings.setEmail_notifications(accountSettings.getEmail_notifications());
            existingSettings.setVolume_level(accountSettings.getVolume_level());
            existingSettings.setSleep_timer(accountSettings.getSleep_timer());
            return accountSettingsRepository.save(existingSettings);
        });
    }

    public void deleteAccountSettings(Integer account_settings_id) {
        accountSettingsRepository.deleteById(account_settings_id);
    }
}