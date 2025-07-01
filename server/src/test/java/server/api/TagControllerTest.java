package server.api;

import commons.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.EventService;
import server.service.TagService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import commons.Tag;

import java.util.ArrayList;
import java.util.List;

@ExtendWith (MockitoExtension.class)
public class TagControllerTest {

    @Mock
    private TagService tagService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private TagController tagController;

    @Test
    public void testAddTagSuccessTest() {
        Tag tag = new Tag("Test Tag", "Blue");
        Long eventId = 1L;
        when(tagService.addTag(eventId, tag)).thenReturn(tag);

        ResponseEntity<Tag> response = tagController.addTag(tag, eventId);

        verify(tagService, times(1)).addTag(eventId, tag);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().equals(tag);
    }

    @Test
    public void testAddTagNullInputsTest() {
        ResponseEntity<Tag> response = tagController.addTag(null, null);

        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    public void testAddTag() {
        Tag tag = new Tag();
        Long eventId = 1L;

        when(tagService.addTag(eventId, tag)).thenReturn(tag);

        ResponseEntity<Tag> response = tagController.addTag(tag, eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tag, response.getBody());
    }

    @Test
    public void testAddTagInvalidInput() {
        TagController tagController = new TagController();

        ResponseEntity<Tag> response = tagController.addTag(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testRemoveTag() {
        Long eventId = 1L;
        Long tagId = 1L;

        when(tagService.removeTag(eventId, tagId)).thenReturn(true);

        ResponseEntity<String> response = tagController.removeTag(eventId, tagId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tag was successfully deleted!", response.getBody());
    }

    @Test
    public void testRemoveTagInvalidEventId() {
        Long eventId = null;
        Long tagId = 1L;

        ResponseEntity<String> response = tagController.removeTag(eventId, tagId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testRemoveTagTagNotRemoved() {
        Long eventId = 1L;
        Long tagId = 1L;

        when(tagService.removeTag(eventId, tagId)).thenReturn(false);

        ResponseEntity<String> response = tagController.removeTag(eventId, tagId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testUpdateTag() {
        Tag tag = new Tag();
        Long eventId = 1L;

        when(tagService.addTag(eventId, tag)).thenReturn(tag);

        ResponseEntity<Tag> response = tagController.updateTag(tag, eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tag, response.getBody());
    }

    @Test
    public void testUpdateTagInvalidInput() {
        TagController tagController = new TagController();

        ResponseEntity<Tag> response = tagController.updateTag(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetAllTagsByEventId() {
        long eventId = 1L;
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag());

        when(eventService.findById(eventId)).thenReturn(new Event());
        when(eventService.getAllTagsByEventId(eventId)).thenReturn(tags);

        ResponseEntity<List<Tag>> response = tagController.getAllTagsByEventId(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tags, response.getBody());
    }

    @Test
    public void testGetAllTagsByEventIdEventNotFound() {
        long eventId = 1L;

        when(eventService.findById(eventId)).thenReturn(null);

        ResponseEntity<List<Tag>> response = tagController.getAllTagsByEventId(eventId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

}