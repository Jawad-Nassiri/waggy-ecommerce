package com.waggy.service;

import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.entity.User;
import com.waggy.exception.EmailAlreadyExistsException;
import com.waggy.exception.UserNotFoundException;
import com.waggy.mapper.UserMapper;
import com.waggy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.waggy.dto.user.UserUpdateDTO;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    // add Dependencies
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // create a new user
    public UserResponseDTO saveUserInDb(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }


    // get all users
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDTO).toList();
    }


    // get a user by ID
    public UserResponseDTO findUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDTO(user);
    }

    // delete a user by ID
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }


    // update a user's name, email, and role
    public UserResponseDTO updateUser(Integer id, UserUpdateDTO dto) {

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
}
