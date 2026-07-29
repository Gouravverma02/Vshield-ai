package com.vshield.vshield.Controller;

import com.vshield.vshield.model.AnalysisRecord;
import com.vshield.vshield.model.User;
import com.vshield.vshield.repository.AnalysisRecordRepository;
import com.vshield.vshield.repository.UserRepository;
import com.vshield.vshield.util.SessionUserHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final AnalysisRecordRepository analysisRecordRepository;
    private final UserRepository userRepository;

    public HistoryController(AnalysisRecordRepository analysisRecordRepository,
                             UserRepository userRepository) {
        this.analysisRecordRepository = analysisRecordRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getHistory(HttpSession session) {
        Optional<User> userOptional = SessionUserHelper.getSessionUser(session, userRepository);
        if (userOptional.isEmpty()) {
            return unauthenticated();
        }
        User user = userOptional.get();

        List<AnalysisRecord> records = analysisRecordRepository.findByUserOrderByCreatedAtDesc(user);

        List<Map<String, Object>> response = new ArrayList<>();
        for (AnalysisRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("verdict", record.getVerdict());
            item.put("riskScore", record.getRiskScore());
            item.put("textPreview", truncate(record.getOriginalText(), 80));
            item.put("createdAt", record.getCreatedAt());
            response.add(item);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHistoryDetail(@PathVariable Long id, HttpSession session) {
        Optional<User> userOptional = SessionUserHelper.getSessionUser(session, userRepository);
        if (userOptional.isEmpty()) {
            return unauthenticated();
        }
        User user = userOptional.get();

        Optional<AnalysisRecord> recordOptional = analysisRecordRepository.findByIdAndUser(id, user);
        if (recordOptional.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Analysis record not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        AnalysisRecord record = recordOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", record.getId());
        response.put("verdict", record.getVerdict());
        response.put("riskScore", record.getRiskScore());
        response.put("originalText", record.getOriginalText());
        response.put("reasons", splitOrEmpty(record.getReasons()));
        response.put("nextSteps", splitOrEmpty(record.getNextSteps()));
        response.put("createdAt", record.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> unauthenticated() {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private List<String> splitOrEmpty(String joined) {
        if (joined == null || joined.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.asList(joined.split("\\|\\|\\|"));
    }
}