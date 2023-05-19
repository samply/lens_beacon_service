package de.samply.lens_beacon_service.entrytype;


import de.samply.lens_beacon_service.convert.genomicVariations.AstNodeListConverterGenomicVariations;
import de.samply.lens_beacon_service.query.QueryGenomicVariations;

public class GenomicVariationsEntryType extends EntryType {
    public GenomicVariationsEntryType() {
        super("/g_variants", "POST");
    }

    public GenomicVariationsEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterGenomicVariations();
        query = new QueryGenomicVariations();
    }
}
