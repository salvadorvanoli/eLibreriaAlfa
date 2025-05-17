package ti.elibreriaalfa.api.requests;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String from;
    private String subject;
    private String body;
}
