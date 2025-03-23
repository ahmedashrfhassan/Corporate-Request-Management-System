package com.warba.assessment.repository;

import com.warba.assessment.entity.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentTypeRepository extends JpaRepository<AttachmentType, Long> {
    Optional<AttachmentType> findByName(String name);
}