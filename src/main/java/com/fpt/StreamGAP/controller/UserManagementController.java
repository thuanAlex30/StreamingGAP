package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.dto.UserDTO;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.UserRepo;
import com.fpt.StreamGAP.service.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
public class UserManagementController {
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody ReqRes userDto){
        String response = userManagementService.register(userDto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/auth/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String , String> request){
        String email = request.get("email");
        String code = request.get("code");
        String responseMessage =userManagementService.verifyEmail(email,code);
        return ResponseEntity.ok(responseMessage);
    }


    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
        return ResponseEntity.ok(userManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
        return ResponseEntity.ok(userManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }


    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUSerByID(@PathVariable Integer userId) {
        ReqRes response = new ReqRes();

        try {
            User user = userManagementService.getUsersByIdC(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            response.setUser(user);
            response.setStatusCode(200);
            response.setMessage("User found successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }


    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody User reqres) {
        return ResponseEntity.ok(userManagementService.updateUser(userId, reqres));
    }
    @GetMapping("/admin/user/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = userManagementService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUSer(@PathVariable Integer userId){
        return ResponseEntity.ok(userManagementService.deleteUser(userId));
    }

    @CrossOrigin(origins = "https://streaminggap-fontend.onrender.com")
    @PostMapping("/auth/login/google")
    public ReqRes loginWithGoogle(@RequestBody Map<String, String> requestBody, HttpServletResponse response, HttpServletRequest request) {
        String idToken = requestBody.get("idToken");

        if (idToken == null || idToken.isEmpty()) {
            ReqRes errorResponse = new ReqRes();
            errorResponse.setStatusCode(400);
            errorResponse.setMessage("Vui lòng cung cấp id token để tiếp tục.");
            return errorResponse;
        }

        try {
            // Đăng nhập qua Google
            ReqRes loginResponse = userManagementService.loginWithOAuth2(idToken, "google", request, response);
            return loginResponse;

        } catch (Exception e) {
            System.out.println("Lỗi khi đăng nhập với Google: " + e.getMessage());

            ReqRes errorResponse = new ReqRes();
            errorResponse.setStatusCode(500);
            errorResponse.setMessage("Đăng nhập thất bại. Vui lòng thử lại.");
            return errorResponse;
        }
    }

}
