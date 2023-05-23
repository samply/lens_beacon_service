package de.samply.lens_beacon_service.entrytype.genomicVariations;


import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.entrytype.genomicVariations.ast2filter.AstNodeListConverterGenomicVariations;

public class GenomicVariationsEntryType extends EntryType {
    public GenomicVariationsEntryType() {
        this("/g_variants", "POST");
    }

    public GenomicVariationsEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterGenomicVariations();
        query = new QueryGenomicVariations();
        groupAdmin = new GenomicVariationsGroupAdmin();
    }
}
