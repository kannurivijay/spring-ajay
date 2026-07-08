package id.my.hendisantika.compose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.dto.PlaybackSaveRequest;
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

import java.time.Instant;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlaybackControllerTest {

    @Mock
    PlaybackService playbackService;

    @InjectMocks
    PlaybackController controller;

    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void save_happyPath_returns202AndTraceId() throws Exception {
        PlaybackSaveRequest req = new PlaybackSaveRequest(1L, "m1", 1000L, 5000L, Instant.now(), "MOBILE");
        doNothing().when(playbackService).saveProgress(req);

        mvc.perform(post("/v1/playback/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("Authorization", "Bearer token_with_scope playback:write"))
                .andExpect(status().isAccepted())
                .andExpect(header().exists("X-Trace-Id"));

        verify(playbackService).saveProgress(req);
    }

    @Test
    void save_missingScope_returns403() throws Exception {
        PlaybackSaveRequest req = new PlaybackSaveRequest(1L, "m1", 1000L, 5000L, Instant.now(), "MOBILE");

        mvc.perform(post("/v1/playback/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("Authorization", "Bearer token_without_scope"))
                .andExpect(status().isForbidden());
    }

    @Test
    void save_invalidRequest_returns400() throws Exception {
        // missing mediaId
        PlaybackSaveRequest req = new PlaybackSaveRequest(1L, "", 1000L, 5000L, Instant.now(), "MOBILE");

        mvc.perform(post("/v1/playback/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("Authorization", "Bearer playback:write"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void history_cacheHit_returnsRecords() throws Exception {
        // prepare a simple cache-backed service response by mocking playbackService
        var dto = new id.my.hendisantika.compose.dto.PlaybackRecordDTO(1L, 1L, "m1", "MOBILE", 1000L, 5000L, Instant.now(), Instant.now());
        var resp = new id.my.hendisantika.compose.dto.PlaybackHistoryResponse(java.util.List.of(dto), 1);
        when(playbackService.getHistory(eq(1L), any(Integer.class), anyString())).thenReturn(resp);

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/playback/history/1")
                .param("limit", "10")
                .header("Authorization", "Bearer token playback:read"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.total").value(1));
    }

    @Test
    void history_missingScope_returns403() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/playback/history/1"))
                .andExpect(status().isForbidden());
    }
}
