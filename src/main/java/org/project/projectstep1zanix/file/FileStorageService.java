package org.project.projectstep1zanix.file;


import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveFile(MultipartFile file, String subDirectory);
    void deleteFile(String filePath);
}

