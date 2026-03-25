package dev.lucca.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record EmailDto(
        UUID userId,
        String emailTo,
        @JsonProperty("EmailSubject") String emailSubject,
        String body
) {
}

