package com.awd.monolith.candidat.service;

import com.awd.monolith.candidat.dto.AddressRequest;
import com.awd.monolith.candidat.dto.AddressResponse;
import com.awd.monolith.candidat.entity.Address;
import com.awd.monolith.candidat.mapper.CandidatMapper;
import com.awd.monolith.candidat.repository.IAddressRepository;
import com.awd.monolith.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service métier des adresses.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final IAddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> findAll() {
        return addressRepository.findAll().stream().map(CandidatMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse findById(Long id) {
        return CandidatMapper.toResponse(getAddress(id));
    }

    @Override
    public AddressResponse create(AddressRequest request) {
        return CandidatMapper.toResponse(addressRepository.save(CandidatMapper.toEntity(request)));
    }

    @Override
    public AddressResponse update(Long id, AddressRequest request) {
        Address address = getAddress(id);
        CandidatMapper.updateEntity(address, request);
        return CandidatMapper.toResponse(address);
    }

    @Override
    public void delete(Long id) {
        Address address = getAddress(id);
        if (address.getCandidate() != null) {
            // unlink first so the candidate's foreign key does not point to a deleted row
            address.getCandidate().setAddress(null);
        }
        addressRepository.delete(address);
    }

    @Override
    public Address getAddress(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", id));
    }
}

