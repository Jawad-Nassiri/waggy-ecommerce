package com.waggy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.service.CategoryService;
import com.waggy.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryRequestDTO createRequestDTO() {
        return new CategoryRequestDTO(
                "Dog Food",
                "dog_food"
        );
    }

    private CategoryResponseDTO createResponseDTO() {
        return new CategoryResponseDTO(
                1,
                "Dog Food",
                "dog_food"
        );
    }

    @Test
    void createCategory_shouldReturnCreatedCategory() throws Exception {

        CategoryRequestDTO request = createRequestDTO();
        CategoryResponseDTO response = createResponseDTO();

        when(categoryService.saveCategoryInDb(request))
                .thenReturn(response);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dog Food"))
                .andExpect(jsonPath("$.slug").value("dog_food"));

        verify(categoryService).saveCategoryInDb(request);
    }

    @Test
    void findAllCategories_shouldReturnCategories() throws Exception {
        List<CategoryResponseDTO> categoryResponseDTOS = List.of(createResponseDTO());

        when(categoryService.findAllCategories()).thenReturn(categoryResponseDTOS);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dog Food"))
                .andExpect(jsonPath("$[0].slug").value("dog_food"));
        verify(categoryService).findAllCategories();
    }

    @Test
    void findCategoryById_shouldReturnCategory() throws Exception {
        CategoryResponseDTO response = createResponseDTO();

        when(categoryService.findCategoryById(1)).thenReturn(response);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dog Food"))
                .andExpect(jsonPath("$.slug").value("dog_food"));

        verify(categoryService).findCategoryById(1);
    }

    @Test
    void updateCategory_shouldReturnUpdatedCategory() throws Exception{
        Integer id = 1;
        CategoryRequestDTO request = createRequestDTO();
        CategoryResponseDTO response = createResponseDTO();

        when(categoryService.updateCategory(id, request)).thenReturn(response);

        mockMvc.perform(put("/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dog Food"))
                .andExpect(jsonPath("$.slug").value("dog_food"));
        verify(categoryService).updateCategory(id, request);
    }

    @Test
    void deleteCategory_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1);
    }
}