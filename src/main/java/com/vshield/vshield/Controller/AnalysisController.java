package com.vshield.vshield.Controller;

import com.vshield.vshield.dto.AnalyzeRequest;
import com.vshield.vshield.model.AnalysisRecord;
import com.vshield.vshield.model.AnalysisResult;
import com.vshield.vshield.model.User;
import com.vshield.vshield.repository.AnalysisRecordRepository;
import com.vshield.vshield.repository.UserRepository;
import com.vshield.vshield.service.DetectionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    private static final String DELIMITER = "|||";

    private final DetectionService detectionService;
    private final AnalysisRecordRepository analysisRecordRepository;
    private final UserRepository userRepository;

    public AnalysisController(DetectionService detectionService,
                              AnalysisRecordRepository analysisRecordRepository,
                              UserRepository userRepository) {
        this.detectionService = detectionService;
        this.analysisRecordRepository = analysisRecordRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@Valid @RequestBody AnalyzeRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        User user = userOptional.get();

        AnalysisResult result = detectionService.analyze(request.getText());

        String reasonsJoined = String.join(DELIMITER, result.getReasons());
        String nextStepsJoined = String.join(DELIMITER, result.getNextSteps());

        AnalysisRecord record = new AnalysisRecord(
                user,
                result.getVerdict().toString(),
                result.getRiskScore(),
                request.getText(),
                reasonsJoined,
                nextStepsJoined
        );
        analysisRecordRepository.save(record);

        Map<String, Object> response = new HashMap<>();
        response.put("id", record.getId());
        response.put("riskScore", result.getRiskScore());
        response.put("verdict", result.getVerdict().toString());
        response.put("reasons", result.getReasons());
        response.put("nextSteps", result.getNextSteps());

        return ResponseEntity.ok(response);
    }
}