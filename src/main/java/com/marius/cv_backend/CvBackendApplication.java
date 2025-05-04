package com.marius.cv_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import java.io.File;
import org.springframework.http.HttpHeaders;

@SpringBootApplication
public class CvBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvBackendApplication.class, args);
    }
}

@RestController
class RootController {

    @GetMapping("/")
    public String root() {
        return "Hello world";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/api/subpath1")
    public String subpath1() {
        return "This is Subpath 1";
    }

    @GetMapping("/api/subpath2")
    public String subpath2() {
        return "This is Subpath 2";
    }

 
    @GetMapping("/api/video")
    public ResponseEntity<Resource> streamVideo() {
        // Path to the local video file
        String videoPath = "/media/marius/DISK GROS/reels/BABEL/cahnt.mp4"; // Replace with the actual path to your video file
        File videoFile = new File(videoPath);

        if (!videoFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Load the video file as a resource
        FileSystemResource resource = new FileSystemResource(videoFile);

        // Set HTTP headers for streaming
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", videoFile.getName());

        // Return the video file as a stream
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(videoFile.length())
                .body(resource);
    }
}