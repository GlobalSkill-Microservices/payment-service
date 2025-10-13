package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Common.PageResponse;
import com.globalskills.payment_service.Payment.Dto.ProductResponse;
import com.globalskills.payment_service.Payment.Entity.Product;
import com.globalskills.payment_service.Payment.Exception.ProductException;
import com.globalskills.payment_service.Payment.Repository.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductQueryService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductRepo productRepo;

    public  Product findProductById(Long id){
        return productRepo.findById(id).orElseThrow(()-> new ProductException("Product not found", HttpStatus.NOT_FOUND));
    }


    public ProductResponse getProductById(Long id){
        Product product = findProductById(id);
        return modelMapper.map(product, ProductResponse.class);
    }

    public PageResponse<ProductResponse> findAll(
            int page,
            int size,
            String sortBy,
            String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Product> productPage = productRepo.findAll(pageRequest);
        if(productPage.isEmpty()){
            return null;
        }

        List<ProductResponse> responses = productPage
                .stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .toList();
        return new PageResponse<>(
                responses,
                page,
                size,
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );

    }


}
