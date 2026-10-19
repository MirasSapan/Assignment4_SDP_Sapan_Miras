package library.client;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

final class AdjustableClock extends Clock {
    private Instant now;

    AdjustableClock(Instant start) {
        this.now = start;
    }

    void advanceDays(long days) {
        now = now.plusSeconds(days * 24 * 60 * 60);
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return now;
    }
}
