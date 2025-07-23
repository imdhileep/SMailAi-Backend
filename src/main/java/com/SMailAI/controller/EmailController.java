package com.SMailAI.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import com.SMailAI.model.EmailSchedule;
import com.SMailAI.repository.EmailScheduleRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/api")
public class EmailController {

	private final EmailScheduleRepository repository;

	public EmailController(EmailScheduleRepository repository) {
		this.repository = repository;
	}

	@PostMapping("/schedule")
	public ResponseEntity<String> scheduleEmail(@RequestBody EmailSchedule email) {
		repository.save(email); // Save and trigger scheduling logic
		return ResponseEntity.ok("Email scheduled successfully!");
	}
	
	@DeleteMapping("/history/{id}")
	public ResponseEntity<String> deleteEmail(@PathVariable Long id) {
	    if (repository.existsById(id)) {
	        repository.deleteById(id);
	        return ResponseEntity.ok("Email deleted successfully");
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("/summary")
	public Map<String, Long> getSummary() {
	    Map<String, Long> summary = new HashMap<>();
	    summary.put("total", repository.count());
	    summary.put("sent", repository.countBySentTrue());     // ✅ add this line
	    summary.put("pending", repository.countBySentFalse()); // ✅ add this line
	    return summary;
	}

	@GetMapping(value = "/export/csv", produces = "text/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
	    response.setContentType("text/csv");
	    response.setHeader("Content-Disposition", "attachment; filename=email_history.csv");

	    List<EmailSchedule> emails = repository.findAll();

	    PrintWriter writer = response.getWriter();
	    writer.println("Recipient,Subject,Body,Scheduled Time,Sent");

	    for (EmailSchedule email : emails) {
	        writer.printf("%s,%s,%s,%s,%s%n",
	                email.getRecipientEmail(),
	                email.getSubject().replace(",", " "),  // prevent CSV column break
	                email.getBody().replace(",", " "),
	                email.getScheduledTime(),
	                email.isSent() ? "Sent" : "Pending");
	    }

	    writer.flush();
	    writer.close();
	}


	@GetMapping("/history")
	public Page<EmailSchedule> getFilteredHistory( // ← not List
			@RequestParam(required = false) String recipient, @RequestParam(required = false) Boolean sent,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

		Pageable pageable = PageRequest.of(page, size);

		return repository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (recipient != null && !recipient.isEmpty()) {
				predicates.add(cb.equal(root.get("recipientEmail"), recipient));
			}
			if (sent != null) {
				predicates.add(cb.equal(root.get("sent"), sent));
			}
			if (startDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("scheduledTime"), startDate));
			}
			if (endDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("scheduledTime"), endDate));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		}, pageable);
	}

}
