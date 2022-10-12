package com.test.SpringReactAllinOne.controller;

import com.test.SpringReactAllinOne.domain.User;
import com.test.SpringReactAllinOne.dto.JwtAuthenticationResponse;
import com.test.SpringReactAllinOne.dto.LoginRequestDto;
import com.test.SpringReactAllinOne.dto.LoginResponseDto;
import com.test.SpringReactAllinOne.dto.SignUpRequestDto;
import com.test.SpringReactAllinOne.repository.UserRepository;
import com.test.SpringReactAllinOne.rvo.RVO;
import com.test.SpringReactAllinOne.security.JwtTokenProvider;
import com.test.SpringReactAllinOne.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public RVO<LoginResponseDto> userLogin(@RequestBody LoginRequestDto loginRequestDto){
        return RVO.<LoginResponseDto>builder()
                .message("로그인 되었습니다.")
                .checkCode("체크 코드 생성")
                .data(userService.getUserIdAndPassword(loginRequestDto))
                .build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsernameOrEmail(),
                        loginRequestDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequestDto signUpRequestDto) {
        String password = passwordEncoder.encode(signUpRequestDto.getPassword());

        User user = new User(signUpRequestDto.getUsername(), password);

        userRepository.save(user);

        return ResponseEntity.ok("create success!");

    }
}
