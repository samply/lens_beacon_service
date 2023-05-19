package de.samply.lens_beacon_service.entrytype.biosamples;

import de.samply.lens_beacon_service.entrytype.biosamples.convert.AstNodeListConverterBiosamples;
import de.samply.lens_beacon_service.entrytype.EntryType;

public class BiosamplesEntryType extends EntryType {
    public BiosamplesEntryType() {
        super("/biosamples", "POST");
    }

    public BiosamplesEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterBiosamples();
        query = new QueryBiosamples();
    }
}
