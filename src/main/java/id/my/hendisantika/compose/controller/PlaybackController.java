package id.my.hendisantika.compose.controller;

import id.my.hendisantika.compose.dto.PlaybackHistoryResponse;
import id.my.hendisantika.compose.dto.PlaybackSaveRequest;
import id.my.hendisantika.compose.service.PlaybackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/playback")
@RequiredArgsConstructor
@Validated
public class PlaybackController {

    private final PlaybackService playbackService;

    @PostMapping("/save")
    public ResponseEntity<Void> save(@Valid @RequestBody PlaybackSaveRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        // Simple scope check: Authorization header must contain 'playback:write' scope.
        String auth = servletRequest.getHeader("Authorization");
        if (auth == null || !auth.contains("playback:write")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String traceId = java.util.UUID.randomUUID().toString();
        servletResponse.setHeader("X-Trace-Id", traceId);
        playbackService.saveProgress(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).header("X-Trace-Id", traceId).build();
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<PlaybackHistoryResponse> history(@PathVariable Long userId,
                                                           @RequestParam(required = false) Integer limit,
                                                           @RequestParam(required = false) String nextPageToken,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "50") int size,
                                                           HttpServletRequest servletRequest,
                                                           HttpServletResponse servletResponse) {
        // scope check
        String auth = servletRequest.getHeader("Authorization");
        if (auth == null || !auth.contains("playback:read")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String traceId = java.util.UUID.randomUUID().toString();
        servletResponse.setHeader("X-Trace-Id", traceId);

        if (limit != null || nextPageToken != null) {
            return ResponseEntity.ok(playbackService.getHistory(userId, limit, nextPageToken));
        }

        Pageable p = PageRequest.of(Math.max(0, page), Math.max(1, size));
        return ResponseEntity.ok(playbackService.getHistory(userId, p));
    }
}
