package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.service.ParticipantService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@RestController
@RequestMapping ("/api/events/{eventId}/participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    private final Map<Object, Consumer<Participant>> listeners = new HashMap<>();

    @PostMapping ("/addparticipant")
    public ResponseEntity<Participant> addParticipant(@RequestBody Participant participant) {
        if (participant.getName() == null || participant.getBic() == null ||
            participant.getIban() == null || participant.getEvent() == null ||
            participant.getMail() == null){
            return ResponseEntity.badRequest().build();
        }
        listeners.forEach((k, l) -> l.accept(participant));

        Participant saved = participantService.addParticipantToEvent(participant);
        return ResponseEntity.ok(saved);
    }

    @PutMapping ("/{participantId}")
    public ResponseEntity<Participant> updateParticipant(@RequestBody Participant participant,
                                                         @PathVariable Long eventId,
                                                         @PathVariable Long participantId) {
        if (eventId == null || participantId == null) {
            return ResponseEntity.badRequest().build();
        }
        participant = participantService.updateParticipant(participant);
        if (participant == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(participant);

    }

    @GetMapping ("/updates")
    public DeferredResult<ResponseEntity<Participant>> getUpdates() {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var output = new DeferredResult<ResponseEntity<Participant>>(30000L, noContent);

        var key = new Object();
        listeners.put(key, participant -> output.setResult(ResponseEntity.ok(participant)));
        output.onCompletion(() -> listeners.remove(key));
        return output;
    }

    @GetMapping
    public List<Participant> getAllParticipants(@PathVariable Long eventId) {
        return participantService.getAllParticipantsByEventId(eventId);
    }

    @DeleteMapping ("/{participantId}/delete")
    public ResponseEntity<String> deleteParticipant(@PathVariable Long participantId) {
        if (participantId == null)
            return ResponseEntity.badRequest().build();
        if (participantService.deleteParticipant(participantId)) {
            return ResponseEntity.ok("Participant was successfully deleted!");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping ("/{name}")
    public Participant getByName(@PathVariable String name, @PathVariable Long eventId) {
        return participantService.getParticipantByName(eventId, name);
    }
}
