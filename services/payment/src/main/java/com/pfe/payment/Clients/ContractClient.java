package com.pfe.payment.Clients;

import com.pfe.payment.dtos.ContractDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "contract", path = "/api/contracts")
public interface ContractClient {

    @GetMapping("/{id}")
    ContractDto getContract(@PathVariable("id") String id);
}