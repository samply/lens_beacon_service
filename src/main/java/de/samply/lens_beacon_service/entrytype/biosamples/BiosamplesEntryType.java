package de.samply.lens_beacon_service.entrytype.biosamples;

import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.entrytype.biosamples.ast2filter.AstNodeListConverterBiosamples;

public class BiosamplesEntryType extends EntryType {
    public BiosamplesEntryType() {
        this("/biosamples", "POST");
    }

    public BiosamplesEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterBiosamples();
        query = new QueryBiosamples();
        groupAdmin = new BiosamplesGroupAdmin();
    }
}
