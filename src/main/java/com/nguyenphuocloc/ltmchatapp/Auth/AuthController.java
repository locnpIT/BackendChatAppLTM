package com.nguyenphuocloc.ltmchatapp.Auth;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;
import com.nguyenphuocloc.ltmchatapp.Security.CustomUserDetails;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	
	@Autowired AuthenticationManager authenticationManager;
	
	@Autowired TokenService tokenService;

	@Autowired UserRepository userRepository;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@PostMapping("/login")
	public ResponseEntity<?> getAccessToken(@RequestBody AuthRequest request){
		
		String username = request.getUsername();
		String password = request.getPassword();
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
			AuthResponse response = tokenService.generateTokens(userDetails.getUser());
			
			
			return ResponseEntity.ok(response);
		}
		catch(BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> createUser(@RequestBody User user){
		String hashPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(hashPassword);
		user.setIsValid(true);
		user.setRole("user");
		userRepository.save(user);
		return  ResponseEntity.status(HttpStatus.OK).body("Create User Success");
	}
	
	
	
	
}
