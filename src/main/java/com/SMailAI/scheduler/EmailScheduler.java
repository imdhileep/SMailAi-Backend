package com.SMailAI.scheduler;

import com.SMailAI.model.EmailSchedule;
import com.SMailAI.repository.EmailScheduleRepository;
import com.SMailAI.service.EmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EmailScheduler {

	private final EmailScheduleRepository repository;
	private final EmailService emailService;
	private static final Logger log = LoggerFactory.getLogger(EmailScheduler.class);

	public EmailScheduler(EmailScheduleRepository repository, EmailService emailService) {
		this.repository = repository;
		this.emailService = emailService;
	}

	@Scheduled(fixedRate = 60000) // Every 60 seconds
	public void sendScheduledEmails() {
		List<EmailSchedule> pendingEmails = repository.findByScheduledTimeBeforeAndSentFalse(LocalDateTime.now());

		for (EmailSchedule email : pendingEmails) {
			int attempts = 0;
			boolean sent = false;

			while (attempts < 3 && !sent) {
				try {
					emailService.sendEmail(email);
					email.setSent(true);
					repository.save(email);
					log.info("✅ Email sent to: {}", email.getRecipientEmail());
					sent = true;
				} catch (Exception e) {
					attempts++;
					log.error("❌ Attempt {} failed for: {}", attempts, email.getRecipientEmail(), e);
					try {
						Thread.sleep(2000); // Wait 2 seconds before retry
					} catch (InterruptedException ignored) {
					}
				}
			}

		}
	}
}