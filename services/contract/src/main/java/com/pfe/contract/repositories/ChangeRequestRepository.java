package com.pfe.contract.repositories;

import com.pfe.contract.entities.ChangeRequest;
import com.pfe.contract.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findByContract(Contract contract);
    List<ChangeRequest> findByRequesterId(String requesterId);
    List<ChangeRequest> findByStatus(String status);
}



