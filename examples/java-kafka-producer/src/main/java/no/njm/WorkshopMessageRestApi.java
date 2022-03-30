package no.njm;

import no.njm.WorkshopMessageProducer.WorkshopMessage;
import no.njm.WorkshopMessageProducer.WorkshopMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class WorkshopMessageRestApi {

    private final WorkshopMessageProducer workshopMessageProducer;

    @Autowired
    WorkshopMessageRestApi(WorkshopMessageProducer workshopMessageProducer) {
        this.workshopMessageProducer = workshopMessageProducer;
    }

    @ExceptionHandler(WorkshopMessageException.class)
    ResponseEntity<String> handleException() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/message")
    ResponseEntity<String> sendMessage(@RequestBody Message message) {
        if (message != null) {
            workshopMessageProducer.sendSync(toWorkshopMessage(message));
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/message/async")
    ResponseEntity<String> sendAsyncMessage(@RequestBody Message message) {
        if (message != null) {
            workshopMessageProducer.sendAsync(toWorkshopMessage(message));
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private record Message(String message) {
    }

    private WorkshopMessage toWorkshopMessage(Message message) {
        String uuid = UUID.randomUUID().toString();
        return new WorkshopMessage(uuid, message.message);
    }
}
