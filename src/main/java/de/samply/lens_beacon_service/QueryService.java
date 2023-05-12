package de.samply.lens_beacon_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.beacon.*;
import de.samply.lens_beacon_service.convert.ConvertBiosamples;
import de.samply.lens_beacon_service.convert.ConvertGenetics;
import de.samply.lens_beacon_service.convert.ConvertIndividuals;
import de.samply.lens_beacon_service.lens.LensAstLeafPicker;
import de.samply.lens_beacon_service.lens.LensAstNode;
import de.samply.lens_beacon_service.lens.LensResult;
import de.samply.lens_beacon_service.measurereport.MeasureReportGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the bridge between the Lens and the Beacon worlds.
 *
 * It performs the conversion from Lens AST queries into Beacon filters,
 * runs the queries against Beacon and then packs the results into
 * FHIR-style measure reports, which are serialized and sent back to
 * Lens.
 */

@Slf4j
public class QueryService {
    /**
     * Takes an AST query from Lens and returns a set of results containing measure reports,
     * one per site.
     *
     * The query will be translated into a Beacon-friendly form and then run at each Beacon
     * site.
     *
     * @param lensAstNode AST query.
     * @return Serialized results.
     */
    public String runQuery(LensAstNode lensAstNode) {
        List<LensAstNode> lensAstNodeLeafNodeList = new LensAstLeafPicker().crawl(lensAstNode);
        List<BeaconFilter> beaconFiltersIndividuals = new ConvertIndividuals().convert(lensAstNodeLeafNodeList);
        List<BeaconFilter> beaconFiltersBiosamples = new ConvertBiosamples().convert(lensAstNodeLeafNodeList);
        List<BeaconFilter> beaconFiltersGenetics = new ConvertGenetics().convert(lensAstNodeLeafNodeList);

        try {
            log.info("\nlensAstNode: " + lensAstNode);
            log.info("\njsonLensQueryLeafNodeList: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(lensAstNodeLeafNodeList));
            log.info("\nbeaconFiltersIndividuals: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(beaconFiltersIndividuals));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<LensResult> lensResults = new ArrayList<LensResult>();
        for (BeaconSite site: BeaconSites.getSites()) {
            String siteName = site.name;
            String siteUrl = site.url;
            LensResult lensResult = new LensResult(siteName, siteUrl, "PLACEHOLDER" + siteName);
            lensResults.add(lensResult);
        }

        String jsonResults = "";
        try {
            jsonResults = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(lensResults);
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

    /**
     * Run the query at a single Beacon site.
     *
     * Actually, 3 queries will be run, for individuals, biosamples and variants, using the
     * relevant filter sets for each one respectively.
     *
     * The results from the 3 queries will be bundled together into a serialized measure report
     * and returned.
     *
     * @param site
     * @param beaconFiltersIndividuals
     * @param beaconFiltersBiosamples
     * @param beaconFiltersGenomicVariations
     * @return
     */
    private String runQueryAtSite(BeaconSite site,
                                  List<BeaconFilter> beaconFiltersIndividuals,
                                  List<BeaconFilter> beaconFiltersBiosamples,
                                  List<BeaconFilter> beaconFiltersGenomicVariations) {
        MeasureReportGenerator measureReportGenerator = new MeasureReportGenerator();

        runIndividualsQueryAtSite(site, measureReportGenerator, beaconFiltersIndividuals);
        runBiosamplesQueryAtSite(site, measureReportGenerator, beaconFiltersBiosamples);
        Integer geneticsCount = runBeaconEntryTypeQueryAtSite(site.genomicVariations, site.beaconQueryService, beaconFiltersGenomicVariations);
        measureReportGenerator.setGroupPopulationCount("genetics", geneticsCount);

        String jsonMeasure = measureReportGenerator.toString();
        return jsonMeasure;
    }

    /**
     * Run a query on the individuals endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportGenerator will be used to store the results of the query.
     *
     * @param site
     * @param measureReportGenerator
     * @param beaconFilters
     */
    private void runIndividualsQueryAtSite(BeaconSite site,
                                           MeasureReportGenerator measureReportGenerator,
                                           List<BeaconFilter> beaconFilters) {
        BeaconEntryType beaconEntryType = site.individuals;
        Integer count = runBeaconEntryTypeQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters);
        measureReportGenerator.setGroupPopulationCount("patients", count);
        runIndividualsGenderQueryAtSite(site, measureReportGenerator, beaconFilters);
        runIndividualsEthnicityQueryAtSite(site, measureReportGenerator, beaconFilters);
    }

    /**
     * Runs the query for the gender stratifier.
     *
     * @param site
     * @param measureReportGenerator
     * @param beaconFilters
     */
    private void runIndividualsGenderQueryAtSite(BeaconSite site,
                                                 MeasureReportGenerator measureReportGenerator,
                                                 List<BeaconFilter> beaconFilters) {
        BeaconEntryType beaconEntryType = site.individuals;

        Integer femaleCount = runFilterQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters, "id", "NCIT:C16576");
        Integer maleCount = runFilterQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters, "id", "NCIT:C20197");

        measureReportGenerator.setPatientGenderCounts((femaleCount>=0)?femaleCount:0, (maleCount>=0)?maleCount:0);
    }

    /**
     * Runs the query for the ethnicity stratifier.
     *
     * @param site
     * @param measureReportGenerator
     * @param beaconFilters
     */
    private void runIndividualsEthnicityQueryAtSite(BeaconSite site,
                                                 MeasureReportGenerator measureReportGenerator,
                                                 List<BeaconFilter> beaconFilters) {
        BeaconEntryType beaconEntryType = site.individuals;

        Map<String, Integer> ethnicityCounts = new HashMap<String, Integer>();
        for (String ethnicity : Utils.getEthnicityNameNcit().keySet()) {
            Integer count = runFilterQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters, "id", Utils.getEthnicityNameNcit().get(ethnicity));
            log.info(ethnicity + ": " + count);
            ethnicityCounts.put(ethnicity, count);
        }
        measureReportGenerator.setPatientEthnicityCounts(ethnicityCounts);
    }

    /**
     * Run a query on the biosamples endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportGenerator will be used to store the results of the query.
     *
     * @param site
     * @param measureReportGenerator
     * @param beaconFilters
     */
    private void runBiosamplesQueryAtSite(BeaconSite site,
                                           MeasureReportGenerator measureReportGenerator,
                                           List<BeaconFilter> beaconFilters) {
        BeaconEntryType beaconEntryType = site.biosamples;
        Integer count = runBeaconEntryTypeQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters);
        measureReportGenerator.setGroupPopulationCount("specimen", count);
        runBiosamplesTypeQueryAtSite(site, measureReportGenerator, beaconFilters);
    }

    /**
     * Runs the query for the sample type stratifier.
     *
     * @param site
     * @param measureReportGenerator
     * @param beaconFilters
     */
    private void runBiosamplesTypeQueryAtSite(BeaconSite site,
                                                 MeasureReportGenerator measureReportGenerator,
                                                 List<BeaconFilter> beaconFilters) {
        BeaconEntryType beaconEntryType = site.biosamples;

        Integer bloodCount = runFilterQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters, "id", "UBERON:0000178");
        Integer bloodSerumCount = runFilterQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters, "id", "UBERON:0001977");
        Integer bloodPlasmaCount = runFilterQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters, "id", "UBERON:0001969");
        Integer lymphCount = runFilterQueryAtSite(beaconEntryType, site.beaconQueryService, beaconFilters, "id", "UBERON:0002391");

        measureReportGenerator.setSpecimenTypeCounts((bloodCount>=0)?bloodCount:0,
                (bloodSerumCount>=0)?bloodSerumCount:0,
                (bloodPlasmaCount>=0)?bloodPlasmaCount:0,
                (lymphCount>=0)?lymphCount:0);
    }

    /**
     * Takes the supplied filter set, and adds an extra filter based on filterName and filterValue.
     * Runs a query constrained by this expanded filter set.
     *
     * The original filter set (beaconFilters) is not modified, i.e. this method has no sideeffects.
     *
     * @param beaconEntryType
     * @param beaconQueryService
     * @param beaconFilters
     * @param filterName
     * @param filterValue
     * @return
     */
    private Integer runFilterQueryAtSite(BeaconEntryType beaconEntryType,
                                         BeaconQueryService beaconQueryService,
                                         List<BeaconFilter> beaconFilters,
                                         String filterName,
                                         String filterValue) {
        List<BeaconFilter> localFilters = new ArrayList<BeaconFilter>(beaconFilters); // Clone beaconFilters
        localFilters.add(new BeaconFilter(filterName, filterValue));
        Integer count = runBeaconEntryTypeQueryAtSite(beaconEntryType, beaconQueryService, localFilters);
        
        return count;
    }

    /**
     * Runs a Beacon query at the site defined by beaconQueryService, using the endpoint defined
     * by beaconEntryType.
     *
     * The supplied filters will be used to constrain the query.
     *
     * Returns a count of the number of matching hits.
     *
     * @param beaconEntryType
     * @param beaconQueryService
     * @param beaconFilters
     * @return
     */
    private Integer runBeaconEntryTypeQueryAtSite(BeaconEntryType beaconEntryType,
                                           BeaconQueryService beaconQueryService,
                                           List<BeaconFilter> beaconFilters) {
        Integer count = -1;
        try {
            BeaconResponse response = beaconQueryService.queryEntry(beaconEntryType, beaconFilters);
            count = extractCountFromBeaconResponse(response);
        } catch (Exception e) {
            log.error("runQuery: problem with " + beaconEntryType.getEntryType() + ", trace: " + traceFromException(e));
        }
        return count;
    }

    /**
     * Get a printable stack trace from an Exception object.
     * @param e
     * @return
     */
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
