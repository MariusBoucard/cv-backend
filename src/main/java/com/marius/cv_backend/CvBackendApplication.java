package com.marius.cv_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    public ResponseEntity<InputStreamResource> streamVideo(
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        // Path to the local video file
        String videoPath = "/media/marius/DISK GROS/reels/BABEL/cahnt.mp4"; 
        File videoFile = new File(videoPath);

        if (!videoFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        long fileLength = videoFile.length();
        long start = 0;
        long end = fileLength - 1;

        // Parse the Range header if present
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
            if (!ranges.isEmpty()) {
                HttpRange range = ranges.get(0);
                start = range.getRangeStart(fileLength);
                end = range.getRangeEnd(fileLength);
            }
        }

        // Set the content length for the requested range
        long contentLength = end - start + 1;

        // Open an InputStream for the requested range
        InputStream inputStream = new FileInputStream(videoFile);
        inputStream.skip(start);

        // Set HTTP headers for partial content
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "video/mp4");
        headers.add("Accept-Ranges", "bytes");
        headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);

        // Return the partial content response
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .contentLength(contentLength)
                .body(new InputStreamResource(inputStream));
    }
}