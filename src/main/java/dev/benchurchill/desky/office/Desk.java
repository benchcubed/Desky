package dev.benchurchill.desky.office;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
public class Desk extends OfficeElement {
    private UUID bookedBy;

    public Desk(String id, Location location, String officeId) {
        super(id, location, officeId);
    }

    @Override
    public ElementType getType() {
        return ElementType.DESK;
    }
}
