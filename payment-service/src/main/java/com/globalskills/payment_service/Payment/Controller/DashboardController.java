package com.globalskills.payment_service.Payment.Controller;

import com.globalskills.payment_service.Common.BaseResponseAPI;
import com.globalskills.payment_service.Common.PageResponse;
import com.globalskills.payment_service.Payment.Dto.DailyRevenueResponse;
import com.globalskills.payment_service.Payment.Dto.ProductPerformanceResponse;
import com.globalskills.payment_service.Payment.Service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@SecurityRequirement(name = "api")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/daily-revenue")
    public ResponseEntity<?> getDailyRevenue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        PageResponse<DailyRevenueResponse> response = dashboardService.getDailyRevenue(page, size);
        BaseResponseAPI<PageResponse<DailyRevenueResponse>> responseAPI = new BaseResponseAPI<>(true,"Get daily revenue successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @GetMapping("/product-performance")
    public ResponseEntity<?> getProductPerformance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        PageResponse<ProductPerformanceResponse> response = dashboardService.getProductPerformance(page, size);
        BaseResponseAPI<PageResponse<ProductPerformanceResponse>> responseAPI = new BaseResponseAPI<>(true,"Get product performance successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @GetMapping("/transaction-statistics")
    public ResponseEntity<?> getTransactionStatistics(){
        Map<String, Object> response = dashboardService.getTransactionStatistics();
        BaseResponseAPI<Map<String, Object>> responseAPI = new BaseResponseAPI<>(true,"Get transactional statistics successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }

}
