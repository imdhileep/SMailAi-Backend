package com.SMailAI.controller;

import com.SMailAI.repository.EmailScheduleRepository;
import com.SMailAI.model.EmailSchedule;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "http://localhost:3001")
public class EmailStatsController {

    private final EmailScheduleRepository repository;

    public EmailStatsController(EmailScheduleRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/status-count")
    public Map<String, Long> getStatusCount() {
        List<EmailSchedule> emails = repository.findAll();
        long sent = emails.stream().filter(EmailSchedule::isSent).count();
        long pending = emails.size() - sent;

        Map<String, Long> result = new HashMap<>();
        result.put("sent", sent);
        result.put("pending", pending);
        return result;
    }

    @GetMapping("/daily-counts")
    public List<Map<String, Object>> getDailyCounts() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<EmailSchedule> emails = repository.findAll().stream()
            .filter(e -> !e.getScheduledTime().toLocalDate().isBefore(weekAgo))
            .collect(Collectors.toList());

        Map<LocalDate, Map<String, Long>> dailyCounts = new TreeMap<>();

        for (int i = 0; i <= 6; i++) {
            LocalDate date = weekAgo.plusDays(i);
            dailyCounts.put(date, new HashMap<>(Map.of("sent", 0L, "pending", 0L)));
        }

        for (EmailSchedule email : emails) {
            LocalDate date = email.getScheduledTime().toLocalDate();
            String status = email.isSent() ? "sent" : "pending";
            dailyCounts.get(date).merge(status, 1L, Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, Long>> entry : dailyCounts.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", entry.getKey());
            map.put("sent", entry.getValue().get("sent"));
            map.put("pending", entry.getValue().get("pending"));
            result.add(map);
        }

        return result;
    }
}
