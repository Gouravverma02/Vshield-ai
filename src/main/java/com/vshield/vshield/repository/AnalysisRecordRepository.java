package com.vshield.vshield.repository;

import com.vshield.vshield.model.AnalysisRecord;
import com.vshield.vshield.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRecordRepository extends JpaRepository<AnalysisRecord, Long> {

    List<AnalysisRecord> findByUserOrderByCreatedAtDesc(User user);

    Optional<AnalysisRecord> findByIdAndUser(Long id, User user);
}