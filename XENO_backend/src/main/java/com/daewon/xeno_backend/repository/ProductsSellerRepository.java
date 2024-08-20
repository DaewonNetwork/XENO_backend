package com.daewon.xeno_backend.repository;

import com.daewon.xeno_backend.domain.ProductsSeller;
import com.daewon.xeno_backend.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsSellerRepository extends JpaRepository<ProductsSeller, Long>{

    List<ProductsSeller> findByUsers(Users users);


}
