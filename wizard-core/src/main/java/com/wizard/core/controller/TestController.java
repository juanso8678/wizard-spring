package com.wizard.core.controller;

import com.wizard.core.dto.auth.JwtResponse;
import com.wizard.core.entity.Organization;
import com.wizard.core.entity.User;
import com.wizard.core.repository.OrganizationRepository;
import com.wizard.core.repository.UserRepository;
import com.wizard.core.security.CustomUserDetailsService;
import com.wizard.core.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/db")
    public ResponseEntity<String> testDb() {
        try {
            long count = organizationRepository.count();
            return ResponseEntity.ok("✅ Database connection OK. Organizations count: " + count);
        } catch (Exception e) {
            return ResponseEntity.ok("❌ Database error: " + e.getMessage());
        }
    }

    @GetMapping("/organizations-raw")
    public ResponseEntity<List<Organization>> getOrganizationsRaw() {
        try {
            List<Organization> orgs = organizationRepository.findAll();
            return ResponseEntity.ok(orgs);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/create-user-simple")
    public ResponseEntity<String> createUserSimple(@RequestBody Map<String, String> request) {
        try {
            User user = User.builder()
                    .email(request.get("email"))
                    .username(request.get("username"))
                    .password(passwordEncoder.encode(request.get("password")))
                    .name(request.get("name"))
                    .role("USER")
                    .activo(true)
                    .build();
            
            User saved = userRepository.save(user);
            return ResponseEntity.ok("✅ User created with ID: " + saved.getId());
        } catch (Exception e) {
            return ResponseEntity.ok("❌ Error creating user: " + e.getMessage());
        }
    }

    @GetMapping("/users-raw")
    public ResponseEntity<List<User>> getUsersRaw() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/auth-simple")
    public ResponseEntity<String> testAuthSimple(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("usernameOrEmail");
            String password = request.get("password");
            
            System.out.println("Testing auth for: " + username);
            
            // Test 1: Verificar si existe el usuario
            User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
            if (user == null) {
                return ResponseEntity.ok("❌ User not found: " + username);
            }
            
            System.out.println("✅ User found: " + user.getUsername());
            
            // Test 2: Verificar contraseña
            boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
            System.out.println("Password match: " + passwordMatch);
            
            return ResponseEntity.ok("✅ User exists, password match: " + passwordMatch);
            
        } catch (Exception e) {
            return ResponseEntity.ok("❌ Error: " + e.getMessage());
        }
    }

    @PostMapping("/test-jwt")
    public ResponseEntity<String> testJwt(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            System.out.println("🔍 Testing JWT generation for: " + username);
            
            // Test solo la generación de JWT
            String token = jwtService.generateToken(Map.of(), username);
            
            return ResponseEntity.ok("✅ JWT generated successfully: " + token.substring(0, Math.min(50, token.length())) + "...");
            
        } catch (Exception e) {
            System.out.println("❌ JWT Test failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok("❌ JWT Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    @PostMapping("/test-auth-flow")
    public ResponseEntity<String> testAuthFlow(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("usernameOrEmail");
            String password = request.get("password");
            
            System.out.println("🔍 [TEST] Step 1: Testing authentication...");
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            System.out.println("✅ [TEST] Step 1: Authentication OK");
            
            System.out.println("🔍 [TEST] Step 2: Loading user details...");
            UserDetails user = userDetailsService.loadUserByUsername(username);
            System.out.println("✅ [TEST] Step 2: User loaded: " + user.getUsername());
            
            System.out.println("🔍 [TEST] Step 3: Generating token...");
            String token = jwtService.generateToken(Map.of(), user.getUsername());
            System.out.println("✅ [TEST] Step 3: Token generated");
            
            System.out.println("🔍 [TEST] Step 4: Creating response object...");
            JwtResponse response = new JwtResponse(token);
            System.out.println("✅ [TEST] Step 4: Response created");
            
            System.out.println("🔍 [TEST] Step 5: Testing JSON serialization...");
            String jsonTest = "{\"token\":\"" + token.substring(0, 20) + "...\"}";
            System.out.println("✅ [TEST] Step 5: JSON would be: " + jsonTest);
            
            return ResponseEntity.ok("✅ Full auth flow completed successfully!");
            
        } catch (Exception e) {
            System.out.println("❌ [TEST] Auth flow failed: " + e.getClass().getSimpleName());
            System.out.println("❌ [TEST] Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok("❌ Auth flow error: " + e.getMessage());
        }
    }

    @PostMapping("/test-userdetails")
    public ResponseEntity<String> testUserDetails(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("usernameOrEmail");
            
            System.out.println("🔍 Testing UserDetailsService for: " + username);
            
            UserDetails user = userDetailsService.loadUserByUsername(username);
            System.out.println("✅ UserDetails loaded: " + user.getUsername());
            System.out.println("✅ Authorities: " + user.getAuthorities());
            
            return ResponseEntity.ok("✅ UserDetailsService works fine!");
            
        } catch (Exception e) {
            System.out.println("❌ UserDetailsService failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok("❌ UserDetailsService error: " + e.getMessage());
        }
    }


    @PostMapping("/test-auth-manager")
public ResponseEntity<String> testAuthManager(@RequestBody Map<String, String> request) {
    try {
        String username = request.get("usernameOrEmail");
        String password = request.get("password");
        
        System.out.println("🔍 Testing AuthenticationManager...");
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        
        System.out.println("✅ AuthenticationManager works!");
        return ResponseEntity.ok("✅ AuthenticationManager OK!");
        
    } catch (Exception e) {
        System.out.println("❌ AuthenticationManager failed: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.ok("❌ AuthManager error: " + e.getMessage());
    }
}
}