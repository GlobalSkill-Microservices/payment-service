package com.globalskills.payment_service.Common;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
                log.debug("Forwarding Authorization header to Feign client: {}",
                        authorizationHeader.substring(0, Math.min(20, authorizationHeader.length())) + "...");
                template.header(AUTHORIZATION_HEADER, authorizationHeader);
            } else {
                log.warn("No valid Authorization header found in request to forward");
            }
        } else {
            log.warn("No request attributes available to extract Authorization header");
        }
    }
}
