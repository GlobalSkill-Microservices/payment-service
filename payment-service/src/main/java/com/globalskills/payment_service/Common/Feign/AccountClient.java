package com.globalskills.payment_service.Common.Feign;

import com.globalskills.payment_service.Common.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name="user-service", url = "https://gateway-service-w2gi.onrender.com/api/user}")
public interface AccountClient {

    @GetMapping("/{id}")
    AccountDto getAccountById(@PathVariable Long id);

    @GetMapping("/batch")
    List<AccountDto> getAccountByIds(@RequestParam("ids") Set<Long> ids);
}
