package com.publicitas.naca.cloudnative.carddemo.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

/** JSON request corresponding to one terminal input event and one CICS unit of work. */
public record BmsTerminalRequest(
    @NotNull UUID requestId,
    @NotNull UUID conversationId,
    @NotBlank @Pattern(regexp = "[A-Z0-9]{1,4}") String transactionId,
    @NotBlank @Pattern(regexp = "[A-Z0-9]{1,8}") String mapSet,
    @NotBlank @Pattern(regexp = "[A-Z0-9]{1,8}") String map,
    @NotBlank @Pattern(regexp = "ENTER|CLEAR|PF([1-9]|1[0-9]|2[0-4])") String aid,
    @Size(max = 32) String cursorField,
    @NotNull @Size(max = 256) Map<
        @Pattern(regexp = "[A-Z0-9-]{1,32}") String, @Valid BmsFieldInput> fields)
{
}
