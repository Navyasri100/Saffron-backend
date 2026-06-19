package com.restaurant.service;

import com.restaurant.model.MenuItem;
import com.restaurant.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getAll() {
        return menuItemRepository.findByIsAvailableTrue();
    }

    public List<MenuItem> getByCategory(Long categoryId) {
        return menuItemRepository.findByCategoryId(categoryId);
    }

    public List<MenuItem> getByTag(String tag) {
        return menuItemRepository.findByTagsContaining(tag);
    }

    public MenuItem save(MenuItem item) {
        return menuItemRepository.save(item);
    }

    public MenuItem update(Long id, MenuItem updated) {
        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setImageUrl(updated.getImageUrl());
        existing.setCategory(updated.getCategory());
        existing.setTags(updated.getTags());
        existing.setIsAvailable(updated.getIsAvailable());
        return menuItemRepository.save(existing);
    }

    public void delete(Long id) {
        menuItemRepository.deleteById(id);
    }
}
