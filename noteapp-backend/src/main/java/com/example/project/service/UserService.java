package com.example.project.service;

import com.example.project.dto.form.UserLoginForm;
import com.example.project.dto.vo.UserLoginVO;

public interface UserService {
    UserLoginVO login(UserLoginForm form);
}
