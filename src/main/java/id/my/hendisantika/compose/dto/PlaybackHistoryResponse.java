package id.my.hendisantika.compose.dto;

import java.util.List;

public record PlaybackHistoryResponse(List<PlaybackRecordDTO> records, long total) {}
