package com.onixbyte.deltaforceguide.domain.dto;

import com.onixbyte.deltaforceguide.domain.entity.Modification;
import com.onixbyte.deltaforceguide.domain.entity.User;
import com.onixbyte.deltaforceguide.enumeration.ModificationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Response DTO for a modification record including accessories and tags.
 *
 * @author zihluwang
 */
public record ModificationResponse(
        Long id,
        Long firearmId,
        Long userId,
        String name,
        String code,
        List<String> tags,
        List<AccessoryResponse> accessories,
        String note,
        String author,
        String videoUrl,
        ModificationStatus status
) {
    public static ModificationResponse from(Modification modification) {
        return new ModificationResponse(
                modification.getId(),
                modification.getFirearm().getId(),
                Optional.ofNullable(modification.getUser()).map(User::getId).orElse(null),
                modification.getName(),
                modification.getCode(),
                modification.getTags(),
                modification.getAccessories() == null
                        ? List.of()
                        : modification.getAccessories().stream().map(AccessoryResponse::from).toList(),
                modification.getNote(),
                modification.getAuthor(),
                modification.getVideoUrl(),
                modification.getStatus()
        );
    }
}

