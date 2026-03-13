package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.domain.entity.RestaurantTable;
import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.RestaurantTableRepository;
import com.restaurante.backend.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository tableRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public RestaurantTable createTable(Long branchId, RestaurantTable table) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Branch not found"));
        table.setBranch(branch);
        return tableRepository.save(table);
    }

    @Override
    @Transactional
    public RestaurantTable updateTable(Long id, RestaurantTable tableDetails) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Table not found"));

        table.setNumber(tableDetails.getNumber());
        table.setCapacity(tableDetails.getCapacity());

        return tableRepository.save(table);
    }

    @Override
    public Optional<RestaurantTable> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    @Override
    public List<RestaurantTable> getTablesByBranchId(Long branchId) {
        return tableRepository.findByBranchId(branchId);
    }

    @Override
    @Transactional
    public RestaurantTable updateTableStatus(Long id, String status) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Table not found"));
        table.setStatus(status);
        return tableRepository.save(table);
    }

    @Override
    @Transactional
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }
}
