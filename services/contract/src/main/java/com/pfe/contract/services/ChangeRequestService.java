package com.pfe.contract.services;

import com.pfe.contract.dtos.ChangeRequestCreateDto;
import com.pfe.contract.dtos.ChangeRequestResponseDto;

import java.util.List;

public interface ChangeRequestService {
    ChangeRequestResponseDto submitRequest(String requesterId, ChangeRequestCreateDto dto);
    List<ChangeRequestResponseDto> listMyRequests(String requesterId);
    List<ChangeRequestResponseDto> listPending();
    ChangeRequestResponseDto approve(Long requestId, String approverId);
    ChangeRequestResponseDto reject(Long requestId, String approverId);
}


