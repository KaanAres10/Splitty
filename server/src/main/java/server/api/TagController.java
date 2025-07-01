package server.api;

import commons.Event;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.EventService;
import server.service.TagService;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private EventService eventService;

    @PostMapping("/add")
    public ResponseEntity<Tag> addTag(@RequestBody Tag tag, @PathVariable Long eventId) {
        if (tag == null || eventId == null) {
            return ResponseEntity.badRequest().build();
        }
        tag = tagService.addTag(eventId, tag);
        System.out.println(tag);
        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("/remove/{tagId}")
    public ResponseEntity<String> removeTag(@PathVariable Long eventId, @PathVariable Long tagId) {
        if (eventId == null)
            return ResponseEntity.badRequest().build();
        if (tagService.removeTag(eventId, tagId)) {
            return ResponseEntity.ok("Tag was successfully deleted!");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/update")
    public ResponseEntity<Tag> updateTag(@RequestBody Tag tag, @PathVariable Long eventId) {
        if (tag == null || eventId == null) {
            return ResponseEntity.badRequest().build();
        }
        tag = tagService.addTag(eventId, tag);
        return ResponseEntity.ok(tag);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Tag>> getAllTagsByEventId(@PathVariable("eventId") long eventId) {
        Event event = eventService.findById(eventId);
        if (event == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(eventService.getAllTagsByEventId(eventId));
    }

}