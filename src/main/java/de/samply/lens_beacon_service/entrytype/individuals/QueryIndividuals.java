package de.samply.lens_beacon_service.entrytype.individuals;

import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.Utils;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;
import de.samply.lens_beacon_service.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QueryIndividuals extends Query {
    /**
     * Run a query on the individuals endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param entryType
     * @param measureReportAdmin
     */
    public void runQueryAtSite(BeaconQueryService beaconQueryService, EntryType entryType, MeasureReportAdmin measureReportAdmin) {
        Integer count = beaconQueryService.runBeaconEntryTypeQueryAtSite(entryType, entryType.baseFilters);
        measureReportAdmin.individualsGroupAdmin.setCount(count);
        runIndividualsGenderQueryAtSite(beaconQueryService, entryType, measureReportAdmin);
        runIndividualsEthnicityQueryAtSite(beaconQueryService, entryType, measureReportAdmin);
    }

    /**
     * Runs the query for the gender stratifier.
     *
     * @param beaconQueryService
     * @param entryType
     * @param measureReportAdmin
     */
    private void runIndividualsGenderQueryAtSite(BeaconQueryService beaconQueryService,
                                                 EntryType entryType,
                                                 MeasureReportAdmin measureReportAdmin) {
        Integer femaleCount = beaconQueryService.runFilterQueryAtSite(entryType, "id", "NCIT:C16576");
        Integer maleCount = beaconQueryService.runFilterQueryAtSite(entryType, "id", "NCIT:C20197");

        measureReportAdmin.individualsGroupAdmin.setGenderCounts((femaleCount>=0)?femaleCount:0, (maleCount>=0)?maleCount:0);
    }

    /**
     * Runs the query for the ethnicity stratifier.
     *
     * @param beaconQueryService
     * @param entryType
     * @param measureReportAdmin
     */
    private void runIndividualsEthnicityQueryAtSite(BeaconQueryService beaconQueryService,
                                                    EntryType entryType,
                                                    MeasureReportAdmin measureReportAdmin) {
        Map<String, Integer> ethnicityCounts = new HashMap<String, Integer>();
        for (String ethnicity : Utils.getEthnicityNameNcit().keySet()) {
            Integer count = beaconQueryService.runFilterQueryAtSite(entryType,"id", Utils.getEthnicityNameNcit().get(ethnicity));
            ethnicityCounts.put(ethnicity, count);
        }
        measureReportAdmin.individualsGroupAdmin.setEthnicityCounts(ethnicityCounts);
    }
}
