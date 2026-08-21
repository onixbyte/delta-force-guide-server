package com.onixbyte.deltaforceguide.domain.dto;

import com.onixbyte.deltaforceguide.domain.entity.Tuning;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for a tuning adjustment on an accessory.
 *
 * @author zihluwang
 */
public record TuningRequest(
        @NotBlank(message = "调校项名称不能为空")
        String tuningName,
        @NotNull(message = "调校值不能为空")
        Double tuningValue
) {

    public Tuning toEntity() {
        var tuning = new Tuning();
        tuning.setTuningName(tuningName());
        tuning.setTuningValue(tuningValue());
        return tuning;
    }
}

