package org.uvhnael.mpbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uvhnael.mpbe.model.ShoppingListItem;

import java.util.List;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {
    List<ShoppingListItem> findByShoppingListId(Long shoppingListId);
    List<ShoppingListItem> findByShoppingListIdAndIsChecked(Long shoppingListId, Boolean isChecked);
    void deleteByShoppingListId(Long shoppingListId);
}
