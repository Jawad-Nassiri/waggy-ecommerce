package com.waggy.service;
import com.waggy.dto.user.AdminUserUpdateDTO;
import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.dto.user.UserUpdateDTO;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import com.waggy.exception.UserNotFoundException;
import com.waggy.mapper.UserMapper;
import com.waggy.repository.OrderRepository;
import com.waggy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private UserService userService;

    private User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("jawad");
        user.setEmail("jawad@test.com");
        user.setRole(Role.USER);

        return user;
    }

    private UserResponseDTO createUserResponseDTO() {
        return new UserResponseDTO(
                1,
                "jawad",
                "jawad@test.com",
                Role.USER,
                LocalDateTime.parse("2020-07-25T00:00:00")
        );
    }

    @Test
    void saveUserInDb_shouldSaveUser() {

        UserRequestDTO dto = new UserRequestDTO(
                "Jawad",
                "jawad@test.com",
                "password"
        );

        User user = new User();
        user.setName("Jawad");
        user.setEmail("jawad@test.com");
        user.setPassword("password");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setName("Jawad");
        savedUser.setEmail("jawad@test.com");

        UserResponseDTO response = new UserResponseDTO(
                1,
                "Jawad",
                "jawad@test.com",
                savedUser.getRole(),
                LocalDateTime.parse("2030-12-12T10:30:00")
        );

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(response);

        UserResponseDTO result = userService.saveUserInDb(dto);


        assertEquals(response, result);
        verify(userRepository).existsByEmail(dto.email());
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
        verify(userMapper).toDTO(savedUser);
    }

    @Test
    void findAllUsers_shouldFindAllUsers() {
        User user = createUser();
        List<User> users = List.of(user);
        UserResponseDTO userResponseDTO = createUserResponseDTO();

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        List<UserResponseDTO> result = userService.findAllUsers();

        assertEquals(List.of(userResponseDTO), result);
        verify(userRepository).findAll();
        verify(userMapper).toDTO(user);
    }

    @Test
    void findUserById_shouldFindById() {
        User user = createUser();
        UserResponseDTO userResponseDTO = createUserResponseDTO();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.findUserById(user.getId());

        assertEquals(userResponseDTO, result);
        verify(userRepository).findById(user.getId());
        verify(userMapper).toDTO(user);

    }

    @Test
    void findUserByEmail_shouldFindByEmail() {
        String email = "jawad@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        User user = createUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());

        verify(userRepository).findById(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void updateUser_shouldUpdateUser() {
        User user = createUser();

        AdminUserUpdateDTO adminUserUpdateDTO = new AdminUserUpdateDTO(
                "jawad",
                "jawad.test.com",
                Role.ADMIN
        );

        User updatedUser = createUser();
        UserResponseDTO responseDTO = createUserResponseDTO();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(adminUserUpdateDTO.email())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toDTO(updatedUser)).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUser(user.getId(), adminUserUpdateDTO);

        assertEquals(responseDTO, result);
        verify(userRepository).findById(user.getId());
        verify(userRepository).existsByEmail(adminUserUpdateDTO.email());
        verify(userRepository).save(user);
        verify(userMapper).toDTO(updatedUser);
    }

    @Test
    void updateCurrentUser_shouldUpdateCurrentUser() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(
                "jawad",
                "new@test.com"
        );

        User user = createUser();
        User savedUser = createUser();
        UserResponseDTO responseDTO = createUserResponseDTO();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("jawad@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userUpdateDTO.email()))
                .thenReturn(false);
        when(userRepository.save(user))
                .thenReturn(savedUser);
        when(userMapper.toDTO(savedUser))
                .thenReturn(responseDTO);

        UserResponseDTO result = userService.updateCurrentUser(userUpdateDTO);

        assertEquals(responseDTO, result);

        verify(userRepository).findByEmail("jawad@test.com");
        verify(userRepository).existsByEmail(userUpdateDTO.email());
        verify(userRepository).save(user);
        verify(userMapper).toDTO(savedUser);
    }

    @Test
    void getCurrentUser_shouldReturnGetUser() {
        User user = createUser();
        UserResponseDTO responseDTO = createUserResponseDTO();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("jawad@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(userMapper.toDTO(user))
                .thenReturn(responseDTO);

        UserResponseDTO result = userService.getCurrentUser();

        assertEquals(responseDTO, result);

        verify(userRepository).findByEmail("jawad@test.com");
        verify(userMapper).toDTO(user);
    }

    @Test
    void deleteCurrentUser_shouldDeleteCurrentUser() {
        User user = createUser();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("jawad@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));

        userService.deleteCurrentUser();

        verify(userRepository).findByEmail("jawad@test.com");
        verify(userRepository).delete(user);
    }
}