package de.samply.lens_beacon_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.beacon.BeaconResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Run a query. */

@Slf4j
public class QueryService {
    private Map<String, String> sites = new HashMap<String, String>();

    public QueryService() {
        // These should be put into a configuration file or some kind of
        // registration service.
        sites.put("HD Cineca", "http://beacon:5050/api");
//        sites.put("EGA Cineca", "https://ega-archive.org/beacon-apis/cineca");
//        sites.put("Molgenis", "https://mutatiedatabases.molgeniscloud.org/api/beacon");
//        sites.put("RDC playground", "https://playground.rd-connect.eu/beacon2/api");
//        sites.put("Progenetix", "https://progenetix.org/beacon");
    }

    public String runQuery() {
        return runQuery(new LensQuery());
    }

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
        for (String siteName: sites.keySet()) {
            Result result = new Result(siteName, "PLACEHOLDER" + siteName);
            results.add(result);
        }

        String jsonResults = "";
        try {
            jsonResults = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(results);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while converting an object into JSON");
            e.printStackTrace();
        }

        for (String siteName: sites.keySet()) {
            String jsonMeasure = runQueryAtSite(sites.get(siteName), beaconFiltersIndividuals, beaconFiltersBiosamples, beaconFiltersGenomicVariations);
            jsonResults = jsonResults.replaceAll("\"PLACEHOLDER" + siteName + "\"", "\n" + jsonMeasure.replaceAll("^", "        "));
        }

        //log.info("\nAfter string replace: jsonResults: " + jsonResults);

        return jsonResults;
    }

    private String runQueryAtSite(String siteUrl,
                                  List<BeaconFilter> beaconFiltersIndividuals,
                                  List<BeaconFilter> beaconFiltersBiosamples,
                                  List<BeaconFilter> beaconFiltersGenomicVariations) {
        BeaconQueryService beaconQueryService = new BeaconQueryService(siteUrl);

        Integer countIndividuals = -1;
        try {
            log.info("Send query 'individuals'");
            BeaconResponse response = beaconQueryService.postIndividuals(beaconFiltersIndividuals);
            log.info("Extract count 'individuals'");
            countIndividuals = extractCountFromBeaconResponse(response);
            log.info("Finished query 'individuals'");
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon individuals: " + e.getMessage());
        }
        Integer countBiosamples = -1;
        try {
            countBiosamples = (Integer) beaconQueryService.postBiosamples(beaconFiltersBiosamples).responseSummary.get("numTotalResults");
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon biosamples, message: " + e.getMessage());
            log.error("runQuery: problem with Beacon biosamples, string: " + e.toString());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log.error("runQuery: problem with Beacon biosamples, trace: " + sw.toString());
        }
        Integer countGenomicVariations = -1;
        try {
            countGenomicVariations = (Integer) beaconQueryService.postGenomicVariations(beaconFiltersGenomicVariations).responseSummary.get("numTotalResults");
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon genomic variants: " + e.getMessage());
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
        if (response.responseSummary == null)
            return -1;
        // If there is no results count but "exists" is true, return 1.
        // Rationale: if results exist, then there must be at least one.
        if (!response.responseSummary.containsKey("numTotalResults") &&
                response.responseSummary.containsKey("exists")) {
            if (response.responseSummary.get("exists").equals("true"))
                return 1;
            else
                return 0;
        }
        // We have a results count, so cast it to Integer and return it.
        return (Integer) response.responseSummary.get("numTotalResults");
    }
    public String runQueryOld(LensQuery lensQuery) {
        List<LensQuery> lensQueryLeafNodeList = new LensQueryLeafPicker().crawl(lensQuery);
        List<BeaconFilter> beaconFiltersIndividuals = new ConvertIndividuals().convert(lensQueryLeafNodeList);
        List<BeaconFilter> beaconFiltersBiosamples = new ConvertBiosamples().convert(lensQueryLeafNodeList);

        try {
            log.info("\nlensQuery: " + lensQuery);
            log.info("\njsonLensQueryLeafNodeList: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(lensQueryLeafNodeList));
            log.info("\nbeaconFiltersIndividuals: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(beaconFiltersIndividuals));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        BeaconQueryService beaconQueryService = new BeaconQueryService("http://beacon:5050/api");
        Integer countIndividuals = -1;
        try {
            countIndividuals = (Integer) beaconQueryService.postIndividuals(beaconFiltersIndividuals).responseSummary.get("numTotalResults");
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon individuals: " + e.getMessage());
        }
        Integer countBiosamples = -1;
        try {
            countBiosamples = (Integer) beaconQueryService.postBiosamples(beaconFiltersBiosamples).responseSummary.get("numTotalResults");
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon biosamples: " + e.getMessage());
        }
        Integer countGenomicVariations = -1;
        try {
            countGenomicVariations = (Integer) beaconQueryService.postGenomicVariations(new ArrayList<BeaconFilter>()).responseSummary.get("numTotalResults");
        } catch (Exception e) {
            log.error("runQuery: problem with Beacon genomic variants: " + e.getMessage());
        }
        log.info("countIndividuals: " + countIndividuals);
        log.info("countBiosamples: " + countBiosamples);
        log.info("countGenomicVariations: " + countGenomicVariations);
        MeasureReportGenerator measureReportGenerator = new MeasureReportGenerator();
        measureReportGenerator.setPatientCount(countIndividuals);
        measureReportGenerator.setSpecimenCount(countBiosamples);
        measureReportGenerator.setGenomicVariationCount(countGenomicVariations);
        String jsonMeasure = measureReportGenerator.toString();
        String siteName = "HD Cineca";
        Result result = new Result(siteName, "PLACEHOLDER" + siteName);
        List<Result> results = new ArrayList<Result>();
        results.add(result);
        String jsonResults = "";
        try {
            jsonResults = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(results);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while converting an object into JSON");
            e.printStackTrace();
        }
        jsonResults = jsonResults.replaceAll("\"PLACEHOLDER" + siteName + "\"", "\n" + jsonMeasure.replaceAll("^", "        "));
        log.info("\njsonResults: " + jsonResults);
        return jsonResults;
    }
}
