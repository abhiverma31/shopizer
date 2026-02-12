package com.salesmanager.shop.model.customer.wishlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReadableWishlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long customerId;
    private List<ReadableWishlistItem> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<ReadableWishlistItem> getItems() {
        return items;
    }

    public void setItems(List<ReadableWishlistItem> items) {
        this.items = items;
    }
}
