package com.globalskills.payment_service.Payment.Controller;

import com.globalskills.payment_service.Common.Dto.BaseResponseAPI;
import com.globalskills.payment_service.Common.Dto.PageResponse;
import com.globalskills.payment_service.Payment.Dto.ProductRequest;
import com.globalskills.payment_service.Payment.Dto.ProductResponse;
import com.globalskills.payment_service.Payment.Service.ProductCommandService;
import com.globalskills.payment_service.Payment.Service.ProductQueryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@SecurityRequirement(name = "api")
public class ProductController {

    @Autowired
    ProductQueryService productQueryService;

    @Autowired
    ProductCommandService productCommandService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductRequest request){
        ProductResponse response = productCommandService.create(request);
        BaseResponseAPI<ProductResponse> responseAPI = new BaseResponseAPI<>(true,"Create product successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody ProductRequest request,@PathVariable Long id){
        ProductResponse response = productCommandService.update(request, id);
        BaseResponseAPI<ProductResponse> responseAPI = new BaseResponseAPI<>(true,"Update product successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        productCommandService.delete(id);
        BaseResponseAPI<?> responseAPI = new BaseResponseAPI<>(true,"Delete product successfully",null,null);
        return ResponseEntity.ok(responseAPI);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id){
        ProductResponse response = productQueryService.getProductById(id);
        BaseResponseAPI<ProductResponse> responseAPI = new BaseResponseAPI<>(true,"Get product id: "+ id,response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        PageResponse<ProductResponse> pageResponse = productQueryService.findAll(page, size, sortBy, sortDir);
        BaseResponseAPI<PageResponse<ProductResponse>> responseAPI = new BaseResponseAPI<>(true,"Get all product",pageResponse,null);
        return ResponseEntity.ok(responseAPI);
    }
}
