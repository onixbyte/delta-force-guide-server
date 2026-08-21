package com.onixbyte.deltaforceguide.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onixbyte.deltaforceguide.domain.dto.AccessoryRequest;
import com.onixbyte.deltaforceguide.domain.dto.ModificationRequest;
import com.onixbyte.deltaforceguide.domain.dto.ModificationResponse;
import com.onixbyte.deltaforceguide.domain.dto.PageResponse;
import com.onixbyte.deltaforceguide.domain.entity.Modification;
import com.onixbyte.deltaforceguide.domain.entity.User;
import com.onixbyte.deltaforceguide.enumeration.ModificationStatus;
import com.onixbyte.deltaforceguide.exeption.InternalServerErrorException;
import com.onixbyte.deltaforceguide.exeption.NotFoundException;
import com.onixbyte.deltaforceguide.manager.FirearmManager;
import com.onixbyte.deltaforceguide.manager.ModificationManager;
import com.onixbyte.deltaforceguide.specification.ModificationSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service handling modification business logic including CRUD, batch operations, and tag filtering.
 *
 * @author zihluwang
 */
@Service
public class ModificationService {

    private static final Logger log = LoggerFactory.getLogger(ModificationService.class);
    private final ModificationManager modificationManager;
    private final ObjectMapper objectMapper;
    private final FirearmManager firearmManager;

    public ModificationService(
            ModificationManager modificationManager,
            ObjectMapper objectMapper,
            FirearmManager firearmManager
    ) {
        this.modificationManager = modificationManager;
        this.objectMapper = objectMapper;
        this.firearmManager = firearmManager;
    }

    /**
     * Queries modifications with optional firearm and tag filters.
     *
     * @param firearmId optional firearm ID filter
     * @param tags      optional tag list filter
     * @param pageable  pagination parameters
     * @return a paginated response of modification records
     */
    public PageResponse<ModificationResponse> pageQuery(Long firearmId, List<String> tags, Pageable pageable) {
        String tagsJson = null;
        if (tags != null && !tags.isEmpty()) {
            try {
                tagsJson = objectMapper.writeValueAsString(tags);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialise tags.", e);
                throw new InternalServerErrorException("Failed to serialise tags.");
            }
        }

        var spec = Specification.allOf(
                ModificationSpecification.hasFirearmId(firearmId),
                ModificationSpecification.containsTags(tagsJson)
        );

        var page = modificationManager.findBySpec(spec, pageable);

        return PageResponse.from(page.map(ModificationResponse::from));
    }

    /**
     * Finds a modification by its ID.
     *
     * @param id the modification ID
     * @return the modification response
     */
    public ModificationResponse queryById(Long id) {
        return modificationManager.findById(id)
                .map(ModificationResponse::from)
                .orElseThrow(() -> new NotFoundException("Modification not found: " + id));
    }

    /**
     * Finds all unique tags across modifications, optionally scoped to a firearm.
     *
     * @param firearmId optional firearm ID to scope the tag search
     * @return list of unique tag strings
     */
    public List<String> findAllTags(Long firearmId) {
        return modificationManager.findAllTags(firearmId);
    }

    /**
     * Creates a new modification for a given firearm.
     *
     * @param request the modification creation request
     * @param user    the authenticated user creating the modification
     * @return the created modification response
     */
    public ModificationResponse create(ModificationRequest request, User user) {
        var firearm = firearmManager.findById(request.firearmId())
                .orElseThrow(() -> new NotFoundException("Firearm not found: " + request.firearmId()));
        var modification = modificationManager.save(Modification.builder()
                .firearm(firearm)
                .user(user)
                .name(request.name())
                .code(request.code())
                .tags(request.tags())
                .accessories(request.accessories().stream().map(AccessoryRequest::toEntity).toList())
                .note(request.note())
                .author(user.getUsername())
                .videoUrl(request.videoUrl())
                .createBy(user.getId())
                .build());
        return ModificationResponse.from(modification);
    }

    /**
     * Updates an existing modification identified by ID.
     *
     * @param id      the modification ID
     * @param request the updated modification data
     * @return the updated modification response
     */
    public ModificationResponse update(Long id, ModificationRequest request) {
        var modification = modificationManager.findById(id)
                .orElseThrow(() -> new NotFoundException("Modification not found: " + id));
        var firearm = firearmManager.findById(request.firearmId())
                .orElseThrow(() -> new NotFoundException("Firearm not found: " + request.firearmId()));

        var accessories = request.accessories()
                .stream()
                .map(AccessoryRequest::toEntity)
                .toList();

        var tags = Optional.ofNullable(request.tags())
                .orElseGet(ArrayList::new);

        modification.setFirearm(firearm);
        modification.setName(request.name());
        modification.setCode(request.code());
        modification.setTags(tags);
        modification.setAccessories(accessories);
        modification.setNote(request.note());
        modification.setAuthor(request.author());
        modification.setVideoUrl(request.videoUrl());

        return ModificationResponse.from(modificationManager.save(modification));
    }

    /**
     * Deletes a modification by its ID.
     *
     * @param id the modification ID to delete
     */
    public void delete(Long id) {
        var modification = modificationManager.findById(id)
                .orElseThrow(() -> new NotFoundException("Modification not found: " + id));
        modificationManager.delete(modification);
    }

    /**
     * Deletes multiple modifications in a single batch operation.
     *
     * @param ids list of modification IDs to delete
     */
    public void batchDelete(List<Long> ids) {
        var uniqueIds = new LinkedHashSet<>(ids);
        var modifications = modificationManager.findAllById(uniqueIds);

        if (modifications.size() != uniqueIds.size()) {
            var foundIds = modifications.stream()
                    .map(Modification::getId)
                    .collect(Collectors.toSet());
            var missingIds = uniqueIds.stream()
                    .filter((id) -> !foundIds.contains(id))
                    .toList();
            throw new NotFoundException("Modification not found: " + missingIds);
        }

        modificationManager.deleteAllInBatch(modifications);
    }

    public ModificationResponse createPublicModification(ModificationRequest request, User user) {
        var firearm = firearmManager.findById(request.firearmId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Firearm not found: " + request.firearmId()));
        var modification = modificationManager.save(Modification.builder()
                .firearm(firearm)
                .user(null)
                .name(request.name())
                .code(request.code())
                .tags(request.tags())
                .accessories(request.accessories().stream().map(AccessoryRequest::toEntity).toList())
                .note(request.note())
                .author(request.author())
                .videoUrl(request.videoUrl())
                .status(ModificationStatus.PUBLISHED)
                .createBy(user.getId())
                .build());
        return ModificationResponse.from(modification);
    }
}
