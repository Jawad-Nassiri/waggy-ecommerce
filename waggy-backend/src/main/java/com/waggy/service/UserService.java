package com.waggy.service;

import com.waggy.dto.user.AdminUserUpdateDTO;
import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.entity.User;
import com.waggy.exception.EmailAlreadyExistsException;
import com.waggy.exception.UserNotFoundException;
import com.waggy.mapper.UserMapper;
import com.waggy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.waggy.dto.user.UserUpdateDTO;

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


    public UserResponseDTO findUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDTO(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

    }


    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }

    public UserResponseDTO updateUser(Integer id, AdminUserUpdateDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getEmail().equals(dto.email())
                && userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setRole(dto.role());

        User updatedUser = userRepository.save(user);

        return userMapper.toDTO(updatedUser);
    }

    public UserResponseDTO updateCurrentUser(UserUpdateDTO dto) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getEmail().equals(dto.email())
                && userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        user.setName(dto.name());
        user.setEmail(dto.email());

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = findUserByEmail(email);

        return userMapper.toDTO(user);
    }

    public void deleteCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = findUserByEmail(email);

        userRepository.delete(user);
    }
}
