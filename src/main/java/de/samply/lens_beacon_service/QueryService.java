package de.samply.lens_beacon_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.beacon.*;
import de.samply.lens_beacon_service.convert.ConvertBiosamples;
import de.samply.lens_beacon_service.convert.ConvertIndividuals;
import de.samply.lens_beacon_service.lens.LensQuery;
import de.samply.lens_beacon_service.lens.LensQueryLeafPicker;
import de.samply.lens_beacon_service.measurereport.MeasureReportGenerator;
import de.samply.lens_beacon_service.measurereport.Result;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/* Run a query. */

@Slf4j
public class QueryService {
    public String runQuery(LensQuery lensQuery) {
        List<LensQuery> lensQueryLeafNodeList = new LensQueryLeafPicker().crawl(lensQuery);
        List<BeaconFilter> beaconFiltersIndividuals = new ConvertIndividuals().convert(lensQueryLeafNodeList);
        List<BeaconFilter> beaconFiltersBiosamples = new ConvertBiosamples().convert(lensQueryLeafNodeList);
        List<BeaconFilter> beaconFiltersGenomicVariations = new ArrayList<BeaconFilter>();

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
            String jsonMeasure = runQueryAtSite(site, beaconFiltersIndividuals, beaconFiltersBiosamples, beaconFiltersGenomicVariations);
            jsonResults = jsonResults.replaceAll("\"PLACEHOLDER" + siteName + "\"", "\n" + jsonMeasure.replaceAll("^", "        "));
        }

        return jsonResults;
    }

    private String runQueryAtSite(BeaconSite site,
                                  List<BeaconFilter> beaconFiltersIndividuals,
                                  List<BeaconFilter> beaconFiltersBiosamples,
                                  List<BeaconFilter> beaconFiltersGenomicVariations) {
        BeaconQueryService beaconQueryService = site.beaconQueryService;

        Integer countIndividuals = -1;
        try {
            log.info("Send query 'individuals'");
            BeaconResponse response = beaconQueryService.queryEntry(site.individuals, beaconFiltersIndividuals);
            log.info("Extract count 'individuals'");
            countIndividuals = extractCountFromBeaconResponse(response);
            log.info("Finished query 'individuals'");
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon individuals: " + e.getMessage());
        }
        Integer countBiosamples = -1;
        try {
            BeaconResponse response = beaconQueryService.queryEntry(site.biosamples, beaconFiltersBiosamples);
            log.info("Extract count 'biosamples'");
            countBiosamples = extractCountFromBeaconResponse(response);
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon biosamples, message: " + e.getMessage());
        }
        Integer countGenomicVariations = -1;
        try {
            BeaconResponse response = beaconQueryService.queryEntry(site.genomicVariations, beaconFiltersGenomicVariations);
            log.info("Extract count 'genomicVariations'");
            countGenomicVariations = extractCountFromBeaconResponse(response);
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon genomic variants: " + e.getMessage());
            log.error("runQuery: problem with Beacon biosamples, string: " + e.toString());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log.error("runQuery: problem with Beacon biosamples, trace: " + sw.toString());
        }
        log.info("countIndividuals: " + countIndividuals);
        log.info("countBiosamples: " + countBiosamples);
        log.info("countGenomicVariations: " + countGenomicVariations);
        MeasureReportGenerator measureReportGenerator = new MeasureReportGenerator();
        measureReportGenerator.setPatientCount(countIndividuals);
        measureReportGenerator.setSpecimenCount(countBiosamples);
        measureReportGenerator.setGenomicVariationCount(countGenomicVariations);
        String jsonMeasure = measureReportGenerator.toString();
        return jsonMeasure;
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
