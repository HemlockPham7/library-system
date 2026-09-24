package com.librarysystem.userservice.service;

import com.librarysystem.userservice.dto.CreateUserRequestDTO;
import com.librarysystem.userservice.dto.LoginRequestDto;
import com.librarysystem.userservice.dto.UserResponseDTO;
import com.librarysystem.userservice.dto.identity.TokenExchangeResponse;

import java.util.List;

public interface IUserService {
    UserResponseDTO createUser(CreateUserRequestDTO dto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, CreateUserRequestDTO dto);
    void deleteUser(Long id);

    TokenExchangeResponse login(LoginRequestDto dto);
}