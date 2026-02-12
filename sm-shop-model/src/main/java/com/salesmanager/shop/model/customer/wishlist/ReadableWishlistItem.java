package com.salesmanager.shop.model.customer.wishlist;

import java.io.Serializable;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;

public class ReadableWishlistItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private ReadableProduct product;
    private String dateAdded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReadableProduct getProduct() {
        return product;
    }

    public void setProduct(ReadableProduct product) {
        this.product = product;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
