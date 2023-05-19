package de.samply.lens_beacon_service.query;

import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;

public abstract class Query {
    /**
     * Run queries for stratifiers on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param entryType
     * @param measureReportAdmin
     */
    public abstract void runStratifierQueriesAtSite(BeaconQueryService beaconQueryService, EntryType entryType, MeasureReportAdmin measureReportAdmin);

    /**
     * Run a query on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param entryType
     * @param measureReportAdmin
     */
    public void runQueryAtSite(BeaconQueryService beaconQueryService, EntryType entryType, MeasureReportAdmin measureReportAdmin) {
        measureReportAdmin.measureReport.addGroup(entryType.groupAdmin.group);
        // Get and store the population count. This is always needed.
        Integer count = beaconQueryService.runBeaconEntryTypeQueryAtSite(entryType, entryType.baseFilters);
        entryType.groupAdmin.setCount(count);

        runStratifierQueriesAtSite(beaconQueryService, entryType, measureReportAdmin);
    }
}
