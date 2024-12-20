package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subscription")
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // Base hoặc Premium

    @Column(nullable = false)
    private Long price; // Giá gói

    @Column(nullable = false)
    private String description; // Mô tả
}
