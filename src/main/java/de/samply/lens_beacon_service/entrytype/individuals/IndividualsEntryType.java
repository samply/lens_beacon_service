package de.samply.lens_beacon_service.entrytype.individuals;

import de.samply.lens_beacon_service.entrytype.individuals.ast2filter.AstNodeListConverterIndividuals;
import de.samply.lens_beacon_service.entrytype.EntryType;

public class IndividualsEntryType extends EntryType {
    public IndividualsEntryType() {
        this("/individuals", "POST");
    }

    public IndividualsEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterIndividuals();
        query = new QueryIndividuals();
        groupAdmin = new IndividualsGroupAdmin();
    }
}
