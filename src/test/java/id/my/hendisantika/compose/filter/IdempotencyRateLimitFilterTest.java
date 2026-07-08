package id.my.hendisantika.compose.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.rate.RateLimiterService;
import id.my.hendisantika.compose.repository.cache.CacheClient;
import id.my.hendisantika.compose.service.PlaybackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IdempotencyRateLimitFilterTest {

    @Mock
    CacheClient cacheClient;

    @Mock
    RateLimiterService rateLimiterService;

    @Mock
    PlaybackService playbackService;

    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    IdempotencyRateLimitFilter filter;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        var controller = new id.my.hendisantika.compose.controller.PlaybackController(playbackService);
        mvc = MockMvcBuilders.standaloneSetup(controller).addFilters(filter).build();
    }

    @Test
    void save_duplicateIdempotency_returns202_and_skipsService() throws Exception {
        String body = "{\"userId\":1,\"mediaId\":\"m1\",\"positionMs\":100}";
        when(cacheClient.setIdempotencyKey(eq(1L), eq("id1"), anyLong())).thenReturn(false);

        mvc.perform(post("/v1/playback/save").contentType(MediaType.APPLICATION_JSON).content(body).header("Idempotency-Key", "id1").header("Authorization", "playback:write"))
                .andExpect(status().isAccepted());

        verify(playbackService, never()).saveProgress(any());
    }

    @Test
    void save_rateLimited_returns429_and_skipsService() throws Exception {
        String body = "{\"userId\":1,\"mediaId\":\"m1\",\"positionMs\":100}";
        when(cacheClient.setIdempotencyKey(eq(1L), eq("id2"), anyLong())).thenReturn(true);
        when(rateLimiterService.allowRequest(1L)).thenReturn(false);

        mvc.perform(post("/v1/playback/save").contentType(MediaType.APPLICATION_JSON).content(body).header("Idempotency-Key", "id2").header("Authorization", "playback:write"))
                .andExpect(status().isTooManyRequests());

        verify(playbackService, never()).saveProgress(any());
    }

    @Test
    void save_allowed_proceedsToService() throws Exception {
        String body = "{\"userId\":1,\"mediaId\":\"m1\",\"positionMs\":100}";
        when(cacheClient.setIdempotencyKey(eq(1L), eq("id3"), anyLong())).thenReturn(true);
        when(rateLimiterService.allowRequest(1L)).thenReturn(true);

        mvc.perform(post("/v1/playback/save").contentType(MediaType.APPLICATION_JSON).content(body).header("Idempotency-Key", "id3").header("Authorization", "Bearer playback:write"))
                .andExpect(status().isAccepted());

        verify(playbackService).saveProgress(any());
    }
}
