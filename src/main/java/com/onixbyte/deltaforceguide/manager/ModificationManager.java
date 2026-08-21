package com.onixbyte.deltaforceguide.manager;

import com.onixbyte.deltaforceguide.domain.entity.Modification;
import com.onixbyte.deltaforceguide.repository.FirearmRepository;
import com.onixbyte.deltaforceguide.repository.ModificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public class ModificationManager {

    private final ModificationRepository modificationRepository;
    private final FirearmRepository firearmRepository;

    public ModificationManager(
            ModificationRepository modificationRepository,
            FirearmRepository firearmRepository
    ) {
        this.modificationRepository = modificationRepository;
        this.firearmRepository = firearmRepository;
    }

    @Transactional
    public Modification save(Modification modification) {
        return modificationRepository.save(modification);
    }

    public Long resolveFirearmId(Long firearmId, String firearmName) {
        if (firearmId != null) {
            return firearmId;
        }
        if (firearmName == null || firearmName.isBlank()) {
            return null;
        }
        var matches = firearmRepository.findByName(firearmName);
        if (matches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Firearm not found by name: " + firearmName);
        }
        return matches.getFirst().getId();
    }

    @Transactional(readOnly = true)
    public Page<Modification> findBySpec(Specification<Modification> spec, Pageable pageable) {
        return modificationRepository.findAll(spec, pageable);
    }

    public Optional<Modification> findById(Long id) {
        return modificationRepository.findById(id);
    }

    public List<String> findAllTags(Long firearmId) {
        return modificationRepository.findAllTags(firearmId);
    }

    @Transactional
    public void delete(Modification modification) {
        modificationRepository.delete(modification);
    }

    public List<Modification> findAllById(Iterable<Long> uniqueIds) {
        return modificationRepository.findAllById(uniqueIds);
    }

    public void deleteAllInBatch(List<Modification> modifications) {
        modificationRepository.deleteAllInBatch(modifications);
    }
}
