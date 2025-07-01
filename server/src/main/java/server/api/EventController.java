package server.api;

import commons.*;
import dto.EventUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.EventService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping ("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;


    @PostMapping ("/create")
    public ResponseEntity<Event> createEvent(@RequestBody EventUserDTO eventUserDTO) {
        Event event = eventUserDTO.getEvent();
        User user = eventUserDTO.getUser();
        if (event == null || event.getTitle() == null || event.getCreationDate() == null) {
            return ResponseEntity.badRequest().build();
        }

        Event savedEvent = eventService.createEvent(event, user);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    @PostMapping ("/{id}/updateCode")
    public ResponseEntity<Event> editInviteCode(@PathVariable ("id") long id) {
        Event eventToUpdate = eventService.findById(id);
        if (eventToUpdate == null) {
            return ResponseEntity.notFound().build();
        }
        eventToUpdate.setInviteCode(UUID.randomUUID().toString().substring(0, 8));
        Event savedEvent = eventService.saveEvent(eventToUpdate);
        return ResponseEntity.ok(savedEvent);
    }

    /**
     * Changes the title of an event
     */
    @PutMapping ("/{eventId}/title")
    public ResponseEntity<Event> changeEventTitle(@PathVariable Long eventId, @RequestBody Event updated) {
        if (eventId == null || updated.getTitle() == null || updated.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Event changedEvent = eventService.changeTitle(eventId, updated.getTitle());
        if (changedEvent != null)
            return new ResponseEntity<>(changedEvent, HttpStatus.OK);
        else
            return ResponseEntity.badRequest().build();
    }

    /**
     * Deletes an event
     */
    @DeleteMapping ("/{eventId}/delete")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        if (eventService.deleteEvent(eventId))
            return ResponseEntity.ok("Event was deleted successfully");
        else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping ("/{eventId}")
    public ResponseEntity<Event> getById(@PathVariable Long eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) return ResponseEntity.notFound().build();
        else return ResponseEntity.ok(event);
    }

    /**
     * Get Event by Invite Code
     */
    @GetMapping ("/invite/{inviteCode}")
    public ResponseEntity<Event> getEventByInviteCode(@PathVariable String inviteCode) {
        if (inviteCode == null || inviteCode.isEmpty())
            return ResponseEntity.badRequest().build();
        Event event = eventService.findByInviteCode(inviteCode);
        if (event != null) return ResponseEntity.ok(event);
        else return ResponseEntity.notFound().build();
    }

    @GetMapping ("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> list = eventService.getAllEvents();
        return ResponseEntity.ok(list);
    }

    @GetMapping ("/{eventId}/transfers/all")
    public ResponseEntity<List<Transfer>> getAllTransfersByEventId(
        @PathVariable ("eventId") long eventId) {
        Event event = eventService.findById(eventId);
        if (event == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(eventService.getAllTransfersByEventId(eventId));
    }

    @GetMapping ("/{eventId}/debts/all")
    public ResponseEntity<List<Debt>> getAllDebtsByEventId(@PathVariable ("eventId") long eventId) {
        Event event = eventService.findById(eventId);
        if (event == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(eventService.getAllDebtsByEventId(eventId));
    }
}