package ti.elibreriaalfa.services;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ti.elibreriaalfa.dtos.image.ImageDto;
import ti.elibreriaalfa.exceptions.image.ImageException;
import ti.elibreriaalfa.exceptions.image.ImageNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.images.base-path}")
    private String basePath;

    private Path basePathObj;

    @PostConstruct
    public void init() throws IOException {
        basePathObj = Paths.get(basePath).toAbsolutePath().normalize();
        Files.createDirectories(basePathObj);
    }

    public String saveImage(MultipartFile file, String type) {
        try {
            validateImage(file);
            Path subfolderPath = getSubfolderPath(type);
            Files.createDirectories(subfolderPath);

            String filename = getImageName(file.getOriginalFilename());
            Path destination = subfolderPath.resolve(filename);

            while (Files.exists(destination)) {
                filename = getImageName(file.getOriginalFilename());
                destination = subfolderPath.resolve(filename);
            }

            file.transferTo(destination.toFile());
            return type + '/' + filename;
        } catch (IOException e) {
            throw new ImageException("Error al guardar la imagen: " + e.getMessage());
        }
    }

    public Resource loadImage(String relativePath) {
        String decodedPath = URLDecoder.decode(relativePath, StandardCharsets.UTF_8);
        Path path = basePathObj.resolve(decodedPath).normalize();

        verifyGeneratedPath(path);

        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new ImageNotFoundException("Ruta del archivo malformada: " + relativePath);
        }

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new ImageNotFoundException("Archivo no encontrado: " + relativePath);
        }
    }

    public void deleteImage(String relativePath) {
        Path path = basePathObj.resolve(relativePath).normalize();

        verifyGeneratedPath(path);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new ImageException("Error al eliminar la imagen: " + e.getMessage());
        }
    }

    public ImageDto getImageInfo(String relativePath) {
        Path path = basePathObj.resolve(relativePath).normalize();
        verifyGeneratedPath(path);

        return createImageDtoFromPath(path, relativePath);
    }

    private Path getSubfolderPath(String type) {
        return basePathObj.resolve(type);
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/") || contentType.equals("image/gif"))
            throw new ImageException("El archivo no es una imagen válida");
    }

    private void verifyGeneratedPath(Path path) {
        if (!path.toAbsolutePath().startsWith(basePathObj)) {
            throw new SecurityException("Acceso a ruta no permitida");
        }
    }

    private String getImageName(String originalName) {
        if (originalName == null || originalName.trim().isEmpty()) {
            originalName = "unnamed_file";
        }

        originalName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

        if (originalName.length() > 100) {
            String extension = "";
            int lastDot = originalName.lastIndexOf('.');
            if (lastDot > 0) {
                extension = originalName.substring(lastDot);
                originalName = originalName.substring(0, Math.min(95, lastDot)) + extension;
            } else {
                originalName = originalName.substring(0, 100);
            }
        }

        return UUID.randomUUID() + "_" + originalName;
    }

    private ImageDto createImageDtoFromPath(Path path, String relativePath) {
        ImageDto dto = new ImageDto();
        dto.setRelativePath(relativePath.replace("\\", "/"));
        dto.setOriginalName(extractOriginalName(path.getFileName().toString()));
        try {
            dto.setSize(Files.size(path));
        } catch (IOException e) {
            throw new ImageException("Error al obtener el tamaño de la imagen: " + e.getMessage());
        }
        return dto;
    }

    private String extractOriginalName(String filename) {
        return filename.replaceFirst("^[0-9a-fA-F\\-]{36}_", "");
    }
}
