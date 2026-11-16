package com.awd.monolith.candidat.service;

import com.awd.monolith.candidat.dto.AddressRequest;
import com.awd.monolith.candidat.dto.AddressResponse;
import com.awd.monolith.candidat.entity.Address;

import java.util.List;

/**
 * Interface de service pour la gestion métier des adresses.
 */
public interface IAddressService {

    List<AddressResponse> findAll();

    AddressResponse findById(Long id);

    AddressResponse create(AddressRequest request);

    AddressResponse update(Long id, AddressRequest request);

    void delete(Long id);

    default void deleteById(Long id) {
        delete(id);
    }

    Address getAddress(Long id);
}

