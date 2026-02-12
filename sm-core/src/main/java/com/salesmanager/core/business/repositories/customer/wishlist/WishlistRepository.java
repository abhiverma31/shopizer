package com.salesmanager.core.business.repositories.customer.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.salesmanager.core.model.customer.wishlist.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("select distinct w from Wishlist w left join fetch w.items i left join fetch i.product where w.customer.id = ?1")
    Wishlist findByCustomerId(Long customerId);
}
