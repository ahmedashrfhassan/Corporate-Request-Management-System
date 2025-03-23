package com.warba.assessment.repository;

import com.warba.assessment.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    Status findByName(Status.Statuses name);
}