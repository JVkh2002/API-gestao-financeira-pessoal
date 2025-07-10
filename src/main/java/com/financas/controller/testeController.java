package com.financas.controller;

import com.financas.dto.AuthResponseDTO;
import com.financas.dto.LoginDTO;
import com.financas.dto.RegistroDTO;
import com.financas.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
public class testeController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}