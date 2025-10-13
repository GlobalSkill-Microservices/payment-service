package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Payment.Dto.ProductRequest;
import com.globalskills.payment_service.Payment.Dto.ProductResponse;
import com.globalskills.payment_service.Payment.Entity.Product;
import com.globalskills.payment_service.Payment.Repository.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductCommandService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductQueryService productQueryService;

    @Autowired
    ProductRepo productRepo;

    public ProductResponse create(ProductRequest request){
        Product product = modelMapper.map(request,Product.class);
        productRepo.save(product);
        return modelMapper.map(product, ProductResponse.class);
    }

    public ProductResponse update(ProductRequest request,Long id){
        Product oldProduct = productQueryService.findProductById(id);
        modelMapper.map(request,oldProduct);
        productRepo.save(oldProduct);
        return modelMapper.map(oldProduct, ProductResponse.class);
    }

    public void delete(){

    }
}
