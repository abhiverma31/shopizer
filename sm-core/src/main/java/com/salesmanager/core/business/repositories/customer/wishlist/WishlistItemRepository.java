package com.salesmanager.core.business.repositories.customer.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.salesmanager.core.model.customer.wishlist.WishlistItem;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    @Query("select count(wi) > 0 from WishlistItem wi where wi.wishlist.id = ?1 and wi.product.id = ?2")
    boolean existsByWishlistIdAndProductId(Long wishlistId, Long productId);
    
    @Query("select wi from WishlistItem wi where wi.wishlist.id = ?1 and wi.product.id = ?2")
    WishlistItem findByWishlistIdAndProductId(Long wishlistId, Long productId);
}
