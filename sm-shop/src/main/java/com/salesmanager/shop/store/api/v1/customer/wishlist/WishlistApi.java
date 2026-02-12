package com.salesmanager.shop.store.api.v1.customer.wishlist;

import javax.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.salesmanager.core.business.services.customer.wishlist.WishlistService;
import com.salesmanager.core.model.customer.wishlist.Wishlist;
import com.salesmanager.core.model.customer.wishlist.WishlistItem;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlist;
import com.salesmanager.shop.model.customer.wishlist.ReadableWishlistItem;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1")
@Api(tags = {"Wishlist management"})
@SwaggerDefinition(tags = {
    @Tag(name = "Wishlist management", description = "Manage customer wishlists")
})
public class WishlistApi {

    @Inject
    private WishlistService wishlistService;

    @GetMapping("/customer/{id}/wishlist")
    @ApiOperation(value = "Get customer wishlist", notes = "Returns wishlist for a customer")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Wishlist retrieved successfully"),
        @ApiResponse(code = 404, message = "Customer not found")
    })
    public ResponseEntity<ReadableWishlist> getWishlist(
            @PathVariable Long id,
            @ApiIgnore MerchantStore merchantStore,
            @ApiIgnore Language language) {
        try {
            Wishlist wishlist = wishlistService.getByCustomer(id);
            ReadableWishlist readable = convertToReadable(wishlist, merchantStore, language);
            return ResponseEntity.ok(readable);
        } catch (Exception e) {
            e.printStackTrace(); // Log the actual error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/customer/{id}/wishlist/product/{productId}")
    @ApiOperation(value = "Add product to wishlist", notes = "Adds a product to customer's wishlist")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product added successfully"),
        @ApiResponse(code = 404, message = "Customer or product not found")
    })
    public ResponseEntity<Void> addProduct(
            @PathVariable Long id,
            @PathVariable Long productId) {
        try {
            wishlistService.addProduct(id, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/customer/{id}/wishlist/product/{productId}")
    @ApiOperation(value = "Remove product from wishlist", notes = "Removes a product from customer's wishlist")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product removed successfully"),
        @ApiResponse(code = 404, message = "Product not found in wishlist")
    })
    public ResponseEntity<Void> removeProduct(
            @PathVariable Long id,
            @PathVariable Long productId) {
        try {
            wishlistService.removeProduct(id, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/customer/{id}/wishlist")
    @ApiOperation(value = "Clear wishlist", notes = "Removes all products from customer's wishlist")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Wishlist cleared successfully")
    })
    public ResponseEntity<Void> clearWishlist(@PathVariable Long id) {
        try {
            wishlistService.clearWishlist(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{id}/wishlist/product/{productId}/exists")
    @ApiOperation(value = "Check if product in wishlist", notes = "Checks if a product exists in customer's wishlist")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Check completed successfully")
    })
    public ResponseEntity<Boolean> isProductInWishlist(
            @PathVariable Long id,
            @PathVariable Long productId) {
        try {
            boolean exists = wishlistService.isProductInWishlist(id, productId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ReadableWishlist convertToReadable(Wishlist wishlist, MerchantStore store, Language language) {
        ReadableWishlist readable = new ReadableWishlist();
        readable.setId(wishlist.getId());
        readable.setCustomerId(wishlist.getCustomer().getId());
        
        // Convert items
        if (wishlist.getItems() != null) {
            for (WishlistItem item : wishlist.getItems()) {
                ReadableWishlistItem readableItem = new ReadableWishlistItem();
                readableItem.setId(item.getId());
                if (item.getAuditSection() != null && item.getAuditSection().getDateCreated() != null) {
                    readableItem.setDateAdded(item.getAuditSection().getDateCreated().toString());
                }
                
                // Create minimal product info
                ReadableProduct product = new ReadableProduct();
                product.setId(item.getProduct().getId());
                if (item.getProduct().getSku() != null) {
                    product.setSku(item.getProduct().getSku());
                }
                readableItem.setProduct(product);
                
                readable.getItems().add(readableItem);
            }
        }
        
        return readable;
    }
}
