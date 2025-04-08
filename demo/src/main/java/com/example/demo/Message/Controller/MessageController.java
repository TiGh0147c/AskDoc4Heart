package com.example.demo.Message.Controller;

import com.example.demo.Message.DTO.*;
import com.example.demo.Message.Service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/sessions/{sessionId}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Result<Integer>> sendMessage(
            @PathVariable Integer sessionId,
            @Valid @RequestBody MessageSendDTO sendDTO) {

        Integer messageId = messageService.sendMessage(sessionId, sendDTO);
        return ResponseEntity.ok(Result.success(messageId));
    }

    @GetMapping
    public ResponseEntity<PageResult<MessageVO>> getMessageHistory(
            @PathVariable Integer sessionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageQuery pageQuery = new PageQuery(page, size);
        return ResponseEntity.ok(messageService.getMessageHistory(sessionId, pageQuery));
    }

    @PostMapping("/upload")
    public ResponseEntity<Result<FileUploadResult>> uploadFile(
            @PathVariable Integer sessionId,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(Result.success(messageService.uploadFile(sessionId, file)));
    }
}
