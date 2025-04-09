package com.example.demo.Message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResult {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private long fileSize;
}
