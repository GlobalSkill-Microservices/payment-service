package com.globalskills.payment_service.Payment.Repository;

import com.globalskills.payment_service.Payment.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {
}
