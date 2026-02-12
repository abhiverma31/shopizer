package com.salesmanager.core.business.services.customer.wishlist;

import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.customer.wishlist.WishlistItemRepository;
import com.salesmanager.core.business.repositories.customer.wishlist.WishlistRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.customer.wishlist.WishlistItem;

@Service("wishlistService")
public class WishlistServiceImpl extends SalesManagerEntityServiceImpl<Long, Wishlist> implements WishlistService {

    private WishlistRepository wishlistRepository;
    
    @Inject
    private WishlistItemRepository wishlistItemRepository;

    @Inject
    public WishlistServiceImpl(WishlistRepository wishlistRepository) {
        super(wishlistRepository);
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public Wishlist getByCustomer(Long customerId) throws ServiceException {
        try {
            Wishlist wishlist = wishlistRepository.findByCustomerId(customerId);
            if (wishlist == null) {
                wishlist = new Wishlist();
                Customer customer = new Customer();
                customer.setId(customerId);
                wishlist.setCustomer(customer);
                wishlistRepository.save(wishlist);
                // Reload to get the managed entity with items collection initialized
                wishlist = wishlistRepository.findByCustomerId(customerId);
            }
            return wishlist;
        } catch (Exception e) {
            throw new ServiceException("Error getting wishlist for customer " + customerId, e);
        }
    }

    @Override
    public void addProduct(Long customerId, Long productId) throws ServiceException {
        Wishlist wishlist = getByCustomer(customerId);
        
        if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId)) {
            WishlistItem item = new WishlistItem();
            item.setWishlist(wishlist);
            Product product = new Product();
            product.setId(productId);
            item.setProduct(product);
            wishlistItemRepository.save(item);
        }
    }

    @Override
    public void removeProduct(Long customerId, Long productId) throws ServiceException {
        Wishlist wishlist = getByCustomer(customerId);
        WishlistItem item = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId);
        if (item != null) {
            wishlistItemRepository.delete(item);
        }
    }

    @Override
    public void clearWishlist(Long customerId) throws ServiceException {
        Wishlist wishlist = getByCustomer(customerId);
        wishlist.getItems().clear();
        wishlistRepository.save(wishlist);
    }

    @Override
    public boolean isProductInWishlist(Long customerId, Long productId) throws ServiceException {
        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId);
        if (wishlist == null) {
            return false;
        }
        return wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId);
    }
}
