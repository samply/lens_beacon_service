package de.samply.lens_beacon_service;

import de.samply.lens_beacon_service.convert.biosamples.AstNodeListConverterBiosamples;
import de.samply.lens_beacon_service.measurereport.group.BiosamplesGroupAdmin;
import de.samply.lens_beacon_service.query.QueryBiosamples;

public class BiosamplesEntryType extends EntryType {
    public BiosamplesEntryType() {
        super("/biosamples", "POST");
    }

    public BiosamplesEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterBiosamples();
        groupAdmin = new BiosamplesGroupAdmin();
        query = new QueryBiosamples();
    }
}