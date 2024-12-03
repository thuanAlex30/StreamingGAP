package com.fpt.StreamGAP.entity;

import com.fpt.StreamGAP.entity.Enum.CodeTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class VerifyCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String code;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "type")
    private CodeTypeEnum codeType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.codeType == null) {
            throw new IllegalArgumentException("Code type cannot be null");
        }

        switch (this.codeType) {
            case REGISTER:
                this.expireAt = createdAt.plusMinutes(5);
                break;
            case FORGET_PASS:
                this.expireAt = createdAt.plusMinutes(10);
                break;
            default:
                this.expireAt = createdAt.plusMinutes(5); // Default expiration time for unhandled cases
                break;
        }
        System.out.println(VerifyCode.this.toString());
    }
}
