package com.example.EcommerceWeb.Controller;

import com.example.EcommerceWeb.DTO.ProductDTO;
import com.example.EcommerceWeb.DTO.ProductListDTO;
import com.example.EcommerceWeb.Repository.BusinessRepository;
import com.example.EcommerceWeb.Service.ProductService;
import com.example.EcommerceWeb.Service.VoiceSearchService;
import com.example.EcommerceWeb.model.Business;
import com.example.EcommerceWeb.model.Product;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    VoiceSearchService voiceSearchService;
    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@Valid @RequestPart("product") Product product, @RequestPart(value = "images", required = false) List<MultipartFile> images, @RequestPart(value = "video", required = false) MultipartFile video, Authentication authentication) {
        String email = authentication.getName();
        Business business = businessRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        product.setBusiness(business);
        Product saved = productService.addProduct(product, images, video);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @Valid @RequestPart("product") Product product, @RequestPart(value = "images", required = false) List<MultipartFile> images, @RequestPart(value = "video", required = false) MultipartFile video) {
        Product getproduct = productService.updateProduct(id, product, images, video);
        if (getproduct.getProductId() > 0) {
            return new ResponseEntity<>(getproduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(getproduct, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>("Product deleted successfully",HttpStatus.OK);
    }

    @GetMapping("/getProductBusiness")
    public ResponseEntity<Page<Product>> getProductByBusiness(@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "10")int size,Authentication authentication){
        String email = authentication.getName();
        Business business = businessRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return new ResponseEntity<>(productService.getProductByBusiness(business.getBusinessId(),page,size),HttpStatus.OK);
    }

    @GetMapping("/getProduct")
    public ResponseEntity<List<ProductListDTO>> getAllProducts(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "15")int size) {
        Page<ProductListDTO> products = productService.getAllProducts(page,size);
        return new ResponseEntity<>(products.getContent(), HttpStatus.OK);
    }

    @GetMapping("/getProduct/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable int id) {
        ProductDTO product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductListDTO>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductListDTO> products = productService.searchProducts(name, category, brand, minPrice, maxPrice, sortBy, sortDir, page, size);
        return new ResponseEntity<>(products.getContent(), HttpStatus.OK);
    }

    @PatchMapping(value = "/uProduct/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> updateProductwithMedia(
            @PathVariable int id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Integer actualPrice,
            @RequestParam(required = false) Integer discountedPrice,
            @RequestParam(required = false) Integer discountPercent,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) MultipartFile video
    ) throws IOException {
        Map<String, Object> updates = new HashMap<>();
        if (name != null) updates.put("name", name);
        if (description != null) updates.put("description", description);
        if (category != null) updates.put("category", category);
        if (brand != null) updates.put("brand", brand);
        if (stock != null) updates.put("stock", stock);
        if (actualPrice != null) updates.put("actualPrice", actualPrice);
        if (discountedPrice != null) updates.put("discountedPrice", discountedPrice);
        if (discountPercent != null) updates.put("discountPercent", discountPercent);

        Product updatedProduct = productService.updateProductWithMedia(id, updates, images, video);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PostMapping("/voiceSearch")
    public ResponseEntity<List<ProductListDTO>> searchProductsByVoice(
            @RequestParam("audioFile") MultipartFile audioFile) throws Exception{
        try {
            List<ProductListDTO> products = voiceSearchService.searchByVoice(audioFile);
            return new ResponseEntity<>(products,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}

