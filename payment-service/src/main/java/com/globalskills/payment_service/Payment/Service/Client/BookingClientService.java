package com.globalskills.payment_service.Payment.Service.Client;

import com.globalskills.payment_service.Common.Dto.BookingStatusDto;
import com.globalskills.payment_service.Common.Feign.BookingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingClientService {

    @Autowired
    BookingClient bookingClient;

    public void BookingStatus(BookingStatusDto request){
        bookingClient.BookingStatus(request);
    }
}
