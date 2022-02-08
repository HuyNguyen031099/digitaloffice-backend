package com.datn.doffice.config.security;

import com.datn.doffice.controller.ApiController;
import com.datn.doffice.dao.UserCollection;
import com.datn.doffice.dto.UserLoginDetailDTO;
import com.datn.doffice.entity.UserEntity;
import com.datn.doffice.enums.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@CrossOrigin
public class JwtAuthenticationController extends ApiController {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private UserCollection userCollection;

    @RequestMapping(value = "/validate-token", method = RequestMethod.GET)
    public ResponseEntity<?> validateToken(HttpServletRequest request) throws Exception {
        String curToken = getCurrentToken(request);
        String userName;
        UserEntity userEntity;
        try {
            userName = jwtUtils.getUsernameFromToken(curToken);
            userEntity = userCollection.findByUsername(userName);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ok(userEntity);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest, HttpServletRequest request) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final MyUserPrincipal userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtUtils.generateToken(userDetails);

        JwtResponse jwtResponse = JwtResponse.builder()
                .userEntity(userDetails.getUser())
                .jwtToken(token)
                .build();

        return ResponseEntity.ok(jwtResponse);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }


}
