package de.samply.lens_beacon_service.entrytype.individuals;

import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class QueryIndividuals extends Query {
    /**
     * Run queries for stratifiers on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * @param entryType
     */
    public void runStratifierQueriesAtSite(BeaconQueryService beaconQueryService, EntryType entryType) {
        Map<String, Integer> counts;
        // Runs the query for the gender stratifier.
        counts =  runStratifierQueryAtSite(beaconQueryService, entryType, NameOntologyMaps.genderNameNcit);
        ((IndividualsGroupAdmin)entryType.groupAdmin).setGenderCounts(counts);

        // Runs the query for the ethnicity stratifier.
        counts =  runStratifierQueryAtSite(beaconQueryService, entryType, NameOntologyMaps.ethnicityNameNcit);
        ((IndividualsGroupAdmin)entryType.groupAdmin).setEthnicityCounts(counts);
    }
}
