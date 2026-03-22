package com.baeldung.ljj;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class DateWrapper {
    private LocalDateTime dateTime;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'")
    private ZonedDateTime zonedDateTime;

    public DateWrapper(LocalDate date, LocalDateTime dateTime, ZonedDateTime zonedDateTime) {
        super();
        this.date = date;
        this.dateTime = dateTime;
        this.zonedDateTime = zonedDateTime;
    }

    public DateWrapper() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
}
