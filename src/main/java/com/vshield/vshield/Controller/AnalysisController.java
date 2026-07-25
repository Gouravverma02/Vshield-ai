package com.vshield.vshield.Controller;

import com.vshield.vshield.dto.AnalyzeRequest;
import com.vshield.vshield.model.AnalysisResult;
import com.vshield.vshield.service.DetectionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final DetectionService detectionService;

    public AnalysisController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@Valid @RequestBody AnalyzeRequest request, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        AnalysisResult result = detectionService.analyze(request.getText());

        Map<String, Object> response = new HashMap<>();
        response.put("riskScore", result.getRiskScore());
        response.put("verdict", result.getVerdict().toString());
        response.put("reasons", result.getReasons());
        response.put("nextSteps", result.getNextSteps());

        return ResponseEntity.ok(response);
    }
}