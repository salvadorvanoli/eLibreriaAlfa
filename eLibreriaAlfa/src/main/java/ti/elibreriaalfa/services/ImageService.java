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
       // Path basePathObj = Paths.get(basePath).toAbsolutePath().normalize();

        this.basePathObj = Paths.get(basePath).toAbsolutePath().normalize();
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

        // Validaciones específicas para impresiones
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }

        // Verificar extensión del archivo
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        return extension;
    }

    public String savePrint(MultipartFile file) throws IOException {
        System.out.println("=== INICIANDO GUARDADO DE ARCHIVO ===");
        System.out.println("Nombre original: " + file.getOriginalFilename());
        System.out.println("Tamaño: " + file.getSize() + " bytes");
        System.out.println("Tipo de contenido: " + file.getContentType());

        validatePrint(file);
        System.out.println("✅ Validación completada");

        Path subfolderPath = getSubfolderPath("impresiones");
        System.out.println("Ruta de subcarpeta: " + subfolderPath.toAbsolutePath());

        Files.createDirectories(subfolderPath);
        System.out.println("✅ Directorio creado/verificado");

        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        System.out.println("Nombre del archivo generado: " + filename);

        Path destination = subfolderPath.resolve(filename);
        System.out.println("Ruta completa de destino: " + destination.toAbsolutePath());

        file.transferTo(destination.toFile());
        System.out.println("✅ Archivo guardado exitosamente");
        System.out.println("¿Archivo existe?: " + Files.exists(destination));
        System.out.println("Tamaño del archivo guardado: " + Files.size(destination) + " bytes");
        System.out.println("=== FIN GUARDADO DE ARCHIVO ===");

        return filename;
    }


}
