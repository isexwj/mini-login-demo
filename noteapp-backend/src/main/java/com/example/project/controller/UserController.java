package com.example.project.controller;

import com.example.project.common.Result;
import com.example.project.dto.form.UserLoginForm;
import com.example.project.dto.vo.UserLoginVO;
import com.example.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /** 用户登录 */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginForm form) {
        return Result.ok(userService.login(form));
    }

}