package de.samply.lens_beacon_service.entrytype.genomicVariations;


import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.entrytype.genomicVariations.convert.AstNodeListConverterGenomicVariations;

public class GenomicVariationsEntryType extends EntryType {
    public GenomicVariationsEntryType() {
        super("/g_variants", "POST");
    }

    public GenomicVariationsEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterGenomicVariations();
        query = new QueryGenomicVariations();
        groupAdmin = new GenomicVariationsGroupAdmin();
    }
}
