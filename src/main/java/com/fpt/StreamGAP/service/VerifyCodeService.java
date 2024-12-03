package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.entity.VerifyCode;
import com.fpt.StreamGAP.repository.VerifyCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VerifyCodeService {

    @Autowired
    private VerifyCodeRepository verifyCodeRepository;

    // Save or create a new VerifyCode
    public VerifyCode save(VerifyCode verifyCode) {
        return verifyCodeRepository.save(verifyCode);
    }

    // Find a VerifyCode by ID
    public Optional<VerifyCode> findById(int id) {
        return verifyCodeRepository.findById(id);
    }

    // Find a VerifyCode by User
    public VerifyCode findByUser(User user) {
        return verifyCodeRepository.findByUser(user);
    }

    // Update an existing VerifyCode
    public VerifyCode update(int id, VerifyCode newVerifyCodeData) {
        return verifyCodeRepository.findById(id)
                .map(existingVerifyCode -> {
                    existingVerifyCode.setCode(newVerifyCodeData.getCode());
                    existingVerifyCode.setUser(newVerifyCodeData.getUser());
                    existingVerifyCode.setCodeType(newVerifyCodeData.getCodeType());
                    existingVerifyCode.setExpireAt(newVerifyCodeData.getExpireAt());
                    return verifyCodeRepository.save(existingVerifyCode);
                }).orElseThrow(() -> new RuntimeException("VerifyCode not found with id: " + id));
    }

    // Delete a VerifyCode by ID
    public void deleteById(int id) {
        verifyCodeRepository.deleteById(id);
    }

    public VerifyCode findByCode(String code){
        return verifyCodeRepository.findByCode(code);
    }

    public void deleteByCode(String code){
        VerifyCode findByCode = findByCode(code);
        if(findByCode != null){
            deleteById(findByCode.getId());
            System.out.println("Da xoa thanh cong code");
        }
    }

    // Check and delete expired codes
    public void deleteExpiredCodes() {
        List<VerifyCode> expiredCodes = verifyCodeRepository.findByExpireAtBefore(LocalDateTime.now());
        for (VerifyCode verifyCode : expiredCodes) {
            verifyCodeRepository.delete(verifyCode);
        }
    }

    // Delete expired codes on a schedule (e.g., every night)
    public void deleteExpiredCodesScheduled() {
        verifyCodeRepository.deleteByExpireAtBefore(LocalDateTime.now());
    }
}
