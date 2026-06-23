package com.restaurant.controller;

import com.restaurant.model.Category;
import com.restaurant.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryController controller;

    @Test
    void getAllReturnsCategories() {
        Category category = new Category();
        category.setName("Main Course");
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<Category> result = controller.getAll();

        assertEquals(1, result.size());
        assertEquals("Main Course", result.get(0).getName());
    }

    @Test
    void createReturnsSavedCategory() {
        Category category = new Category();
        category.setName("Dessert");
        when(categoryRepository.save(category)).thenReturn(category);

        ResponseEntity<Category> response = controller.create(category);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Dessert", response.getBody().getName());
    }

    @Test
    void deleteReturnsNoContent() {
        ResponseEntity<Void> response = controller.delete(10L);

        verify(categoryRepository).deleteById(10L);
        assertEquals(204, response.getStatusCode().value());
    }
}

