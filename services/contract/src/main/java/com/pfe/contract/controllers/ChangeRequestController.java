package com.pfe.contract.controllers;

import com.pfe.contract.dtos.ChangeRequestCreateDto;
import com.pfe.contract.dtos.ChangeRequestResponseDto;
import com.pfe.contract.services.ChangeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/change-requests")
@RequiredArgsConstructor
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;

    // CLIENT: submit a change request
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ChangeRequestResponseDto> submit(Authentication authentication,
                                                           @RequestBody ChangeRequestCreateDto dto) {
        String requesterId = authentication.getName();
        return ResponseEntity.ok(changeRequestService.submitRequest(requesterId, dto));
    }

    // CLIENT: list my requests
    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ChangeRequestResponseDto>> myRequests(Authentication authentication) {
        String requesterId = authentication.getName();
        return ResponseEntity.ok(changeRequestService.listMyRequests(requesterId));
    }

    // ADMIN/INSURER: list pending
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','INSURER')")
    public ResponseEntity<List<ChangeRequestResponseDto>> pending() {
        return ResponseEntity.ok(changeRequestService.listPending());
    }

    // ADMIN/INSURER: approve
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','INSURER')")
    public ResponseEntity<ChangeRequestResponseDto> approve(@PathVariable Long id, Authentication authentication) {
        String approverId = authentication.getName();
        return ResponseEntity.ok(changeRequestService.approve(id, approverId));
    }

    // ADMIN/INSURER: reject
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','INSURER')")
    public ResponseEntity<ChangeRequestResponseDto> reject(@PathVariable Long id, Authentication authentication) {
        String approverId = authentication.getName();
        return ResponseEntity.ok(changeRequestService.reject(id, approverId));
    }
}




