package com.fpt.StreamGAP.service;


import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.Enum.CodeTypeEnum;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.entity.VerifyCode;
import com.fpt.StreamGAP.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserManagementService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerifyCodeService verifyCodeService;

    public String register(ReqRes registrationRequest, HttpSession session) {
        if (userRepo.existsByEmail(registrationRequest.getEmail())) {
            return "Email đã được sử dụng.";
        }

        try {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole(registrationRequest.getRole());
            user.setUsername(registrationRequest.getUsername());
            session.setAttribute("registeredUser", user);
            session.setAttribute("registrationTime", LocalDateTime.now().plusMinutes(5));

            String verificationCode = generateVerificationCode(CodeTypeEnum.REGISTER, null);
            emailService.sendVerificationEmail(user.getEmail(), "Xác nhận email", verificationCode);

            return "Vui lòng kiểm tra email để xác nhận.";
        } catch (Exception e) {
            return "Không thể đăng ký: " + e.getMessage();
        }
    }

    private String generateVerificationCode(CodeTypeEnum codeTypeEnum, User user) {
        String codeStr = null;
        int attempts = 0; // Thêm biến đếm số lần thử
        while (attempts < 5) { // Giới hạn số lần thử
            Random random = new Random();
            int code = 100000 + random.nextInt(900000);
            VerifyCode verifyCode = new VerifyCode();
            verifyCode.setCode(String.valueOf(code));
            verifyCode.setUser(user);
            verifyCode.setCodeType(codeTypeEnum);
            try {
                verifyCodeService.save(verifyCode);
                codeStr = String.valueOf(code);
                break;
            } catch (DataIntegrityViolationException e) {
                attempts++;
            }
        }
        if (codeStr == null) {
            throw new IllegalStateException("Không thể tạo mã xác thực. Vui lòng thử lại.");
        }
        return codeStr;
    }
    private boolean isCodeValid(String code, CodeTypeEnum type) {
        VerifyCode verifyCode = verifyCodeService.findByCode(code);
        if (verifyCode == null || verifyCode.getCodeType() != type) {
            return false;
        }
        if (LocalDateTime.now().isAfter(verifyCode.getExpireAt())) {
            verifyCodeService.deleteByCode(code); // Xóa mã hết hạn
            return false;
        }
        return true;
    }


    public String verifyEmail(String email, String code, HttpSession session) {
        User registeredUser = (User) session.getAttribute("registeredUser");
        LocalDateTime registrationTime = (LocalDateTime) session.getAttribute("registrationTime");

        VerifyCode findedCode = verifyCodeService.findByCode(code);

        if (findedCode == null) {
            return "Mã xác nhận không hợp lệ.";
        }

        if (registeredUser == null || LocalDateTime.now().isAfter(registrationTime) || LocalDateTime.now().isAfter(findedCode.getExpireAt())) {
            verifyCodeService.deleteByCode(code);
            return "Phiên đăng ký đã hết hạn.";
        }

        if (registeredUser.getEmail().equals(email)) {
            registeredUser.setRole("USER");
            userRepo.save(registeredUser);
            verifyCodeService.deleteByCode(code);
            return "Đăng ký thành công.";
        }

        return "Xác nhận không thành công.";
    }

    public ReqRes  login(ReqRes loginRequest){
        ReqRes response = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
            var user =userRepo.findByUsername(loginRequest.getUsername()).orElseThrow();
            var jwt=jwtUtils.generateRefreshToken(new HashMap<>(),user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            User user=userRepo.findByUsername(ourEmail).orElseThrow();
            if(jwtUtils.isTokenValid(refreshTokenReqiest.getToken(),user)){
                var jwt=jwtUtils.generateToken(user);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refresh Token");
            }
            response.setStatusCode(200);
            return response;
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();
        try{
            List<User> result=userRepo.findAll();
            if(!result.isEmpty()){
                reqRes.setUserList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successfull");
            }else{
                reqRes.setStatusCode(404);
                reqRes.setMessage("No user Found");
            }
            return reqRes;
        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " +e.getMessage());
            return reqRes;
        }
    }

    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                userRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }
    public ReqRes updateUser(Integer userId, User updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setUsername(updatedUser.getUsername());
                existingUser.setAvatar_url(updatedUser.getAvatar_url());
                existingUser.setRole(updatedUser.getRole());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                User savedUser = userRepo.save(existingUser);
                reqRes.setUser(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }
    public ReqRes getMyInfo(String username){
        ReqRes reqRes = new ReqRes();
        try {
            Optional<User> userOptional = userRepo.findByUsername(username);
            if (userOptional.isPresent()) {
                reqRes.setUser(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }
    public Optional<Integer> getUserIdByUsername(String username) {
        return userRepo.findByUsername(username)
                .map(User::getUser_id);
    }

    public Optional<User> getUsersByIdC(Integer id) {
        return userRepo.findById(id);
    }
    public Optional<Integer> getUsersById(Integer id) {
        return userRepo.findById(id).map(User::getUser_id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    public User findByUsernameOrThrow(String username) {
        // Tìm kiếm user bằng username
        Optional<User> user = userRepo.findByUsername(username);

        // Kiểm tra nếu user tồn tại, nếu không thì ném ra một ngoại lệ
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    //forgot password
    public String initiatePasswordReset(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "Email không tồn tại.";
        }

        String resetCode = generateVerificationCode(CodeTypeEnum.FORGET_PASS, userOptional.get());
        emailService.sendVerificationEmail(email, "Đặt lại mật khẩu", resetCode);

        return "Vui lòng kiểm tra email để lấy mã xác nhận.";
    }
    //reset password
    public String resetPassword(String code, String newPassword) {
        if (!isCodeValid(code, CodeTypeEnum.FORGET_PASS)) {
            throw new IllegalArgumentException("Mã không hợp lệ hoặc đã hết hạn.");
        }

        User user = verifyCodeService.findByCode(code).getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        verifyCodeService.deleteByCode(code);
        return "Mật khẩu đã được đặt lại thành công.";
    }


    public User saveUser(User user) {
        User save = userRepo.save(user);
        return save;
    }
}