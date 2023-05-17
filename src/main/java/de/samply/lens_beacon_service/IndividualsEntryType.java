package de.samply.lens_beacon_service;

import de.samply.lens_beacon_service.convert.individuals.AstNodeListConverterIndividuals;
import de.samply.lens_beacon_service.measurereport.group.IndividualsGroupAdmin;
import de.samply.lens_beacon_service.query.QueryIndividuals;

public class IndividualsEntryType extends EntryType {
    public IndividualsEntryType() {
        super("/individuals", "POST");
    }

    public IndividualsEntryType(String uri, String method) {
        super(uri, method);
        astNodeListConverter = new AstNodeListConverterIndividuals();
        groupAdmin = new IndividualsGroupAdmin();
        query = new QueryIndividuals();
    }
}
