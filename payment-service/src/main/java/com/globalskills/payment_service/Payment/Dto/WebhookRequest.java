package com.globalskills.payment_service.Payment.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebhookRequest {

    @JsonProperty("gateway")
    private String gateway;

    @JsonProperty("transactionDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("subAccount")
    private String subAccount;

    @JsonProperty("code")
    private String code;

    @JsonProperty("content")
    private String content;

    @JsonProperty("transferType")
    private String transferType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("transferAmount")
    private long transferAmount;

    @JsonProperty("referenceCode")
    private String referenceCode;

    @JsonProperty("accumulated")
    private long accumulated;

    @JsonProperty("id")
    private long id;

}
