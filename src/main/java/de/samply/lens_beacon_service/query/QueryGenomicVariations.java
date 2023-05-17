package de.samply.lens_beacon_service.query;

import de.samply.lens_beacon_service.beacon.model.BeaconSite;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryGenomicVariations extends Query {
    /**
     * Run a query on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param site
     * @param measureReportAdmin
     */
    public void runQueryAtSite(BeaconSite site,
                                MeasureReportAdmin measureReportAdmin) {
        Integer count = site.beaconQueryService.runBeaconEntryTypeQueryAtSite(site.genomicVariations, site.genomicVariations.baseFilters);
        measureReportAdmin.genomicVariationsGroupAdmin.setCount(count);
    }
}
