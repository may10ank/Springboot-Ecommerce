package com.example.EcommerceWeb.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.EcommerceWeb.Configuration.CloudinaryConfig;
import com.example.EcommerceWeb.DTO.*;
import com.example.EcommerceWeb.Repository.ProductImageRepository;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.example.EcommerceWeb.Repository.ReviewRepository;
import com.example.EcommerceWeb.customException.ProductNotFoundException;
import com.example.EcommerceWeb.model.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final Validator validator;
    private final Cloudinary cloudinary;

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, ReviewRepository reviewRepository, ReviewService reviewService, Validator validator,Cloudinary cloudinary) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.validator = validator;
        this.cloudinary = cloudinary;
    }

    private final String CACHE_NAME="product";
    public Product addProduct(Product product, List<MultipartFile> images, MultipartFile video) {
    try{
    List<ProductImage> imageEntity=new ArrayList<>();
    if(images!=null) {
        for (MultipartFile file : images) {
            Map result=cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder","products/images"));
            ProductImage image1 = new ProductImage();
            image1.setImageUrl((String)result.get("secure_url"));
            image1.setPublicId((String)result.get("public_id"));
            image1.setProduct(product);
            imageEntity.add(image1);
        }
    }
    product.setImages(imageEntity);
    if(video!=null){
        Map result=cloudinary.uploader().upload(video.getBytes(),ObjectUtils.asMap("resource_type","video","folder","products/videos"));
        ProductVideo productVideo=new ProductVideo();
        productVideo.setVideoUrl((String)result.get("secure_url"));
        productVideo.setPublicId((String)result.get("public_id"));
        productVideo.setProduct(product);
        product.setVideo(productVideo);
    }
    return productRepository.save(product);
}catch (IOException e){
      throw new RuntimeException("Errors while adding the product",e);
}
    }

   // @CachePut(value = CACHE_NAME,key = "#id")
    public Product updateProduct(int id, Product product, List<MultipartFile> images, MultipartFile video) {
        Product existing=productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product with ID" + id + "not found"));
        existing.setProductName(product.getProductName());
        existing.setProductDescription(product.getProductDescription());
        existing.setCategory(product.getCategory());
        existing.setBrand(product.getBrand());
        existing.setStock(product.getStock());
        existing.setActualPrice(product.getActualPrice());
        existing.setDiscountedPrice(product.getDiscountedPrice());
        existing.setDiscountPercent(product.getDiscountPercent());
        try{
            if(images!=null && !images.isEmpty()){
                if (existing.getImages() != null) {
                    for (ProductImage oldImage : existing.getImages()) {
                        if (oldImage.getPublicId() != null) {
                            cloudinary.uploader().destroy(
                                    oldImage.getPublicId(),
                                    ObjectUtils.emptyMap()
                            );
                        }
                    }
                }

                List<ProductImage> imageEntities = new ArrayList<>();
                for (MultipartFile file : images) {
                    Map uploadResult = cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap("folder", "products/images")
                    );
                    ProductImage image1 = new ProductImage();
                    image1.setImageUrl((String) uploadResult.get("secure_url"));
                    image1.setPublicId((String) uploadResult.get("public_id"));
                    image1.setProduct(existing);
                    imageEntities.add(image1);
                }
                 existing.setImages(imageEntities);
            }

            if (video != null && !video.isEmpty()) {
                if (existing.getVideo() != null && existing.getVideo().getPublicId() != null) {
                    cloudinary.uploader().destroy(
                            existing.getVideo().getPublicId(),
                            ObjectUtils.asMap("resource_type", "video")
                    );
                }
                Map uploadResult = cloudinary.uploader().upload(
                        video.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", "video",
                                "folder", "products/videos"
                        )
                );
                ProductVideo productVideo = existing.getVideo() != null
                        ? existing.getVideo()
                        : new ProductVideo();
                productVideo.setVideoUrl((String) uploadResult.get("secure_url"));
                productVideo.setPublicId((String) uploadResult.get("public_id"));
                productVideo.setProduct(existing);
                existing.setVideo(productVideo);
            }
            return productRepository.save(existing);
        }catch(IOException e){
            throw new RuntimeException("Error While updating product files",e);
        }
    }

  //  @CacheEvict(value = CACHE_NAME,key = "#id")
  public void deleteProduct(int id) {
      Product existing = productRepository.findById(id)
              .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
      try {
          if (existing.getImages() != null) {
              for (ProductImage image : existing.getImages()) {
                  if (image.getPublicId() != null) {
                      cloudinary.uploader().destroy(
                              image.getPublicId(),
                              ObjectUtils.emptyMap()
                      );
                  }
              }
          }
          if (existing.getVideo() != null && existing.getVideo().getPublicId() != null) {
              cloudinary.uploader().destroy(
                      existing.getVideo().getPublicId(),
                      ObjectUtils.asMap("resource_type", "video")
              );
          }

      } catch (Exception e) {
          throw new RuntimeException("Error deleting media from Cloudinary", e);
      }
      productRepository.deleteById(id);
  }


    public Page<ProductListingDTO> getProductByBusiness(int businessId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByBusinessBusinessId(businessId, pageable);

        return products.map(product -> {
            RatingSummaryDTO rating = reviewService.getRatingSummary(product.getProductId());
            return ProductListingDTO.productToListDto(product, rating);
        });
    }


    public Page<ProductListDTO> getAllProducts(int page,int size) {
        Pageable pageable=PageRequest.of(page,size);
        LocalDateTime oneWeekAgo=LocalDateTime.now().minusWeeks(1);
//        Page<Product> products=productRepository.findAll(pageable);
         Page<Product> products = productRepository.findAllSortedProducts(oneWeekAgo, pageable);

        return products.map(product->{
            RatingSummaryDTO ratingSummaryDTO=reviewService.getRatingSummary(product.getProductId());
            return ProductListDTO.productToListDto(product,ratingSummaryDTO);
                });
    }

    //@Cacheable(value = CACHE_NAME,key = "#id")
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
        List<String> imageUrls=product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                                .collect(Collectors.toList());
        productDTO.setProductImages(imageUrls);
        if(product.getVideo()!=null) {
            productDTO.setVideos(product.getVideo().getVideoUrl());
        }
        productDTO.setBrand(product.getBrand());
        productDTO.setRatingSummaryDTO(ratingSummaryDTO);
        productDTO.setReviewList(reviewDTO);
        productDTO.setRatingDistributionSummary(distribution);
        productDTO.setTotalSalesCount(product.getTotalSalesCount());
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
    }

  //  @CachePut(value = CACHE_NAME, key = "#id")
    public Product updateProductWithMedia(int id, Map<String, Object> updates, List<MultipartFile> images, MultipartFile video) throws IOException{
        Product product=productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product with ID" + id + "not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name": product.setProductName((String) value); break;
                case "description": product.setProductDescription((String) value); break;
                case "category": product.setCategory((String) value); break;
                case "brand": product.setBrand((String) value); break;
                case "stock": product.setStock(((Number) value).intValue()); break;
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
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap("folder", "products/images")
                );
                ProductImage img = new ProductImage();
                img.setImageUrl((String) uploadResult.get("secure_url"));
                img.setPublicId((String) uploadResult.get("public_id"));
                img.setProduct(product);
                productImageRepository.save(img);
            }
        }
        if (video != null && !video.isEmpty()) {
            if (product.getVideo() != null && product.getVideo().getPublicId() != null) {
                cloudinary.uploader().destroy(
                        product.getVideo().getPublicId(),
                        ObjectUtils.asMap("resource_type", "video")
                );
            }
            Map uploadResult = cloudinary.uploader().upload(
                    video.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", "products/videos"
                    )
            );
            ProductVideo productVideo = product.getVideo() != null
                    ? product.getVideo()
                    : new ProductVideo();

            productVideo.setVideoUrl((String) uploadResult.get("secure_url"));
            productVideo.setPublicId((String) uploadResult.get("public_id"));
            productVideo.setProduct(product);
            product.setVideo(productVideo);
        }
        return productRepository.save(product);
    }

    public List<ProductListDTO> getSimilarProducts(int productId){
        Product product =productRepository.findById(productId).orElseThrow();

        Specification<Product> spec =ProductSpecification.brandContains(product.getBrand()).or(
                                ProductSpecification.categoryContains(product.getCategory()));
        List<Product> products =productRepository.findAll(spec);
        return products.stream().filter(p ->p.getProductId()!=productId)
                .limit(8).map(p ->ProductListDTO.productToListDto(p,reviewService.getRatingSummary(p.getProductId())))
                .toList();
    }
}
