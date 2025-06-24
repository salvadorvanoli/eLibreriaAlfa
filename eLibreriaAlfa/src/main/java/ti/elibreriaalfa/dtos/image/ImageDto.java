package ti.elibreriaalfa.dtos.image;

import lombok.Data;

@Data
public class ImageDto {
    private String relativePath;
    private String originalName;
    private long size;
}
