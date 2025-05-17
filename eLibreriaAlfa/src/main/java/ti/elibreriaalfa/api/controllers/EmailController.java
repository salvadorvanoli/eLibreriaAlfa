package ti.elibreriaalfa.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ti.elibreriaalfa.api.requests.EmailRequest;
import ti.elibreriaalfa.services.EmailService;

@RestController
@RequestMapping("email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        String response = emailService.sendEmail(
                emailRequest.getTo(),
                emailRequest.getFrom(),
                emailRequest.getSubject(),
                emailRequest.getBody()
        );

        return new ResponseEntity<>(response , HttpStatus.OK);
    }
}