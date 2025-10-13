package com.globalskills.payment_service.Common;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse <T>{
    List<T> Content;
    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean last;

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        Content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}