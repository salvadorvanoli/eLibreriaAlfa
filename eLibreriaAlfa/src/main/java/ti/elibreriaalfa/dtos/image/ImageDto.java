package ti.elibreriaalfa.dtos.image;

import lombok.Data;

@Data
public class ImageDto {
    private String url;
    private String originalName;
    private long size;
    private String filename;
}
