package org.project.projectstep1zanix.file;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String uploadDir = "uploads";
    private final List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
    private final long maxFileSize = 5 * 1024 * 1024; // 5MB

    @Override
    public String saveFile(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds 5MB limit.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);

        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file format. Only jpg, jpeg, and png are allowed.");
        }

        String newFilename = UUID.randomUUID().toString() + "." + extension;
        Path targetLocation = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();

        try {
            if (!Files.exists(targetLocation)) {
                Files.createDirectories(targetLocation);
            }
            Path filePath = targetLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/" + uploadDir + "/" + subDirectory + "/" + newFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + newFilename + ". Please try again!", ex);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }
            Path fileToDeletePath = Paths.get(filePath).toAbsolutePath().normalize();
            try {
                if (Files.exists(fileToDeletePath)) {
                    Files.delete(fileToDeletePath);
                }
            } catch (IOException ex) {
                System.err.println("Could not delete file " + filePath + ": " + ex.getMessage());
               
            }
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}

