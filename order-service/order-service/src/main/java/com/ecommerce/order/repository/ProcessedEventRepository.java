package com.ecommerce.order.repository;

import com.ecommerce.order.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
