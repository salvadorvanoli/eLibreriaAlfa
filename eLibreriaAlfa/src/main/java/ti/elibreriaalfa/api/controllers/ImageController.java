package ti.elibreriaalfa.api.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ti.elibreriaalfa.services.ImageService;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping(value = "image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/{relativePath:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable (name = "relativePath") String relativePath) {
        try {
            Resource resource = imageService.loadImage(relativePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(resource.getFile().toPath())))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
