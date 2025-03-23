package com.warba.assessment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "STATUSES", schema = "WARBA")
public class Status {

    @Id
    private Long id;

    @NotBlank(message = "Status name is required")
    @Column(name = "NAME", unique = true)
    @Enumerated(EnumType.STRING)
    private Statuses name;

    @Column(name = "DESCRIPTION")
    private String description;

    public enum Statuses {
        DRAFT, IN_PROGRESS, DONE, CANCELLED, SUBMITTED
    }
}
