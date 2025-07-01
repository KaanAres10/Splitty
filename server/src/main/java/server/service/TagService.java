package server.service;

import commons.Event;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.EventLastActivityListener;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventLastActivityListener eventLastActivityListener;

    public Tag addTag(Long eventId, Tag tag) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        Tag ret = tagRepository.save(tag);
        eventLastActivityListener.postPersist(ret);
        return tag;
    }

    public boolean removeTag(Long eventId, Long tagId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        try {
            Optional<Tag> tag = tagRepository.findById(tagId);
            tag.ifPresent(t -> eventLastActivityListener.postRemove(t));
            tagRepository.deleteById(tagId);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
