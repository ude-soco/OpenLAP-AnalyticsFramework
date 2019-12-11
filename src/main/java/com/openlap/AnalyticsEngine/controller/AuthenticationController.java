package com.openlap.AnalyticsEngine.controller;

import com.openlap.AnalyticsEngine.model.OpenLapUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.openlap.AnalyticsEngine.authconfig.TokenProvider;
import com.openlap.AnalyticsEngine.dto.AuthToken;
import com.openlap.AnalyticsEngine.dto.LoginUser;

/**
 * The AuthenticationController class handles the requests received to
 * login.Before sending any other request user need to send login request after
 * this request if he is authticated user he will receive the Jwt token. That
 * Jwt token needs to be send as Bearer Token in all other requests
 * 
 * @author Sammar Javed
 * @version 1.0
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/authenticate/")
public class AuthenticationController {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenProvider jwtTokenUtil;


	/**
	 * User Authentication
	 * 
	 * @param loginUser
	 *            This LoginUser Object contains email and password
	 * 
	 * @return After authentication returns the string JWT token.This returned JWT
	 *         token needs to be passed as Bearer token in every request
	 */
	@RequestMapping(value = "/get-token", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody LoginUser loginUser) throws AuthenticationException {

		final Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final String token = jwtTokenUtil.generateToken(authentication);
		return ResponseEntity.ok(new AuthToken(token));
	}
}
