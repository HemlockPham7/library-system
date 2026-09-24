package com.librarysystem.userservice.repository;

import com.librarysystem.userservice.dto.identity.TokenExchangeParam;
import com.librarysystem.userservice.dto.identity.TokenExchangeResponse;
import com.librarysystem.userservice.dto.identity.UserCreationParam;
import com.librarysystem.userservice.dto.identity.UserTokenExchangeParam;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {

    @PostMapping(
            value = "/realms/librarysystem/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenExchangeResponse exchangeClientToken(@QueryMap() TokenExchangeParam param);

    @PostMapping(
            value = "admin/realms/librarysystem/users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> createUser(@RequestBody() UserCreationParam body, @RequestHeader("authorization") String token);

    @PostMapping(value = "realms/librarysystem/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeUserToken(@QueryMap UserTokenExchangeParam param);
}