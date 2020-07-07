package com.codmind.orderapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codmind.orderapi.converters.UserConverter;
import com.codmind.orderapi.dtos.LoginRequestDTO;
import com.codmind.orderapi.dtos.LoginResponseDTO;
import com.codmind.orderapi.entity.User;
import com.codmind.orderapi.exceptions.GeneralServiceException;
import com.codmind.orderapi.exceptions.NoDataFoundException;
import com.codmind.orderapi.exceptions.ValidateServiceException;
import com.codmind.orderapi.repository.UserRespository;
import com.codmind.orderapi.validators.UserValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private UserConverter userConverter;

	@Autowired
	private UserRespository userRepo;
	
	public User createUser(User user) {
		try {

			UserValidator.signup(user);
			
			User existUser = userRepo.findByUsername(user.getUsername())
					.orElse(null);
			
			if(existUser != null) throw new ValidateServiceException("El nombre de usuario ya existe");
			
			return userRepo.save(user);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	
	public LoginResponseDTO login(LoginRequestDTO request) {
		try {
			User user = userRepo.findByUsername(request.getUsername())
					.orElseThrow(() -> new ValidateServiceException("Usuario o password incorrectos"));
			
			if(! user.getPassword().equals(request.getPassword())) throw new ValidateServiceException("Usuario o password incorrectos");
			return new LoginResponseDTO(userConverter.fromEntity(user), "TOKEN");
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
}
