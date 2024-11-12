package com.fpt.StreamGAP.service;


import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    private Map<String, User> pendingRegistrations = new HashMap<>();

    public String register(ReqRes registrationRequest) {
        try {
            if(userRepo.existsByEmail(registrationRequest.getEmail())){
                return "mail da su dung";
            }

            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole(registrationRequest.getRole());
            user.setAvatar_url(registrationRequest.getAvatar_url());
            user.setUsername(registrationRequest.getUsername());
            user.setProvider_id(registrationRequest.getProvider_id());
            user.setLogin_provider(registrationRequest.getLogin_provider());
            user.setSubscription_type(registrationRequest.getSubscription_type());
            user.setCreated_at(registrationRequest.getCreated_at());
            user.setUpdated_at(registrationRequest.getUpdated_at());

            String verificationCode = generateVerificationCode();
            pendingRegistrations.put(verificationCode, user);
            emailService.sendVerificationEmail(user.getEmail(), "Email Verification", verificationCode);

            return "check code";
        } catch (Exception e) {
            return "khong dang ki duoc: " + e.getMessage();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public String verifyEmail(String email, String code) {
        User user = pendingRegistrations.get(code);
        if (user != null && user.getEmail().equals(email)) {
            User savedUser = userRepo.save(user);
            pendingRegistrations.remove(code);
            return "ddang ki thanh cong";
        } else {
            return "khong dang ki thanh cong";
        }
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

}