package com.waggy.mapper;

import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapRequestDtoToEntity() {

        UserRequestDTO dto = new UserRequestDTO(
                "Jawad",
                "jawad@test.com",
                "password123"
        );

        User user = userMapper.toEntity(dto);

        assertEquals("Jawad", user.getName());
        assertEquals("jawad@test.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void shouldMapEntityToResponseDto() {

        User user = new User();
        user.setId(1);
        user.setName("Jawad");
        user.setEmail("jawad@test.com");
        user.setRole(Role.USER);

        UserResponseDTO dto = userMapper.toDTO(user);

        assertEquals(1, dto.id());
        assertEquals("Jawad", dto.name());
        assertEquals("jawad@test.com", dto.email());
        assertEquals(Role.USER, dto.role());
    }
}