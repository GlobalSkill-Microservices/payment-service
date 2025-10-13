package com.globalskills.payment_service.Payment.Controller;

import com.globalskills.payment_service.Common.BaseResponseAPI;
import com.globalskills.payment_service.Common.PageResponse;
import com.globalskills.payment_service.Payment.Dto.WalletRequest;
import com.globalskills.payment_service.Payment.Dto.WalletResponse;
import com.globalskills.payment_service.Payment.Service.WalletCommandService;
import com.globalskills.payment_service.Payment.Service.WalletQueryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class WalletController {

    @Autowired
    WalletQueryService walletQueryService;

    @Autowired
    WalletCommandService walletCommandService;

    @PostMapping
    public ResponseEntity<?> create(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-ID",required = false) Long accountId){
        WalletResponse response = walletCommandService.create(accountId);
        BaseResponseAPI<WalletResponse> responseAPI = new BaseResponseAPI<>(true,"Create wallet successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> update(@RequestBody WalletRequest request,@PathVariable Long accountId){
        WalletResponse response = walletCommandService.update(request, accountId);
        BaseResponseAPI<WalletResponse> responseAPI = new BaseResponseAPI<>(true,"Update wallet successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getWalletByAccountId(
            @RequestParam(required = false) Long accountId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-ID",required = false) Long currentAccountId
    ){
        Long id = (accountId != null) ? accountId : currentAccountId;
        WalletResponse response = walletQueryService.getWalletByAccountId(id);
        BaseResponseAPI<WalletResponse> responseAPI = new BaseResponseAPI<>(true,"Get wallet successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        PageResponse<WalletResponse> pageResponse = walletQueryService.getAll(page, size, sortBy, sortDir);
        BaseResponseAPI<PageResponse<WalletResponse>> responseAPI = new BaseResponseAPI<>(true,"Get all wallet successfully",pageResponse,null);
        return ResponseEntity.ok(responseAPI);
    }
}
