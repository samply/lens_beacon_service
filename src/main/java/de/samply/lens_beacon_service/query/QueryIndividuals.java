package de.samply.lens_beacon_service.query;

import de.samply.lens_beacon_service.Utils;
import de.samply.lens_beacon_service.beacon.model.BeaconSite;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QueryIndividuals {
    /**
     * Run a query on the individuals endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param site
     * @param measureReportAdmin
     */
    public static void runQueryAtSite(BeaconSite site,
                               MeasureReportAdmin measureReportAdmin) {
        Integer count = site.beaconQueryService.runBeaconEntryTypeQueryAtSite(site.individuals, site.individuals.baseFilters);
        measureReportAdmin.individualsGroupAdmin.setCount(count);
        runIndividualsGenderQueryAtSite(site, measureReportAdmin);
        runIndividualsEthnicityQueryAtSite(site, measureReportAdmin);
    }

    /**
     * Runs the query for the gender stratifier.
     *
     * @param site
     * @param measureReportAdmin
     */
    private static void runIndividualsGenderQueryAtSite(BeaconSite site,
                                                 MeasureReportAdmin measureReportAdmin) {
        Integer femaleCount = site.beaconQueryService.runFilterQueryAtSite(site.individuals, "id", "NCIT:C16576");
        Integer maleCount = site.beaconQueryService.runFilterQueryAtSite(site.individuals, "id", "NCIT:C20197");

        measureReportAdmin.individualsGroupAdmin.setGenderCounts((femaleCount>=0)?femaleCount:0, (maleCount>=0)?maleCount:0);
    }

    /**
     * Runs the query for the ethnicity stratifier.
     *
     * @param site
     * @param measureReportAdmin
     */
    private static void runIndividualsEthnicityQueryAtSite(BeaconSite site,
                                                 MeasureReportAdmin measureReportAdmin) {
        Map<String, Integer> ethnicityCounts = new HashMap<String, Integer>();
        for (String ethnicity : Utils.getEthnicityNameNcit().keySet()) {
            Integer count = site.beaconQueryService.runFilterQueryAtSite(site.individuals,"id", Utils.getEthnicityNameNcit().get(ethnicity));
            ethnicityCounts.put(ethnicity, count);
        }
        measureReportAdmin.individualsGroupAdmin.setEthnicityCounts(ethnicityCounts);
    }
}
