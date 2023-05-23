package de.samply.lens_beacon_service.entrytype.biosamples;

import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.entrytype.biosamples.ast2filter.BiosamplesAstNodeListConverter;

public class BiosamplesEntryType extends EntryType {
    public BiosamplesEntryType() {
        this("/biosamples", "POST");
    }

    public BiosamplesEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new BiosamplesAstNodeListConverter();
        query = new BiosamplesQuery();
        groupAdmin = new BiosamplesGroupAdmin();
    }
}
