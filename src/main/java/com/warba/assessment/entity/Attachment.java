package com.warba.assessment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ATTACHMENTS", schema = "WARBA")
public class Attachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ATTACHMENT_SEQ")
    @SequenceGenerator(name = "ATTACHMENT_SEQ", sequenceName = "WARBA.ATTACHMENT_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank(message = "File name is required")
    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_TYPE")
    private String fileType;

    @Column(name = "UPLOAD_DATE_TIME")
    private LocalDateTime uploadDateTime = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "ATTACHMENT_TYPE")
    @NotNull(message = "Attachment type is required")
    private AttachmentType attachmentType;
    
    @ManyToOne
    @JoinColumn(name = "REQUEST_ID")
    private Request request;

}