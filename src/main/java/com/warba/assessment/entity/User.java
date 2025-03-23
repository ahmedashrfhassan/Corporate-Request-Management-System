package com.warba.assessment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USERS", schema = "WARBA")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    @SequenceGenerator(name = "USER_SEQ", sequenceName = "WARBA.USER_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank(message = ": Name is required")
    @Column(name = "NAME")
    private String name;

    @NotBlank(message = "Civil ID is required")
    @Column(name = "CIVIL_ID", unique = true)
    private String civilId;

    @NotNull(message = ": Expiry date is required")
    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column(name = "IS_DELETED", nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Request> requests;

    public boolean isCivilIdExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
}