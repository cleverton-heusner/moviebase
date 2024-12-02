package cleverton.heusner.adapter.input.exception;

import lombok.Data;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Data
public class ErrorResponse {
    private String title;
    private String detail;
    private int status;
    private LocalDateTime dateTime;

    public ErrorResponse(final String title, final String detail, final int status) {
        this.title = title;
        this.detail = detail;
        this.status = status;
        this.dateTime = now();
    }
}