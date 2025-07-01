package server.service;

import commons.Event;
import commons.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.EventLastActivityListener;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventLastActivityListener eventLastActivityListener;

    @InjectMocks
    private TagService tagService;


    @Test
    public void testAddTag() {
        Event event = new Event();
        event.setId(1L);
        Tag tag = new Tag();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag result = tagService.addTag(1L, tag);

        assertNotNull(result);
        assertEquals(tag, result);
    }

    @Test
    public void testAddTagEventNotFound() {
        Tag tag = new Tag();

        when(eventRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tagService.addTag(1L, tag));
    }

    @Test
    public void testRemoveTagEventFoundTagDeletedSuccessfully() {
        // Arrange
        Long eventId = 1L;
        Long tagId = 1L;
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(tagRepository).deleteById(tagId);

        boolean result = tagService.removeTag(eventId, tagId);

        assertTrue(result);
        verify(eventRepository, times(1)).findById(eventId);
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    public void testRemoveTagEventFoundTagDeletionFailed() {
        Long eventId = 1L;
        Long tagId = 1L;
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doThrow(new RuntimeException("Error deleting tag")).when(tagRepository).deleteById(tagId);

        boolean result = tagService.removeTag(eventId, tagId);

        assertFalse(result);
        verify(eventRepository, times(1)).findById(eventId);
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    public void testRemoveTagEventNotFound() {
        Long eventId = 1L;
        Long tagId = 1L;

        try {
            tagService.removeTag(eventId, tagId);
        }catch (RuntimeException e){
            assertEquals(e.getMessage(),"Event not found");
        }
    }
}
