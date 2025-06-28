package dev.benchurchill.desky.office;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor
public abstract class OfficeElement {
    private final String id;
    private final Location location;
    private final String officeId;

    public abstract ElementType getType();
}
