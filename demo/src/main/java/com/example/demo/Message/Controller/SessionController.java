package com.example.demo.Message.Controller;

import com.example.demo.Message.DTO.*;
import com.example.demo.Message.Service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<PageResult<SessionVO>> getSessionList(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer counselorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        SessionQueryDTO queryDTO = new SessionQueryDTO();
        queryDTO.setUserId(userId);
        queryDTO.setCounselorId(counselorId);
        queryDTO.setStatus(status);

        PageQuery pageQuery = new PageQuery(page, size);
        return ResponseEntity.ok(sessionService.getSessionList(queryDTO, pageQuery));
    }

    @PostMapping
    public ResponseEntity<Result<Integer>> createSession(
            @Valid @RequestBody SessionCreateDTO createDTO) {

        Integer sessionId = sessionService.createSession(createDTO);
        return ResponseEntity.ok(Result.success(sessionId));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<Result<SessionDetailVO>> getSessionDetail(
            @PathVariable Integer sessionId) {

        return ResponseEntity.ok(Result.success(sessionService.getSessionDetail(sessionId)));
    }

    @PutMapping("/{sessionId}/start")
    public ResponseEntity<Result<Void>> startSession(
            @PathVariable Integer sessionId,
            @RequestParam Integer counselorId) {

        sessionService.startSession(sessionId, counselorId);
        return ResponseEntity.ok(Result.success());
    }

    @PutMapping("/{sessionId}/end")
    public ResponseEntity<Result<Void>> endSession(
            @PathVariable Integer sessionId,
            @RequestBody SessionEndDTO endDTO) {

        sessionService.endSession(sessionId, endDTO.getSummaryText());
        return ResponseEntity.ok(Result.success());
    }
}