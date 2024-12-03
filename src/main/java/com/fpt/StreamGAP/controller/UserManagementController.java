package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.dto.UserDTO;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.UserRepo;
import com.fpt.StreamGAP.service.UserManagementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<String> registerUser(@RequestBody ReqRes userDto, HttpSession session) {
        String response = userManagementService.register(userDto, session);

        // Kiểm tra nếu response chứa thông báo lỗi
        if (response.equals("Email đã được sử dụng.")) {
            // Trả về mã lỗi 400 và thông báo lỗi nếu email đã được sử dụng
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Trả về thông báo thành công nếu đăng ký thành công
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String , String> request,  HttpSession session){
        String email = request.get("email");
        String code = request.get("code");
        String responseMessage =userManagementService.verifyEmail(email,code, session);
        // Kiểm tra kết quả xác minh và trả về mã trạng thái 200
        if (responseMessage.equals("Đăng ký thành công.")) {
            return ResponseEntity.ok("200");
        }
        // Trường hợp xác minh thất bại, trả về mã lỗi (ví dụ: 400)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed. Please check the code and try again.");
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
    // @GetMapping("/admin/user/get-profile")
    // public ResponseEntity<ReqRes> getMyProfile(){
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String email = authentication.getName();
    //     ReqRes response = userManagementService.getMyInfo(email);
    //     return  ResponseEntity.status(response.getStatusCode()).body(response);
    // }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUSer(@PathVariable Integer userId){
        return ResponseEntity.ok(userManagementService.deleteUser(userId));
    }
    // forgot password
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String response = userManagementService.initiatePasswordReset(email);
        return ResponseEntity.ok(response);
    }
    // reset password
    @PostMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        try {
            String response = userManagementService.resetPassword(code, newPassword);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
@PutMapping("user/updateProfile")
    public ResponseEntity<ReqRes> updateProfile(@AuthenticationPrincipal User currentUser, @RequestBody User reqres) {
      ReqRes response =userManagementService.updateProfile(currentUser.getUsername(), reqres);
      return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/user/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = userManagementService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }
      
}
