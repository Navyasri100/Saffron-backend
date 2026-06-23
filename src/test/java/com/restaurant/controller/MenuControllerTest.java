package com.restaurant.controller;

import com.restaurant.model.MenuItem;
import com.restaurant.service.MenuService;
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
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController controller;

    @Test
    void getAllUsesCategoryFilterWhenProvided() {
        when(menuService.getByCategory(3L)).thenReturn(List.of(new MenuItem()));

        List<MenuItem> result = controller.getAll(3L, null);

        assertEquals(1, result.size());
        verify(menuService).getByCategory(3L);
    }

    @Test
    void getAllUsesTagFilterWhenProvided() {
        when(menuService.getByTag("veg")).thenReturn(List.of(new MenuItem()));

        List<MenuItem> result = controller.getAll(null, "veg");

        assertEquals(1, result.size());
        verify(menuService).getByTag("veg");
    }

    @Test
    void getAllReturnsAllItemsWhenNoFiltersProvided() {
        when(menuService.getAll()).thenReturn(List.of(new MenuItem(), new MenuItem()));

        List<MenuItem> result = controller.getAll(null, null);

        assertEquals(2, result.size());
        verify(menuService).getAll();
    }

    @Test
    void createReturnsSavedItem() {
        MenuItem item = new MenuItem();
        item.setName("Paneer");
        when(menuService.save(item)).thenReturn(item);

        ResponseEntity<MenuItem> response = controller.create(item);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Paneer", response.getBody().getName());
    }

    @Test
    void updateReturnsUpdatedItem() {
        MenuItem item = new MenuItem();
        item.setName("Updated");
        when(menuService.update(5L, item)).thenReturn(item);

        ResponseEntity<MenuItem> response = controller.update(5L, item);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated", response.getBody().getName());
    }

    @Test
    void deleteReturnsNoContent() {
        ResponseEntity<Void> response = controller.delete(9L);

        verify(menuService).delete(9L);
        assertEquals(204, response.getStatusCode().value());
    }
}

