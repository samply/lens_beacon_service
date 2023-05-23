package de.samply.lens_beacon_service.entrytype.biosamples;

import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class BiosamplesQuery extends Query {
    /**
     * Run queries for stratifiers on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * @param entryType
     */
    public void runStratifierQueriesAtSite(BeaconQueryService beaconQueryService, EntryType entryType) {
        Map<String, Integer> counts;
        // Runs the query for the gender stratifier.
        counts =  runStratifierQueryAtSite(beaconQueryService, entryType, BiosamplesNameOntologyMaps.biosmapleTypeUberon);
        ((BiosamplesGroupAdmin)entryType.groupAdmin).setBiosampleTypeCounts(counts);
    }
}
