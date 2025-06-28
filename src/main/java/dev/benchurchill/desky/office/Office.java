package dev.benchurchill.desky.office;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class Office {
    private final String id;
    private final String displayName;
    private final Map<String, OfficeElement> elements = new HashMap<>();

    public void addElement(OfficeElement element) {
        elements.put(element.getId(), element);
    }

    public Optional<OfficeElement> getElement(String id) {
        return Optional.ofNullable(elements.get(id));
    }

    public Collection<OfficeElement> getAllElements() {
        return elements.values();
    }
}
