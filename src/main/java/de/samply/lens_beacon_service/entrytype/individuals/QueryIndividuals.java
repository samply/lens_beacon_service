package de.samply.lens_beacon_service.entrytype.individuals;

import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;
import de.samply.lens_beacon_service.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QueryIndividuals extends Query {
    /**
     * Run queries for stratifiers on the genomicVariations endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param entryType
     * @param measureReportAdmin
     */
    public void runStratifierQueriesAtSite(BeaconQueryService beaconQueryService, EntryType entryType, MeasureReportAdmin measureReportAdmin) {
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

        ((IndividualsGroupAdmin)entryType.groupAdmin).setGenderCounts((femaleCount>=0)?femaleCount:0, (maleCount>=0)?maleCount:0);
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
        for (String ethnicity : NameOntologyMaps.ethnicityNameNcit.keySet()) {
            Integer count = beaconQueryService.runFilterQueryAtSite(entryType,"id", NameOntologyMaps.ethnicityNameNcit.get(ethnicity));
            ethnicityCounts.put(ethnicity, count);
        }
        ((IndividualsGroupAdmin)entryType.groupAdmin).setEthnicityCounts(ethnicityCounts);
    }
}
