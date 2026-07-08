package id.my.hendisantika.compose.repository;

import id.my.hendisantika.compose.entity.PlaybackProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MySqlPlaybackRepositoryTest {

    @Mock
    PlaybackProgressJpaRepository jpaRepository;

    @Mock
    CacheClient cacheClient;

    @Mock
    EventPublisher eventPublisher;

    @InjectMocks
    MySqlPlaybackRepository repository;

    PlaybackProgress sample;

    @BeforeEach
    void setup() {
        sample = new PlaybackProgress(123L, "media-1", 1000L, 5000L, Instant.now(), Instant.now());
    }

    @Test
    void getFromStore_returnsEntityWhenPresent() {
        when(jpaRepository.findByUserIdAndMediaId(123L, "media-1")).thenReturn(Optional.of(sample));
        var opt = repository.getFromStore(123L, "media-1");
        assertTrue(opt.isPresent());
        assertEquals("media-1", opt.get().getMediaId());
    }

    @Test
    void writeToStore_savesAndReturnsEntity() {
        when(jpaRepository.save(sample)).thenReturn(sample);
        var saved = repository.writeToStore(sample);
        assertNotNull(saved);
        verify(jpaRepository, times(1)).save(sample);
    }

    @Test
    void cacheAndPublish_behaviour() {
        when(cacheClient.get(123L, "media-1")).thenReturn(Optional.of(sample));
        var cached = repository.getFromCache(123L, "media-1");
        assertTrue(cached.isPresent());

        repository.writeToCache(sample);
        verify(cacheClient, times(1)).put(sample);

        repository.publishEvent(sample);
        verify(eventPublisher, times(1)).publish(sample);
    }

    @Test
    void getByUser_delegatesToJpa() {
        var page = new PageImpl<>(List.of(sample));
        when(jpaRepository.findByUserIdOrderByUpdatedAtDesc(eq(123L), any(PageRequest.class))).thenReturn(page);
        Page<PlaybackProgress> res = repository.getByUser(123L, PageRequest.of(0, 10));
        assertEquals(1, res.getTotalElements());
    }
}
