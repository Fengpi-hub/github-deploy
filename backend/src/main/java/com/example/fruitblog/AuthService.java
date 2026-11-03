package com.example.fruitblog;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 登录注册业务逻辑：
 * - 注册：校验用户名/密码，查重，BCrypt 加密后入库
 * - 登录：用户名密码比对，成功则签发 JWT
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        String username = req.getUsername() == null ? null : req.getUsername().trim();
        String password = req.getPassword();

        if (username == null || username.length() < 3) {
            throw new IllegalArgumentException("用户名至少 3 个字符");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("密码至少 6 个字符");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已被占用");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername());
    }

    public AuthResponse login(LoginRequest req) {
        Optional<User> found = userRepository.findByUsername(req.getUsername());
        User user = found.orElse(null);
        // 用户不存在或密码不匹配都返回统一错误，避免泄露用户名是否存在
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername());
    }
}
