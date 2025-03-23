package com.warba.assessment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "REQUESTS", schema = "WARBA")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUEST_SEQ")
    @SequenceGenerator(name = "REQUEST_SEQ", sequenceName = "WARBA.REQUEST_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Request name is required")
    @Column(name = "REQUEST_NAME")
    private String requestName;

    @ManyToOne
    @JoinColumn(name = "STATUS_ID")
    @NotNull(message = "Status is required")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    @NotNull(message = "User is required")
    private User owner;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}