package dev.benchurchill.desky.loader;

import com.google.gson.*;
import dev.benchurchill.desky.office.Desk;
import dev.benchurchill.desky.office.ElementType;
import dev.benchurchill.desky.office.Office;
import dev.benchurchill.desky.office.OfficeElement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.*;
import java.util.*;

public class OfficeLoader {
    public static Office loadOffice(File file) throws IOException {
        JsonObject root = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();

        String id = root.get("id").getAsString();
        String name = root.get("displayName").getAsString();
        Office office = new Office(id, name);

        World world = Bukkit.getWorlds().get(0); // Replace with smarter logic
        JsonArray elements = root.getAsJsonArray("elements");

        for (JsonElement el : elements) {
            JsonObject obj = el.getAsJsonObject();
            String type = obj.get("type").getAsString();
            String elemId = obj.get("id").getAsString();

            Location loc = new Location(
                    world,
                    obj.get("x").getAsDouble(),
                    obj.get("y").getAsDouble(),
                    obj.get("z").getAsDouble()
            );

            OfficeElement element = switch (ElementType.valueOf(type)) {
                case DESK -> new Desk(elemId, loc, id);
                default -> throw new IllegalArgumentException("Unknown element type: " + type);
            };

            office.addElement(element);
            element.getLocation().getBlock().setType(Material.ACACIA_SLAB);
        }

        return office;
    }
}
