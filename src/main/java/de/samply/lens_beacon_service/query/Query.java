package de.samply.lens_beacon_service.query;

import de.samply.lens_beacon_service.beacon.model.BeaconSite;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;

public abstract class Query {
    public abstract void runQueryAtSite(BeaconSite site, MeasureReportAdmin measureReportAdmin);
}
