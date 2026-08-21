package com.onixbyte.deltaforceguide.manager;

import com.onixbyte.deltaforceguide.domain.entity.Firearm;
import com.onixbyte.deltaforceguide.repository.FirearmRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FirearmManager {


    private final FirearmRepository firearmRepository;

    public FirearmManager(FirearmRepository firearmRepository) {
        this.firearmRepository = firearmRepository;
    }

    public Optional<Firearm> findById(Long firearmId) {
        return firearmRepository.findById(firearmId);
    }
}
