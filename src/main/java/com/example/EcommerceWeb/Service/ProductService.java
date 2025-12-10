package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.ProductDTO;
import com.example.EcommerceWeb.DTO.ProductListDTO;
import com.example.EcommerceWeb.DTO.RatingSummaryDTO;
import com.example.EcommerceWeb.DTO.ReviewDTO;
import com.example.EcommerceWeb.Repository.BusinessRepository;
import com.example.EcommerceWeb.Repository.ProductImageRepository;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.ReviewRepository;
import com.example.EcommerceWeb.customException.ProductNotFoundException;
import com.example.EcommerceWeb.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
   @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private Validator validator;

    private final String CACHE_NAME="product";
    public Product addProduct(Product product, List<MultipartFile> images, MultipartFile video) {
    try{
    List<ProductImage> imageEntity=new ArrayList<>();
    if(images!=null) {
        for (MultipartFile file : images) {
            ProductImage image1 = new ProductImage();
            image1.setFileName(file.getOriginalFilename());
            image1.setFileType(file.getContentType());
            image1.setData(file.getBytes());
            image1.setProduct(product);
            imageEntity.add(image1);
        }
    }
    product.setImages(imageEntity);
    if(video!=null){
        product.setVideo(video.getBytes());
    }
    return productRepository.save(product);
}catch (IOException e){
      throw new RuntimeException("Errors while adding the product",e);
}
    }

    @CachePut(value = CACHE_NAME,key = "#id")
    public Product updateProduct(int id, Product product, List<MultipartFile> images, MultipartFile video) {
        Product existing=productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product with ID" + id + "not found"));
        existing.setProductName(product.getProductName());
        existing.setProductDescription(product.getProductDescription());
        existing.setCategory(product.getCategory());
        existing.setBrand(product.getBrand());
        existing.setStock(product.getStock()+existing.getStock());
        existing.setActualPrice(product.getActualPrice());
        existing.setDiscountedPrice(product.getDiscountedPrice());
        existing.setDiscountPercent(product.getDiscountPercent());
        try{
            if(images!=null){
                List<ProductImage> imageEntities = new ArrayList<>();
                for (MultipartFile file : images) {
                    ProductImage image1 = new ProductImage();
                    image1.setFileName(file.getOriginalFilename());
                    image1.setFileType(file.getContentType());
                    image1.setData(file.getBytes());
                    image1.setProduct(existing);
                    imageEntities.add(image1);
                }
                 existing.setImages(imageEntities);
            }
            if(video!=null){
                existing.setVideo(video.getBytes());
            }
            return productRepository.save(existing);
        }catch(IOException e){
            throw new RuntimeException("Error While updating product files",e);
        }
    }

    @CacheEvict(value = CACHE_NAME,key = "#id")
    public void deleteProduct(int id) {
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException("Product with ID" + id + "not found");
        }
        productRepository.deleteById(id);
    }

    public Page<Product> getProductByBusiness(int businessId,int page,int size){
        Pageable pageable=PageRequest.of(page,size);
        return productRepository.findByBusinessBusinessId(businessId,pageable);
    }

    public Page<ProductListDTO> getAllProducts(int page,int size) {
        Pageable pageable=PageRequest.of(page,size);
        Page<Product> products=productRepository.findAll(pageable);
        return products.map(product->{
            RatingSummaryDTO ratingSummaryDTO=reviewService.getRatingSummary(product.getProductId());
            return ProductListDTO.productToListDto(product,ratingSummaryDTO);
                });
       // return products.map(ProductListDTO::productToListDto);
    }

    @Cacheable(value = CACHE_NAME,key = "#id")
    public ProductDTO getProductById(int id) {
        Product product=productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product with ID" + id + "not found"));
        RatingSummaryDTO ratingSummaryDTO=reviewService.getRatingSummary(id);
        Page<ReviewDTO> reviewDTO=reviewService.getReviewsForProduct(id,"createdAt",0,5);
        Map<String,Object> distribution=reviewService.getProductRatingWithDistribution(id);
        ProductDTO productDTO=new ProductDTO();
        productDTO.setProductName(product.getProductName());
        productDTO.setProductDescription(product.getProductDescription());
        productDTO.setActualPrice(product.getActualPrice());
        productDTO.setDiscountedPrice(product.getDiscountedPrice());
        productDTO.setDiscountPercent(product.getDiscountPercent());
        List<String> imageBase64=product.getImages().stream()
                        .map(img->Base64.getEncoder().encodeToString(img.getData()))
                                .collect(Collectors.toList());
        productDTO.setProductImages(imageBase64);
        if(product.getVideo()!=null) {
            productDTO.setVideos(Base64.getEncoder().encodeToString(product.getVideo()));
        }
        productDTO.setBrand(product.getBrand());
        productDTO.setRatingSummaryDTO(ratingSummaryDTO);
        productDTO.setReviewList(reviewDTO);
        productDTO.setRatingDistributionSummary(distribution);
        return productDTO;
    }

    public Page<ProductListDTO> searchProducts(String name, String category, String brand, Integer minPrice, Integer maxPrice,String sortBy,String sortDir,int page,int size){
        Specification<Product> specification=((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        if(name!=null && !name.isEmpty()){
            specification=specification.and(ProductSpecification.nameOrDescriptionContians(name));
        }

        if(category!=null && !category.isEmpty()){
            specification=specification.and(ProductSpecification.categoryContains(category));
        }

        if(brand!=null && !brand.isEmpty()){
            specification=specification.and(ProductSpecification.brandContains(brand));
        }

        if (minPrice != null && maxPrice != null) {
            specification = specification.and(ProductSpecification.priceBetween(minPrice, maxPrice));
        } else if (minPrice != null) {
            specification = specification.and(ProductSpecification.priceGreaterThanEqual(minPrice));
        } else if (maxPrice != null) {
            specification = specification.and(ProductSpecification.priceLessThanEqual(maxPrice));
        }

        Sort sort=Sort.by("id");
        if(sortBy!=null && !sortBy.isEmpty()){
            if("desc".equalsIgnoreCase(sortDir)){
                sort=Sort.by(sortBy).descending();
            }else{
                sort=Sort.by(sortBy).ascending();
            }
        }
        Pageable pageable= PageRequest.of(page,size,sort);
        Page<Product> product = productRepository.findAll(specification, pageable);
        return product.map(products -> {
            RatingSummaryDTO ratingSummary = reviewService.getRatingSummary(products.getProductId());
            return ProductListDTO.productToListDto(products, ratingSummary);
        });

        //return productRepository.findAll(specification,pageable);
    }

    @CachePut(value = CACHE_NAME, key = "#id")
    public Product updateProductWithMedia(int id, Map<String, Object> updates, List<MultipartFile> images, MultipartFile video) throws IOException{
        Product product=productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product with ID" + id + "not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name": product.setProductName((String) value); break;
                case "description": product.setProductDescription((String) value); break;
                case "category": product.setCategory((String) value); break;
                case "brand": product.setBrand((String) value); break;
                case "stock": product.setStock(product.getStock() + ((Number) value).intValue()); break;
                case "actualPrice": product.setActualPrice(((Number) value).intValue()); break;
                case "discountedPrice": product.setDiscountedPrice(((Number) value).intValue()); break;
                case "discountPercent": product.setDiscountPercent(((Number) value).intValue()); break;
            }
        });

        Errors error=new BeanPropertyBindingResult(product,"product");
        validator.validate(product,error);
        if(error.hasErrors()){
            throw new IllegalArgumentException(error.getAllErrors().toString());
        }

        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                ProductImage img = new ProductImage();
                img.setData(file.getBytes());
                img.setProduct(product);
                productImageRepository.save(img);
            }
        }
        if (video != null) {
            product.setVideo(video.getBytes());
        }
        return productRepository.save(product);
    }
}
