package com.pfe.contract.clients;

import com.pfe.contract.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user", path = "/api/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") String id);
}