package id.my.hendisantika.compose.service.impl;

import id.my.hendisantika.compose.dto.PlaybackHistoryResponse;
import id.my.hendisantika.compose.dto.PlaybackRecordDTO;
import id.my.hendisantika.compose.dto.PlaybackSaveRequest;
import id.my.hendisantika.compose.entity.PlaybackProgress;
import id.my.hendisantika.compose.repository.PlaybackRepository;
import id.my.hendisantika.compose.repository.cache.CacheClient;
import id.my.hendisantika.compose.service.PlaybackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaybackServiceImpl implements PlaybackService {

    private final PlaybackRepository playbackRepository;
    private final CacheClient cacheClient;

    @Override
    @Transactional
    public void saveProgress(PlaybackSaveRequest request) {
        if (request == null) throw new IllegalArgumentException("request is required");
        Instant now = Optional.ofNullable(request.getUpdatedAt()).orElse(Instant.now());
        // Hot-path behaviour: write to cache and publish event to Kafka for async persistence.
        PlaybackProgress record = new PlaybackProgress(request.getUserId(), request.getMediaId(), request.getPositionMs(), request.getDurationMs(), now, now);
        // Write to cache (Redis HSET playback:user:{userId} field mediaId -> JSON)
        playbackRepository.writeToCache(record);
        // Publish event for worker to persist to DB asynchronously
        playbackRepository.publishEvent(record);
    }

    @Override
    public PlaybackHistoryResponse getHistory(Long userId, Pageable pageable) {
        Pageable p = pageable == null ? PageRequest.of(0, 50) : pageable;
        var page = playbackRepository.getByUser(userId, p);
        List<PlaybackRecordDTO> records = page.stream().map(ent -> new PlaybackRecordDTO(ent.getId(), ent.getUserId(), ent.getMediaId(), ent.getPositionMs(), ent.getDurationMs(), ent.getCreatedAt(), ent.getUpdatedAt())).collect(Collectors.toList());
        return new PlaybackHistoryResponse(records, page.getTotalElements());
    }

    @Override
    public PlaybackHistoryResponse getHistory(Long userId, Integer limit, String nextPageToken) {
        int pageSize = (limit == null || limit <= 0) ? 50 : limit;
        int offset = 0;
        if (nextPageToken != null && !nextPageToken.isBlank()) {
            try {
                byte[] decoded = java.util.Base64.getUrlDecoder().decode(nextPageToken);
                String s = new String(decoded);
                offset = Integer.parseInt(s);
            } catch (Exception e) { offset = 0; }
        }

        // Try cache first
        var map = cacheClient.getAll(userId);
        if (map != null && !map.isEmpty()) {
            var list = map.values().stream().sorted((a,b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt())).collect(Collectors.toList());
            int total = list.size();
            int from = Math.min(offset, total);
            int to = Math.min(from + pageSize, total);
            var slice = list.subList(from, to);
            List<PlaybackRecordDTO> records = slice.stream().map(ent -> new PlaybackRecordDTO(ent.getId(), ent.getUserId(), ent.getMediaId(), ent.getPositionMs(), ent.getDurationMs(), ent.getCreatedAt(), ent.getUpdatedAt())).collect(Collectors.toList());
            String next = to < total ? java.util.Base64.getUrlEncoder().encodeToString(String.valueOf(to).getBytes()) : null;
            return new PlaybackHistoryResponse(records, total);
        }

        // Fallback to store (DB)
        int pageIndex = offset / pageSize;
        var page = playbackRepository.getByUser(userId, PageRequest.of(pageIndex, pageSize));
        List<PlaybackRecordDTO> records = page.stream().map(ent -> new PlaybackRecordDTO(ent.getId(), ent.getUserId(), ent.getMediaId(), ent.getPositionMs(), ent.getDurationMs(), ent.getCreatedAt(), ent.getUpdatedAt())).collect(Collectors.toList());
        // populate cache for future hits
        for (var ent : page) {
            playbackRepository.writeToCache(ent);
        }
        long total = page.getTotalElements();
        return new PlaybackHistoryResponse(records, total);
    }
}
