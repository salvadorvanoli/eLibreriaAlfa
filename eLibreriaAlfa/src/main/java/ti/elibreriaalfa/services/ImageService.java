package ti.elibreriaalfa.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
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
        Path basePathObj = Paths.get(basePath).toAbsolutePath().normalize();
        Files.createDirectories(basePathObj);
    }

    public String saveImage(MultipartFile file, String type) throws IOException {
        validateImage(file);
        Path subfolderPath = getSubfolderPath(type);
        Files.createDirectories(subfolderPath);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destination = subfolderPath.resolve(filename);
        file.transferTo(destination.toFile());
        return filename;
    }

    public Resource loadImage(String relativePath) throws IOException {
        Path path = basePathObj.resolve(relativePath).normalize();

        verifyGeneratedPath(path);

        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("Archivo no encontrado: " + relativePath);
        }
    }

    public boolean deleteImage(String relativePath) throws IOException {
        Path path = basePathObj.resolve(relativePath).normalize();

        verifyGeneratedPath(path);

        return Files.deleteIfExists(path);
    }

    private Path getSubfolderPath(String type) {
        return basePathObj.resolve(type);
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/") || contentType.equals("image/gif"))
            throw new IllegalArgumentException("El archivo no es una imagen válida");
    }

    private void verifyGeneratedPath(Path path) {
        if (!path.toAbsolutePath().startsWith(basePathObj)) {
            throw new SecurityException("Acceso a ruta no permitida");
        }
    }

}
