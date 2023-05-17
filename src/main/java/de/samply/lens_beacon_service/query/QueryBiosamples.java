package de.samply.lens_beacon_service.query;

import de.samply.lens_beacon_service.beacon.model.BeaconSite;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryBiosamples extends Query {
    /**
     * Run a query on the biosamples endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param site
     * @param measureReportAdmin
     */
    public void runQueryAtSite(BeaconSite site,
                               MeasureReportAdmin measureReportAdmin) {
        Integer count = site.beaconQueryService.runBeaconEntryTypeQueryAtSite(site.biosamples, site.biosamples.baseFilters);
        measureReportAdmin.biosamplesGroupAdmin.setCount(count);
        runBiosamplesTypeQueryAtSite(site, measureReportAdmin);
    }

    /**
     * Runs the query for the sample type stratifier.
     *
     * @param site
     * @param measureReportAdmin
     */
    private void runBiosamplesTypeQueryAtSite(BeaconSite site,
                                                 MeasureReportAdmin measureReportAdmin) {
        Integer bloodCount = site.beaconQueryService.runFilterQueryAtSite(site.biosamples, "id", "UBERON:0000178");
        Integer bloodSerumCount = site.beaconQueryService.runFilterQueryAtSite(site.biosamples, "id", "UBERON:0001977");
        Integer bloodPlasmaCount = site.beaconQueryService.runFilterQueryAtSite(site.biosamples, "id", "UBERON:0001969");
        Integer lymphCount = site.beaconQueryService.runFilterQueryAtSite(site.biosamples, "id", "UBERON:0002391");

        measureReportAdmin.biosamplesGroupAdmin.setTypeCounts((bloodCount>=0)?bloodCount:0,
                (bloodSerumCount>=0)?bloodSerumCount:0,
                (bloodPlasmaCount>=0)?bloodPlasmaCount:0,
                (lymphCount>=0)?lymphCount:0);
    }
}
