package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uvhnael.mpbe.exception.ResourceNotFoundException;
import org.uvhnael.mpbe.model.ShoppingList;
import org.uvhnael.mpbe.model.ShoppingListItem;
import org.uvhnael.mpbe.repository.ShoppingListItemRepository;
import org.uvhnael.mpbe.repository.ShoppingListRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingListItemService {
    
    private final ShoppingListItemRepository itemRepository;
    private final ShoppingListRepository shoppingListRepository;
    
    public List<ShoppingListItem> getItemsByShoppingListId(Long shoppingListId) {
        return itemRepository.findByShoppingListId(shoppingListId);
    }
    
    public List<ShoppingListItem> getUncheckedItems(Long shoppingListId) {
        return itemRepository.findByShoppingListIdAndIsChecked(shoppingListId, false);
    }
    
    @Transactional
    public ShoppingListItem addItem(Long shoppingListId, ShoppingListItem item) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
            .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));
        
        item.setShoppingList(shoppingList);
        if (item.getIsChecked() == null) {
            item.setIsChecked(false);
        }
        
        return itemRepository.save(item);
    }
    
    @Transactional
    public ShoppingListItem updateItem(Long itemId, ShoppingListItem itemDetails) {
        ShoppingListItem item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Shopping list item not found"));
        
        if (itemDetails.getIngredientName() != null) {
            item.setIngredientName(itemDetails.getIngredientName());
        }
        if (itemDetails.getQuantity() != null) {
            item.setQuantity(itemDetails.getQuantity());
        }
        if (itemDetails.getUnit() != null) {
            item.setUnit(itemDetails.getUnit());
        }
        if (itemDetails.getIsChecked() != null) {
            item.setIsChecked(itemDetails.getIsChecked());
        }
        if (itemDetails.getCategory() != null) {
            item.setCategory(itemDetails.getCategory());
        }
        
        return itemRepository.save(item);
    }
    
    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Shopping list item not found");
        }
        itemRepository.deleteById(itemId);
    }
    
    @Transactional
    public void toggleItemChecked(Long itemId) {
        ShoppingListItem item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Shopping list item not found"));
        
        item.setIsChecked(!item.getIsChecked());
        itemRepository.save(item);
    }
}
