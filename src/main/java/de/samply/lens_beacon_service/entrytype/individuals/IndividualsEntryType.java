package de.samply.lens_beacon_service.entrytype.individuals;

import de.samply.lens_beacon_service.entrytype.individuals.convert.AstNodeListConverterIndividuals;
import de.samply.lens_beacon_service.entrytype.EntryType;

public class IndividualsEntryType extends EntryType {
    public IndividualsEntryType() {
        super("/individuals", "POST");
    }

    public IndividualsEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterIndividuals();
        query = new QueryIndividuals();
    }
}
