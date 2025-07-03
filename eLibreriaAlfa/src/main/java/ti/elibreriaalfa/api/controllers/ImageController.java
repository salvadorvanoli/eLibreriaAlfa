package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ti.elibreriaalfa.exceptions.image.ImageNotFoundException;
import ti.elibreriaalfa.services.ImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Obtener una imagen por su ruta relativa")
    @GetMapping("/**")
    public ResponseEntity<Object> getImage(HttpServletRequest request) {
        try {
            String relativePath = extractRelativePath(request);

            if (relativePath.isEmpty()) {
                return ResponseEntity.badRequest().body("Ruta vacía");
            }

            Resource resource = imageService.loadImage(relativePath);  // Puede lanzar excepción

            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("Cache-Control", "public, max-age=86400")
                    .body(resource);
        } catch (ImageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Imagen no encontrada");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cargar imagen");
        }
    }


    private String extractRelativePath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        return requestURI.substring(contextPath.length() + "/image/".length());
    }
}
