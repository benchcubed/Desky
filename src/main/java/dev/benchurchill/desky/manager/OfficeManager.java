package dev.benchurchill.desky.manager;

import dev.benchurchill.desky.office.Office;
import lombok.Getter;

import java.util.*;

public class OfficeManager {
    @Getter
    private static final OfficeManager instance = new OfficeManager();
    private final Map<String, Office> offices = new HashMap<>();

    public void registerOffice(Office office) {
        offices.put(office.getId(), office);
    }

    public Optional<Office> getOffice(String id) {
        return Optional.ofNullable(offices.get(id));
    }

    public Collection<Office> getAllOffices() {
        return offices.values();
    }
}
