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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.images.base-path}")
    private String basePath;

    private Path basePathObj;

    @PostConstruct
    public void init() throws IOException {
        this.basePathObj = Paths.get(basePath).toAbsolutePath().normalize();
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
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        String decodedPath = URLDecoder.decode(relativePath, StandardCharsets.UTF_8);
        Path path = basePathObj.resolve(decodedPath).normalize();

        try {
            verifyGeneratedPath(path);

            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException | SecurityException e) {
            return null;
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
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        Path path = basePathObj.resolve(relativePath).normalize();

        try {
            verifyGeneratedPath(path);

            if (Files.exists(path) && Files.isReadable(path)) {
                return createImageDtoFromPath(path, relativePath);
            } else {
                return null;
            }
        } catch (SecurityException e) {
            return null;
        }
    }

    private Path getSubfolderPath(String type) {
        return basePathObj.resolve(type);
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageException("La imagen está vacía");
        }

        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new ImageException("La imagen excede el tamaño máximo permitido de 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new ImageException("No se pudo determinar el tipo de archivo");
        }

        List<String> allowedTypes = Arrays.asList(
                "image/jpeg", "image/jpg", "image/png", "image/webp",
                "image/svg+xml", "image/bmp", "image/tiff"
        );

        if (!allowedTypes.contains(contentType.toLowerCase())) {
            throw new ImageException("Formato de imagen no permitido. Formatos válidos: JPG, PNG, WEBP, SVG, BMP, TIFF");
        }
    }

    private void verifyGeneratedPath(Path path) {
        if (!path.toAbsolutePath().startsWith(basePathObj)) {
            throw new SecurityException("Acceso a ruta no permitida");
        }
    }

    private void validatePrint(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        // Tipos permitidos para impresiones
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Tipo de archivo no válido");
        }

        String extension = getString(file, contentType);
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx", "xls", "xlsx");
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("Extensión de archivo no permitida: " + extension);
        }
    }

    private static String getString(MultipartFile file, String contentType) {
        List<String> allowedTypes = Arrays.asList(
                "image/jpeg", "image/png", "image/gif", "image/webp", // Imágenes
                "application/pdf", // PDF
                "application/msword", // Word (.doc)
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // Word (.docx)
                "application/vnd.ms-excel", // Excel (.xls)
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // Excel (.xlsx)
        );

        if (!allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido para impresión: " + contentType);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        return extension;
    }

    public String savePrint(MultipartFile file) throws IOException {

        validatePrint(file);

        Path subfolderPath = getSubfolderPath("impresiones");

        Files.createDirectories(subfolderPath);

        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Path destination = subfolderPath.resolve(filename);

        file.transferTo(destination.toFile());

        return filename;
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
