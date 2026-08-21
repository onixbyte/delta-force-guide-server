package com.onixbyte.deltaforceguide.domain.dto;

import com.onixbyte.deltaforceguide.domain.entity.Accessory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Request DTO for creating or updating an accessory attached to a modification.
 *
 * @author zihluwang
 */
public record AccessoryRequest(
        @NotBlank(message = "插槽名称不能为空")
        String slotName,
        @NotBlank(message = "配件名称不能为空")
        String accessoryName,
        List<@Valid TuningRequest> tunings
) {
    public List<TuningRequest> tunings() {
        return Optional.ofNullable(tunings).orElseGet(ArrayList::new);
    }

    public Accessory toEntity() {
        var tunings = tunings().stream().map(TuningRequest::toEntity).toList();

        var accessory = new Accessory();
        accessory.setSlotName(slotName());
        accessory.setAccessoryName(accessoryName());
        accessory.setTunings(tunings);
        return accessory;
    }
}

