package com.waggy.service;

import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.entity.User;
import com.waggy.exception.EmailAlreadyExistsException;
import com.waggy.mapper.UserMapper;
import com.waggy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO saveUserInDb(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDTO).toList();
    }
}
