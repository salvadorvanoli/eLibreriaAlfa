package ti.elibreriaalfa.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ti.elibreriaalfa.api.responses.impresion.ResponseImpresion;
import ti.elibreriaalfa.api.responses.impresion.ResponseListadoImpresiones;
import ti.elibreriaalfa.dtos.impresion.ImpresionDto;
import ti.elibreriaalfa.services.ImpresionService;
import ti.elibreriaalfa.services.ImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "print")
public class ImpresionController {
    private final ImpresionService impresionService;
    private final ImageService imageService;

    public ImpresionController(ImpresionService impresionService, ImageService imageService) {
        this.impresionService = impresionService;
        this.imageService = imageService;
    }

    @Operation(summary = "Obtener todas las impresiones")
    @GetMapping
    public ResponseEntity<ResponseListadoImpresiones> getAllImpresiones() {
        return new ResponseEntity<>(impresionService.getAllImpresiones(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener una impresión por ID")
    @GetMapping("/{idImpresion}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Object> getImpresionById(@PathVariable (name = "idImpresion") Long idImpresion) {
        try {
            ResponseImpresion response = impresionService.getImpresionById(idImpresion);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Obtener todas las impresiones con paginación")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Page<ImpresionDto>> getImpresionPage(@RequestParam(name = "pagina") Integer pagina, @RequestParam(name = "cantidad") Integer cantidad) {
        return new ResponseEntity<>(impresionService.listadoImpresionPage(pagina, cantidad), HttpStatus.OK);
    }

    @Operation(summary = "Crear una impresión")
    @PostMapping
    public ResponseEntity<String> createImpresion(@RequestBody ImpresionDto impresionDto) {
        String response = impresionService.crearImpresion(impresionDto);

        if(response == null) {
            return new ResponseEntity<>("Error al crear la impresión", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

    @Operation(summary = "Eliminar una impresión por ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> borrarImpresion(@PathVariable(name = "id") Long idImpresion) {
        impresionService.borrarImpresion(idImpresion);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Listar impresiones por usuario")
    @GetMapping("/user/{usuarioId}")
    public ResponseEntity<?> listarImpresionesPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(impresionService.listarImpresionesPorUsuario(usuarioId));
    }

    @Operation(summary = "Cambiar el estado de una impresión")
    @PatchMapping("/{idImpresion}/estado")
    public ResponseEntity<String> cambiarEstadoImpresion(
            @PathVariable(name = "idImpresion") Long idImpresion,
            @RequestBody Map<String, String> request) {

        String nuevoEstado = request.get("estado");
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return new ResponseEntity<>("El estado es requerido", HttpStatus.BAD_REQUEST);
        }

        try {
            String resultado = impresionService.cambiarEstadoImpresion(idImpresion, nuevoEstado);
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Subir un archivo de impresión")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = imageService.savePrint(file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filename", filename);
            response.put("message", "Archivo subido exitosamente");
            response.put("url", "/elibreriaalfa/file/impresiones/" + filename);

            return ResponseEntity.ok(response);
        } catch (IOException | IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al subir archivo: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Descargar un archivo de impresión")
    @GetMapping("download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = imageService.loadImage("impresiones/" + filename);

            String contentType = Files.probeContentType(resource.getFile().toPath());

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (IOException e) {
            System.err.println("❌ Error de IO al descargar archivo: " + e.getMessage());
            System.err.println("Stack trace:");
            e.printStackTrace();
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            System.err.println("❌ Error inesperado al descargar archivo: " + e.getMessage());
            System.err.println("Tipo de excepción: " + e.getClass().getSimpleName());
            System.err.println("Stack trace:");
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
