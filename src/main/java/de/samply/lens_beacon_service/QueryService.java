package de.samply.lens_beacon_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.beacon.*;
import de.samply.lens_beacon_service.convert.ConvertBiosamples;
import de.samply.lens_beacon_service.convert.ConvertGenetics;
import de.samply.lens_beacon_service.convert.ConvertIndividuals;
import de.samply.lens_beacon_service.lens.LensQuery;
import de.samply.lens_beacon_service.lens.LensQueryLeafPicker;
import de.samply.lens_beacon_service.measurereport.MeasureReportGenerator;
import de.samply.lens_beacon_service.measurereport.Result;
import de.samply.lens_beacon_service.measurereport.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Run a query. */

@Slf4j
public class QueryService {
    public String runQuery(LensQuery lensQuery) {
        List<LensQuery> lensQueryLeafNodeList = new LensQueryLeafPicker().crawl(lensQuery);
        List<BeaconFilter> beaconFiltersIndividuals = new ConvertIndividuals().convert(lensQueryLeafNodeList);
        List<BeaconFilter> beaconFiltersBiosamples = new ConvertBiosamples().convert(lensQueryLeafNodeList);
        List<BeaconFilter> beaconFiltersGenetics = new ConvertGenetics().convert(lensQueryLeafNodeList);

        try {
            log.info("\nlensQuery: " + lensQuery);
            log.info("\njsonLensQueryLeafNodeList: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(lensQueryLeafNodeList));
            log.info("\nbeaconFiltersIndividuals: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(beaconFiltersIndividuals));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Result> results = new ArrayList<Result>();
        for (BeaconSite site: BeaconSites.getSites()) {
            String siteName = site.name;
            String siteUrl = site.url;
            Result result = new Result(siteName, siteUrl, "PLACEHOLDER" + siteName);
            results.add(result);
        }

        String jsonResults = "";
        try {
            jsonResults = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(results);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while converting an object into JSON");
            e.printStackTrace();
        }

        for (BeaconSite site: BeaconSites.getSites()) {
            String siteName = site.name;
            String jsonMeasure = runQueryAtSite(site, beaconFiltersIndividuals, beaconFiltersBiosamples, beaconFiltersGenetics);
            jsonResults = jsonResults.replaceAll("\"PLACEHOLDER" + siteName + "\"", "\n" + jsonMeasure.replaceAll("^", "        "));
        }

        return jsonResults;
    }

    private String runQueryAtSite(BeaconSite site,
                                  List<BeaconFilter> beaconFiltersIndividuals,
                                  List<BeaconFilter> beaconFiltersBiosamples,
                                  List<BeaconFilter> beaconFiltersGenomicVariations) {
        BeaconQueryService beaconQueryService = site.beaconQueryService;
        MeasureReportGenerator measureReportGenerator = new MeasureReportGenerator();

        runIndividualsQueryAtSite(site, beaconQueryService, measureReportGenerator, beaconFiltersIndividuals);
        runBiosamplesQueryAtSite(site, beaconQueryService, measureReportGenerator, beaconFiltersBiosamples);
        Integer geneticsCount = runBeaconEntryTypeQueryAtSite("genetics", site.genomicVariations, beaconQueryService, beaconFiltersGenomicVariations);
        measureReportGenerator.setGroupPopulationCount("genetics", geneticsCount);

        String jsonMeasure = measureReportGenerator.toString();
        return jsonMeasure;
    }

    private void runIndividualsQueryAtSite(BeaconSite site,
                                           BeaconQueryService beaconQueryService,
                                           MeasureReportGenerator measureReportGenerator,
                                           List<BeaconFilter> beaconFilters) {
        String groupName = "patients";
        BeaconEntryType beaconEntryType = site.individuals;
        Integer count = runBeaconEntryTypeQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters);
        measureReportGenerator.setGroupPopulationCount(groupName, count);
        runIndividualsGenderQueryAtSite(site, beaconQueryService, measureReportGenerator, beaconFilters);
        runIndividualsEthnicityQueryAtSite(site, beaconQueryService, measureReportGenerator, beaconFilters);
    }

    private void runIndividualsGenderQueryAtSite(BeaconSite site,
                                                 BeaconQueryService beaconQueryService,
                                                 MeasureReportGenerator measureReportGenerator,
                                                 List<BeaconFilter> beaconFilters) {
        String groupName = "patients";
        BeaconEntryType beaconEntryType = site.individuals;

        Integer femaleCount = runFilterQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters, "id", "NCIT:C16576");
        Integer maleCount = runFilterQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters, "id", "NCIT:C20197");

        measureReportGenerator.setPatientGenderCounts((femaleCount>=0)?femaleCount:0, (maleCount>=0)?maleCount:0);
    }

    private void runIndividualsEthnicityQueryAtSite(BeaconSite site,
                                                 BeaconQueryService beaconQueryService,
                                                 MeasureReportGenerator measureReportGenerator,
                                                 List<BeaconFilter> beaconFilters) {
        String groupName = "patients";
        BeaconEntryType beaconEntryType = site.individuals;

        Map<String, Integer> ethnicityCounts = new HashMap<String, Integer>();
        for (String ethnicity : Utils.getEthnicityNameNcit().keySet()) {
            Integer count = runFilterQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters, "id", Utils.getEthnicityNameNcit().get(ethnicity));
            log.info(ethnicity + ": " + count);
            ethnicityCounts.put(ethnicity, count);
        }
        measureReportGenerator.setPatientEthnicityCounts(ethnicityCounts);
    }

    private void runBiosamplesQueryAtSite(BeaconSite site,
                                           BeaconQueryService beaconQueryService,
                                           MeasureReportGenerator measureReportGenerator,
                                           List<BeaconFilter> beaconFilters) {
        log.info("Running biosample queries");
        String groupName = "specimen";
        BeaconEntryType beaconEntryType = site.biosamples;
        Integer count = runBeaconEntryTypeQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters);
        measureReportGenerator.setGroupPopulationCount(groupName, count);
        runBiosamplesTypeQueryAtSite(site, beaconQueryService, measureReportGenerator, beaconFilters);
    }

    private void runBiosamplesTypeQueryAtSite(BeaconSite site,
                                                 BeaconQueryService beaconQueryService,
                                                 MeasureReportGenerator measureReportGenerator,
                                                 List<BeaconFilter> beaconFilters) {
        log.info("Running biosample type queries");
        String groupName = "specimen";
        BeaconEntryType beaconEntryType = site.biosamples;

        Integer bloodCount = runFilterQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters, "id", "UBERON:0000178");
        Integer bloodSerumCount = runFilterQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters, "id", "UBERON:0001977");
        Integer bloodPlasmaCount = runFilterQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters, "id", "UBERON:0001969");
        Integer lymphCount = runFilterQueryAtSite(groupName, beaconEntryType, beaconQueryService, beaconFilters, "id", "UBERON:0002391");

        log.info("bloodCount: " + bloodCount);
        log.info("bloodSerumCount: " + bloodSerumCount);
        log.info("bloodPlasmaCount: " + bloodPlasmaCount);
        log.info("lymphCount: " + lymphCount);

        measureReportGenerator.setSpecimenTypeCounts((bloodCount>=0)?bloodCount:0,
                (bloodSerumCount>=0)?bloodSerumCount:0,
                (bloodPlasmaCount>=0)?bloodPlasmaCount:0,
                (lymphCount>=0)?lymphCount:0);
    }

    private Integer runFilterQueryAtSite(String groupName,
                                         BeaconEntryType beaconEntryType,
                                         BeaconQueryService beaconQueryService,
                                         List<BeaconFilter> beaconFilters,
                                         String filterName,
                                         String filterValue) {
        List<BeaconFilter> localFilters = new ArrayList<BeaconFilter>(beaconFilters);
        localFilters.add(new BeaconFilter(filterName, filterValue));
        Integer count = runBeaconEntryTypeQueryAtSite(groupName, beaconEntryType, beaconQueryService, localFilters);
        
        return count;
    }

    private Integer runBeaconEntryTypeQueryAtSite(String groupName,
                                           BeaconEntryType beaconEntryType,
                                           BeaconQueryService beaconQueryService,
                                           List<BeaconFilter> beaconFilters) {
        Integer count = -1;
        try {
            BeaconResponse response = beaconQueryService.queryEntry(beaconEntryType, beaconFilters);
            count = extractCountFromBeaconResponse(response);
        } catch (Exception e) {
            log.error("runQuery: problem with " + groupName + ", trace: " + traceFromException(e));
        }
        log.info("count " + groupName + ": " + count);
        //measureReportGenerator.setGroupPopulationCount(groupName, count);
        return count;
    }

    private String traceFromException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    private Integer extractCountFromBeaconResponse(BeaconResponse response) {
        // If there is no response, return -1 (unknown)
        if (response == null || response.responseSummary == null)
            return -1;
        // If there is no results count but "exists" is true, return 1.
        // Rationale: if results exist, then there must be at least one.
        if (!response.responseSummary.containsKey("numTotalResults") &&
                response.responseSummary.containsKey("exists")) {

            log.info("exists: " + response.responseSummary.get("exists"));
            log.info("exists tostring: " + response.responseSummary.get("exists").toString());
            log.info("exists tostring equals true: " + response.responseSummary.get("exists").toString().equals("true"));
            if (response.responseSummary.get("exists").toString().equals("true")) {
                log.info("Returning 1");
                return 1;
            } else {
                log.info("Returning 0");
                return 0;
            }
        }
        // We have a results count, so cast it to Integer and return it.
        return (Integer) response.responseSummary.get("numTotalResults");
    }
}
