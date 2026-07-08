package id.my.hendisantika.compose.service;

import id.my.hendisantika.compose.dto.PlaybackSaveRequest;
import id.my.hendisantika.compose.entity.PlaybackProgress;
import id.my.hendisantika.compose.repository.PlaybackStoreRepository;
import id.my.hendisantika.compose.service.impl.PlaybackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlaybackServiceImplTest {

    private PlaybackStoreRepository storeRepository;
    private PlaybackServiceImpl service;

    @BeforeEach
    void setup() {
        storeRepository = Mockito.mock(PlaybackStoreRepository.class);
        service = new PlaybackServiceImpl(storeRepository);
    }

    @Test
    void saveProgress_happyPath_createsNewRecord() {
        PlaybackSaveRequest req = new PlaybackSaveRequest(1L, "m1", 1000L, 3000L, Instant.now());
        when(storeRepository.getFromStore(1L, "m1")).thenReturn(Optional.empty());

        service.saveProgress(req);

        ArgumentCaptor<PlaybackProgress> cap = ArgumentCaptor.forClass(PlaybackProgress.class);
        verify(storeRepository, times(1)).writeToStore(cap.capture());
        PlaybackProgress saved = cap.getValue();
        assertEquals(1L, saved.getUserId());
        assertEquals("m1", saved.getMediaId());
        assertEquals(1000L, saved.getPositionMs());
    }

    @Test
    void saveProgress_nullRequest_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.saveProgress(null));
    }

    @Test
    void getHistory_returnsPagedResults() {
        PlaybackProgress p1 = new PlaybackProgress(1L, "m1", 1000L, 2000L, Instant.now(), Instant.now());
        // set id via reflection is cumbersome; repository doesn't rely on id for mapping in test
        when(storeRepository.getByUser(eq(1L), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(p1), PageRequest.of(0,10),1));

        var resp = service.getHistory(1L, PageRequest.of(0,10));
        assertNotNull(resp);
        assertEquals(1, resp.records().size());
    }
}
