package de.samply.lens_beacon_service.entrytype;

import de.samply.lens_beacon_service.convert.individuals.AstNodeListConverterIndividuals;
import de.samply.lens_beacon_service.query.QueryIndividuals;

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
