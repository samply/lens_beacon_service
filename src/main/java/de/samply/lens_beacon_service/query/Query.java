package de.samply.lens_beacon_service.query;

import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;

public abstract class Query {
    public abstract void runQueryAtSite(BeaconQueryService beaconQueryService, EntryType entryType, MeasureReportAdmin measureReportAdmin);
}
