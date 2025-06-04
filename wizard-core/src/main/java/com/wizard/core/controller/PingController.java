
package com.wizard.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    
    @GetMapping("/ping")
    public String ping() {
        return "✅ wizard-core is running";
    }
}