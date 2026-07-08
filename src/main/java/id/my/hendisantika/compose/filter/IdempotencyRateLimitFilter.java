package id.my.hendisantika.compose.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.compose.rate.RateLimiterService;
import id.my.hendisantika.compose.repository.cache.CacheClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * Filter that enforces idempotency via `Idempotency-Key` header and per-user rate-limiting.
 * Applies to POST /v1/playback/save.
 */
@Component
public class IdempotencyRateLimitFilter extends OncePerRequestFilter {

    private final CacheClient cacheClient;
    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    public IdempotencyRateLimitFilter(CacheClient cacheClient, RateLimiterService rateLimiterService, ObjectMapper objectMapper) {
        this.cacheClient = cacheClient;
        this.rateLimiterService = rateLimiterService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().equals("/v1/playback/save"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrapped = (request instanceof ContentCachingRequestWrapper) ? (ContentCachingRequestWrapper) request : new ContentCachingRequestWrapper(request);

        // read body
        byte[] buf = wrapped.getInputStream().readAllBytes();
        JsonNode root = null;
        Long userId = null;
        try {
            if (buf != null && buf.length > 0) {
                root = objectMapper.readTree(buf);
                if (root.has("userId") && root.get("userId").isNumber()) {
                    userId = root.get("userId").longValue();
                }
            }
        } catch (Exception e) {
            // ignore parsing errors, userId stays null
        }

        String idempotencyKey = request.getHeader("Idempotency-Key");
        if (idempotencyKey != null && userId != null) {
            boolean set = cacheClient.setIdempotencyKey(userId, idempotencyKey, 60);
            if (!set) {
                // duplicate request within window — return accepted (idempotent)
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                response.getWriter().flush();
                return;
            }
        }

        // Rate limiting per-user
        if (userId != null) {
            boolean allowed = rateLimiterService.allowRequest(userId);
            if (!allowed) {
                response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
                response.getWriter().write("Rate limit exceeded");
                return;
            }
        }

        filterChain.doFilter(wrapped, response);
    }
}
