package com.vincent.model;

import com.vincent.enums.Coin;
import com.vincent.enums.OrderAction;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "orders")
public class MemberOrder {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "sn", length = 64, nullable = false, unique = true)
    private String sn;

    @Column(name = "uid", length = 64, nullable = false, unique = true)
    private String uid;

    @Column(name = "action", columnDefinition = "VARCHAR(16) NOT NULL")
    private OrderAction action;

    @Column(name = "coin", columnDefinition = "VARCHAR(16) NOT NULL")
    private Coin coin;

    @Column(name = "unit_price", precision = 8, scale = 4, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "volume", precision = 8, scale = 4, nullable = false)
    private BigDecimal volume;

    @Column(name = "origin_volume", precision = 8, scale = 4, nullable = false)
    private BigDecimal originVolume;

    @Column(name = "state", columnDefinition = "VARCHAR(16) NOT NULL")
    private OrderState state;

    @Column(name = "category", columnDefinition = "VARCHAR(16) NOT NULL")
    private OrderCategory category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;


}
