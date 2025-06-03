package com.wizard.core.service.interfaces;

import com.wizard.core.dto.auth.LoginRequest;
import com.wizard.core.dto.auth.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
}
