package com.vincent.web.repository;

import com.vincent.model.MemberOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOrderRepository extends JpaRepository<MemberOrder, Long> {
}
