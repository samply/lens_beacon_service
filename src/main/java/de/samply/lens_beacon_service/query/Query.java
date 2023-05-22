package de.samply.lens_beacon_service.query;

import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.entrytype.EntryType;
import org.hl7.fhir.r4.model.MeasureReport;

import java.util.HashMap;
import java.util.Map;

public abstract class Query {
    /**
     * Run queries for stratifiers on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param beaconQueryService
     * @param entryType
     */
    public abstract void runStratifierQueriesAtSite(BeaconQueryService beaconQueryService, EntryType entryType);

    /**
     * Run a query on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * @param entryType
     * @return The measure report group where the query results were stored.
     */
    public MeasureReport.MeasureReportGroupComponent runQueryAtSite(BeaconQueryService beaconQueryService, EntryType entryType) {
        // Get and store the population count. This is always needed.
        Integer count = beaconQueryService.runBeaconEntryTypeQueryAtSite(entryType, entryType.baseFilters);
        entryType.groupAdmin.setCount(count);

        runStratifierQueriesAtSite(beaconQueryService, entryType);

        return entryType.groupAdmin.group;
    }

    /**
     * Runs the query for a given stratifier.
     *
     * @param beaconQueryService
     * @param entryType
     * @param nameOntologyMap
     */
    protected Map<String, Integer> runStratifierQueryAtSite(BeaconQueryService beaconQueryService,
                                                            EntryType entryType,
                                                            Map<String, String> nameOntologyMap) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (String name : nameOntologyMap.keySet()) {
            Integer count = beaconQueryService.runFilterQueryAtSite(entryType,"id", nameOntologyMap.get(name));
            counts.put(name, count);
        }

        return counts;
    }
}
