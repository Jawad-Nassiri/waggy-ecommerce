package com.waggy.service;

import com.waggy.dto.user.*;
import com.waggy.entity.User;
import com.waggy.exception.EmailAlreadyExistsException;
import com.waggy.exception.UserHasOrdersException;
import com.waggy.exception.UserNotFoundException;
import com.waggy.mapper.UserMapper;
import com.waggy.repository.OrderRepository;
import com.waggy.repository.PasswordResetTokenRepository;
import com.waggy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.waggy.repository.CartRepository;
import com.waggy.entity.Cart;
import java.security.SecureRandom;
import java.util.Base64;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserResponseDTO saveUserInDb(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO createAdminUser(AdminUserCreateDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = userMapper.toAdminEntity(dto);

        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        String temporaryPassword = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        user.setPassword(passwordEncoder.encode(temporaryPassword));

        User savedUser = userRepository.save(user);
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        String token = passwordResetService.generateToken();
        passwordResetService.saveToken(savedUser, token);

        String invitationLink =
                "https://waggy-frontend.onrender.com/reset-password?token=" + token;

        emailService.sendResetEmail(savedUser.getEmail(), invitationLink);

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

    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean haveOrder = orderRepository.existsByUserId(user.getId());

        if (haveOrder) {
            throw new UserHasOrdersException("Cannot delete user with existing orders");
        }
        passwordResetTokenRepository.deleteByUser(user);
        cartRepository.deleteByUserId(user.getId());
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
