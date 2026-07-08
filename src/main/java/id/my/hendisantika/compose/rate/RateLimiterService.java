package id.my.hendisantika.compose.rate;

public interface RateLimiterService {
    /**
     * Returns true if the request for the given userId is allowed under current rate limits.
     */
    boolean allowRequest(Long userId);
}
