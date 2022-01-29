package uz.pdp.appjwtrealemailauditing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtrealemailauditing.entity.User;
import uz.pdp.appjwtrealemailauditing.entity.enums.RoleName;
import uz.pdp.appjwtrealemailauditing.payload.ApiResponse;
import uz.pdp.appjwtrealemailauditing.payload.LoginDto;
import uz.pdp.appjwtrealemailauditing.payload.RegisterDto;
import uz.pdp.appjwtrealemailauditing.repository.RoleRepository;
import uz.pdp.appjwtrealemailauditing.repository.UserRepository;
import uz.pdp.appjwtrealemailauditing.security.JwtProvider;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;

    public ApiResponse registerUser(RegisterDto dto) {
        boolean byEmail = userRepository.existsByEmail(dto.getEmail());
        if (byEmail) {
            return new ApiResponse("Bunday email foydalanuvchisi allaqachon mavjud", false);
        }
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.ROLE_USER)));
        user.setEmailCode(UUID.randomUUID().toString());
        userRepository.save(user);

        // EMAILGA YUBORISH METODINI CHAQIRYAPMIZ
        sendEmail(user.getEmail(), user.getEmailCode());
        return new ApiResponse("Muvaffaqqiyatli ro'yxatdan o'tdingiz, Email akkauntingiz aktivlashtirilishi uchun emailingizni tasdiqlang", true);
    }

    public Boolean sendEmail(String sendingEmail, String emailCode) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("Test@pdp.com");
            simpleMailMessage.setTo(sendingEmail);
            simpleMailMessage.setSubject("Accountni tasdiqlash");
            simpleMailMessage.setText("<a href='http://localhost:8080/api/auth/verifyEmail?emailCode="+emailCode+"&email="+sendingEmail+"'>Tasdiqlang</a>");
            javaMailSender.send(simpleMailMessage);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public ApiResponse verifyEmail(String emailCode, String email) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setEmailCode(null);
            userRepository.save(user);
            return new ApiResponse("Akkaunt tasdiqlandi", true);
        }
        return new ApiResponse("Akkaunt allaqachon tasdiqlangan", false);
    }

    public ApiResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(), loginDto.getPassword()
            ));
            User user = (User) authentication.getPrincipal();
            String token = jwtProvider.generateToken(loginDto.getUsername(), user.getRoles());
            return new ApiResponse("Token", true, token);
        }catch (BadCredentialsException badCredentialsException){
            return new ApiResponse("parol yoki login xato", false);
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> byEmail = userRepository.findByEmail(username);
//        if (byEmail.isPresent())
//            return byEmail.get();
//        throw new UsernameNotFoundException(username + " topilmadi");
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + " topilmadi"));
    }
}