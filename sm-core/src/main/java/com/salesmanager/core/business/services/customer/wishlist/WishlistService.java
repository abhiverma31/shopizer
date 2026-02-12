package com.salesmanager.core.business.services.customer.wishlist;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityService;
import com.salesmanager.core.model.customer.wishlist.Wishlist;

public interface WishlistService extends SalesManagerEntityService<Long, Wishlist> {

    Wishlist getByCustomer(Long customerId) throws ServiceException;
    
    void addProduct(Long customerId, Long productId) throws ServiceException;
    
    void removeProduct(Long customerId, Long productId) throws ServiceException;
    
    void clearWishlist(Long customerId) throws ServiceException;
    
    boolean isProductInWishlist(Long customerId, Long productId) throws ServiceException;
}
