package de.samply.lens_beacon_service.entrytype.genomicVariations;

import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;
import de.samply.lens_beacon_service.query.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryGenomicVariations extends Query {
    /**
     * Run queries for stratifiers on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param entryType
     * @param measureReportAdmin
     */
    public void runStratifierQueriesAtSite(BeaconQueryService beaconQueryService, EntryType entryType, MeasureReportAdmin measureReportAdmin) {
    }
}
