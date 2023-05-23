package de.samply.lens_beacon_service.entrytype.genomicVariations;


import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.entrytype.genomicVariations.ast2filter.GenomicVariationsAstNodeListConverter;

public class GenomicVariationsEntryType extends EntryType {
    public GenomicVariationsEntryType() {
        this("/g_variants", "POST");
    }

    public GenomicVariationsEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new GenomicVariationsAstNodeListConverter();
        query = new GenomicVariationsQuery();
        groupAdmin = new GenomicVariationsGroupAdmin();
    }
}
