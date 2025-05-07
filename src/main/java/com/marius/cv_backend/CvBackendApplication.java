package com.marius.cv_backend;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

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
@CrossOrigin(origins = "*")
class RootController {

    @GetMapping("/")
    public String root() {
        return "Hello world";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
 
    @GetMapping("/api/video")
    public ResponseEntity<InputStreamResource> streamVideo(
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        String videoPath = "static/videos/ATWA.mp4";
        Resource videoResource = new ClassPathResource(videoPath);

        if (!videoResource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        long fileLength = videoResource.contentLength();
        long start = 0;
        long end = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
            if (!ranges.isEmpty()) {
                HttpRange range = ranges.get(0);
                start = range.getRangeStart(fileLength);
                end = range.getRangeEnd(fileLength);
            }
        }

        long contentLength = end - start + 1;

        InputStream inputStream = videoResource.getInputStream();
        inputStream.skip(start);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "video/mp4");
        headers.add("Accept-Ranges", "bytes");
        headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .contentLength(contentLength)
                .body(new InputStreamResource(inputStream));
    }


    @GetMapping("/api/cvPDF")
    public ResponseEntity<Resource> getCvPDF() throws IOException {
        // Load the PDF file from the classpath
        String pdfPath = "static/cv.pdf";
        Resource resource = new ClassPathResource(pdfPath);

        if (!resource.exists()) {
            System.err.println("PDF file not found: " + pdfPath);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", resource.getFilename());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }
   
    @GetMapping("/api/audio")
    public ResponseEntity<InputStreamResource> streamAudio(
            @RequestParam String name, 
            @RequestParam String type
    ) throws IOException {
        String audioPath = "static/audio/" + type + "/" + name + ".wav";

        Resource audioResource = new ClassPathResource(audioPath);

        System.err.println("Audio file path: " + audioPath);
        System.err.println("Audio file exists: " + audioResource.exists());

        if (!audioResource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.add("Accept-Ranges", "bytes");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(audioResource.contentLength())
                .body(new InputStreamResource(audioResource.getInputStream()));
    }
}