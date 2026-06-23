package com.restaurant.service;

import com.restaurant.model.Category;
import com.restaurant.model.MenuItem;
import com.restaurant.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuService service;

    @Test
    void getAllReturnsAvailableItems() {
        when(menuItemRepository.findByIsAvailableTrue()).thenReturn(List.of(new MenuItem()));

        List<MenuItem> result = service.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getByCategoryReturnsFilteredItems() {
        when(menuItemRepository.findByCategoryId(1L)).thenReturn(List.of(new MenuItem(), new MenuItem()));

        List<MenuItem> result = service.getByCategory(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getByTagReturnsTaggedItems() {
        when(menuItemRepository.findByTagsContaining("spicy")).thenReturn(List.of(new MenuItem()));

        List<MenuItem> result = service.getByTag("spicy");

        assertEquals(1, result.size());
    }

    @Test
    void saveDelegatesToRepository() {
        MenuItem item = new MenuItem();
        when(menuItemRepository.save(item)).thenReturn(item);

        MenuItem result = service.save(item);

        assertEquals(item, result);
    }

    @Test
    void updateCopiesAllFieldsAndSaves() {
        MenuItem existing = new MenuItem();
        existing.setId(1L);
        existing.setName("Old");

        Category category = new Category();
        category.setId(7L);
        MenuItem update = new MenuItem();
        update.setName("New");
        update.setDescription("desc");
        update.setPrice(120.0);
        update.setImageUrl("url");
        update.setCategory(category);
        update.setTags(List.of("veg", "new"));
        update.setIsAvailable(false);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(menuItemRepository.save(existing)).thenReturn(existing);

        MenuItem result = service.update(1L, update);

        assertEquals("New", result.getName());
        assertEquals("desc", result.getDescription());
        assertEquals(120.0, result.getPrice());
        assertEquals("url", result.getImageUrl());
        assertEquals(category, result.getCategory());
        assertEquals(List.of("veg", "new"), result.getTags());
        assertEquals(false, result.getIsAvailable());
    }

    @Test
    void updateThrowsWhenItemMissing() {
        when(menuItemRepository.findById(20L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.update(20L, new MenuItem()));

        assertEquals("Menu item not found: 20", ex.getMessage());
    }

    @Test
    void deleteDelegatesToRepository() {
        service.delete(8L);

        verify(menuItemRepository).deleteById(8L);
    }
}

