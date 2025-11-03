package com.globalskills.payment_service.Common.Feign;

import com.globalskills.payment_service.Common.Dto.BookingStatusDto;
import com.globalskills.payment_service.Common.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="booking-service", url = "https://gateway-service-w2gi.onrender.com/api/booking-client",configuration = FeignClientInterceptor.class)
public interface BookingClient {

    @PostMapping("/booking/status")
    void BookingStatus(@RequestBody BookingStatusDto request);

}
