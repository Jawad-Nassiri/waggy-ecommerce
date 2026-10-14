package com.waggy.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final Path uploadDirectory = Paths.get("uploads");

    public String saveImage(MultipartFile image, String categoryName) throws IOException {
        Path categoryDirectory = uploadDirectory.resolve(categoryName.toLowerCase());
        Files.createDirectories(categoryDirectory);

        String fileName = image.getOriginalFilename();
        Path filePath = categoryDirectory.resolve(fileName);

        Files.copy(image.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        return "uploads/" + categoryName.toLowerCase() + "/" + fileName;
    }

    public void deleteImage(String imagePath) throws IOException {
        if (imagePath == null || !imagePath.startsWith("uploads/")) {
            return;
        }

        Path filePath = Paths.get(imagePath).normalize();

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
