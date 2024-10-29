package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "Party_Mode")
@Data
public class PartyMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id", nullable = false)
    private Integer partyId;
    @Column(name = "party_name")
    private String party_name;
    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;
    private Date created_at;
}