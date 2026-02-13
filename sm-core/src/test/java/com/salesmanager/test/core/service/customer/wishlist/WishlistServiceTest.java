package com.salesmanager.test.core.service.customer.wishlist;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.customer.wishlist.WishlistItemRepository;
import com.salesmanager.core.business.repositories.customer.wishlist.WishlistRepository;
import com.salesmanager.core.business.services.customer.wishlist.WishlistServiceImpl;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.customer.wishlist.WishlistItem;

@RunWith(MockitoJUnitRunner.class)
public class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    private WishlistServiceImpl wishlistService;

    private Wishlist mockWishlist;
    private Customer mockCustomer;

    @Before
    public void setUp() throws Exception {
        wishlistService = new WishlistServiceImpl(wishlistRepository);
        
        // Use reflection to set the private field
        java.lang.reflect.Field field = WishlistServiceImpl.class.getDeclaredField("wishlistItemRepository");
        field.setAccessible(true);
        field.set(wishlistService, wishlistItemRepository);
        
        mockCustomer = new Customer();
        mockCustomer.setId(1L);

        mockWishlist = new Wishlist();
        mockWishlist.setId(1L);
        mockWishlist.setCustomer(mockCustomer);
    }

    @Test
    public void testGetByCustomer_ExistingWishlist() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenReturn(mockWishlist);

        Wishlist result = wishlistService.getByCustomer(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals(Long.valueOf(1L), result.getCustomer().getId());
        verify(wishlistRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    public void testGetByCustomer_CreateNewWishlist() throws ServiceException {
        when(wishlistRepository.findByCustomerId(2L)).thenReturn(null).thenReturn(mockWishlist);
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(mockWishlist);

        Wishlist result = wishlistService.getByCustomer(2L);

        assertNotNull(result);
        verify(wishlistRepository, times(2)).findByCustomerId(2L);
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }

    @Test
    public void testAddProduct_Success() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenReturn(mockWishlist);
        when(wishlistItemRepository.existsByWishlistIdAndProductId(1L, 5L)).thenReturn(false);
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenReturn(new WishlistItem());

        wishlistService.addProduct(1L, 5L);

        verify(wishlistItemRepository, times(1)).existsByWishlistIdAndProductId(1L, 5L);
        verify(wishlistItemRepository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    public void testAddProduct_AlreadyExists() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenReturn(mockWishlist);
        when(wishlistItemRepository.existsByWishlistIdAndProductId(1L, 5L)).thenReturn(true);

        wishlistService.addProduct(1L, 5L);

        verify(wishlistItemRepository, times(1)).existsByWishlistIdAndProductId(1L, 5L);
        verify(wishlistItemRepository, never()).save(any(WishlistItem.class));
    }

    @Test
    public void testRemoveProduct_Success() throws ServiceException {
        WishlistItem mockItem = new WishlistItem();
        mockItem.setId(1L);

        when(wishlistRepository.findByCustomerId(1L)).thenReturn(mockWishlist);
        when(wishlistItemRepository.findByWishlistIdAndProductId(1L, 5L)).thenReturn(mockItem);

        wishlistService.removeProduct(1L, 5L);

        verify(wishlistItemRepository, times(1)).findByWishlistIdAndProductId(1L, 5L);
        verify(wishlistItemRepository, times(1)).delete(mockItem);
    }

    @Test
    public void testRemoveProduct_NotFound() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenReturn(mockWishlist);
        when(wishlistItemRepository.findByWishlistIdAndProductId(1L, 5L)).thenReturn(null);

        wishlistService.removeProduct(1L, 5L);

        verify(wishlistItemRepository, times(1)).findByWishlistIdAndProductId(1L, 5L);
        verify(wishlistItemRepository, never()).delete(any(WishlistItem.class));
    }

    @Test
    public void testClearWishlist() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenReturn(mockWishlist);
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(mockWishlist);

        wishlistService.clearWishlist(1L);

        verify(wishlistRepository, times(1)).save(mockWishlist);
        assertTrue(mockWishlist.getItems().isEmpty());
    }

    @Test
    public void testIsProductInWishlist_True() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenReturn(mockWishlist);
        when(wishlistItemRepository.existsByWishlistIdAndProductId(1L, 5L)).thenReturn(true);

        boolean result = wishlistService.isProductInWishlist(1L, 5L);

        assertTrue(result);
        verify(wishlistItemRepository, times(1)).existsByWishlistIdAndProductId(1L, 5L);
    }

    @Test
    public void testIsProductInWishlist_False() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenReturn(mockWishlist);
        when(wishlistItemRepository.existsByWishlistIdAndProductId(1L, 5L)).thenReturn(false);

        boolean result = wishlistService.isProductInWishlist(1L, 5L);

        assertFalse(result);
        verify(wishlistItemRepository, times(1)).existsByWishlistIdAndProductId(1L, 5L);
    }

    @Test
    public void testIsProductInWishlist_NoWishlist() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenReturn(null);

        boolean result = wishlistService.isProductInWishlist(1L, 5L);

        assertFalse(result);
        verify(wishlistItemRepository, never()).existsByWishlistIdAndProductId(anyLong(), anyLong());
    }

    @Test(expected = ServiceException.class)
    public void testGetByCustomer_ThrowsException() throws ServiceException {
        when(wishlistRepository.findByCustomerId(1L)).thenThrow(new RuntimeException("Database error"));

        wishlistService.getByCustomer(1L);
    }
}
