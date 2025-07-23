// repository/EmailScheduleRepository.java
package com.SMailAI.repository;

import com.SMailAI.model.EmailSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailScheduleRepository
		extends JpaRepository<EmailSchedule, Long>, JpaSpecificationExecutor<EmailSchedule> {
	List<EmailSchedule> findByScheduledTimeBeforeAndSentFalse(LocalDateTime now);

	List<EmailSchedule> findAllByOrderByScheduledTimeDesc();
	
    long count();  // already exists by default
    
    long countBySentTrue();
    
    long countBySentFalse();

}
