package id.my.hendisantika.compose.service;

import id.my.hendisantika.compose.dto.PlaybackHistoryResponse;
import id.my.hendisantika.compose.dto.PlaybackSaveRequest;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PlaybackService {
    void saveProgress(PlaybackSaveRequest request);
    PlaybackHistoryResponse getHistory(Long userId, Pageable pageable);
    PlaybackHistoryResponse getHistory(Long userId, Integer limit, String nextPageToken);
}
