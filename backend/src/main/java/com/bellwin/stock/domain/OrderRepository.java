package com.bellwin.stock.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findTop20ByAccountIdOrderByCreatedAtDesc(Long accountId);
}
