package com.example.cms.user.domain.repository;

import com.example.cms.user.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByIdAndEmail(Long id, String email);

    Optional<Seller> findByEmailAndPasswordAndVerifyIsTrue(String email, String password);

    Optional<Seller> findByEmail(String email);
}
