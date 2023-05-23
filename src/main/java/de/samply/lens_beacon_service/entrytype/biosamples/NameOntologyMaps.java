package de.samply.lens_beacon_service.entrytype.biosamples;

import java.util.HashMap;
import java.util.Map;

/**
 * Map Lens term names onto Ontology IDs understood by Beacon.
 */
public class NameOntologyMaps {
    // Maps the standard names for sample types onto NCIT codes
    public static Map<String, String> biosmapleTypeUberon;
    static {
        biosmapleTypeUberon = new HashMap<String, String>();

        biosmapleTypeUberon.put("blood", "UBERON:0000178");
        biosmapleTypeUberon.put("blood-serum", "UBERON:0001977");
        biosmapleTypeUberon.put("blood-plasma", "UBERON:0001969");
        biosmapleTypeUberon.put("lymph", "UBERON:0002391");
    }
}
