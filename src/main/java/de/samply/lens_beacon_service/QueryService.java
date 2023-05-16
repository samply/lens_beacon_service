package de.samply.lens_beacon_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.beacon.model.BeaconSite;
import de.samply.lens_beacon_service.beacon.model.BeaconSites;
import de.samply.lens_beacon_service.convert.AstLeafPicker;
import de.samply.lens_beacon_service.convert.biosamples.AstNodeListConverterBiosamples;
import de.samply.lens_beacon_service.convert.genetics.AstNodeListConverterGenetics;
import de.samply.lens_beacon_service.convert.individuals.AstNodeListConverterIndividuals;
import de.samply.lens_beacon_service.lens.AstNode;
import de.samply.lens_beacon_service.lens.SiteResult;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;
import lombok.extern.slf4j.Slf4j;

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
     * @param astNode AST query.
     * @return Serialized results.
     */
    public String runQuery(AstNode astNode) {
        try {
            log.info("\nastNode: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(astNode));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Flatten AST tree, because converters cannot handle tree structures.
        List<AstNode> astNodeLeafNodeList = new AstLeafPicker().crawl(astNode);
        // Run converters once for each Beacon end point.
        List<BeaconFilter> beaconFiltersIndividuals = new AstNodeListConverterIndividuals().convert(astNodeLeafNodeList);
        List<BeaconFilter> beaconFiltersBiosamples = new AstNodeListConverterBiosamples().convert(astNodeLeafNodeList);
        List<BeaconFilter> beaconFiltersGenetics = new AstNodeListConverterGenetics().convert(astNodeLeafNodeList);

        // Add filters to sites.
        // Create an object for holding the result objects for all sites.
        // Insert placeholders for the measure reports.
        List<SiteResult> siteResults = new ArrayList<SiteResult>();
        for (BeaconSite site: BeaconSites.getSites()) {
            site.individuals.baseFilters = beaconFiltersIndividuals;
            site.biosamples.baseFilters = beaconFiltersBiosamples;
            site.genomicVariations.baseFilters = beaconFiltersGenetics;
            String siteName = site.name;
            String siteUrl = site.url;
            SiteResult siteResult = new SiteResult(siteName, siteUrl, "PLACEHOLDER" + siteName);
            siteResults.add(siteResult);
        }

        // Convert results object into a string.
        String jsonResults = "";
        try {
            jsonResults = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(siteResults);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while converting an object into JSON\n" + Utils.traceFromException(e));
        }

        // Run Beacon query at each site, serialize measure reports into JSON strings,
        // replace placeholders in results object with serialized measure reports.
        for (BeaconSite site: BeaconSites.getSites()) {
            String siteName = site.name;
            String jsonMeasure = runQueryAtSite(site);
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
     * @return
     */
    private String runQueryAtSite(BeaconSite site) {
        MeasureReportAdmin measureReportAdmin = new MeasureReportAdmin();

        runIndividualsQueryAtSite(site, measureReportAdmin);
        runBiosamplesQueryAtSite(site, measureReportAdmin);
        Integer geneticsCount = site.beaconQueryService.runBeaconEntryTypeQueryAtSite(site.genomicVariations, site.genomicVariations.baseFilters);
        measureReportAdmin.geneticsGroupAdmin.setCount(geneticsCount);

        String jsonMeasure = measureReportAdmin.toString();
        return jsonMeasure;
    }

    /**
     * Run a query on the individuals endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param site
     * @param measureReportAdmin
     */
    private void runIndividualsQueryAtSite(BeaconSite site,
                                           MeasureReportAdmin measureReportAdmin) {
        Integer count = site.beaconQueryService.runBeaconEntryTypeQueryAtSite(site.individuals, site.individuals.baseFilters);
        measureReportAdmin.patientsGroupAdmin.setCount(count);
        runIndividualsGenderQueryAtSite(site, measureReportAdmin);
        runIndividualsEthnicityQueryAtSite(site, measureReportAdmin);
    }

    /**
     * Runs the query for the gender stratifier.
     *
     * @param site
     * @param measureReportAdmin
     */
    private void runIndividualsGenderQueryAtSite(BeaconSite site,
                                                 MeasureReportAdmin measureReportAdmin) {
        Integer femaleCount = site.beaconQueryService.runFilterQueryAtSite(site.individuals, "id", "NCIT:C16576");
        Integer maleCount = site.beaconQueryService.runFilterQueryAtSite(site.individuals, "id", "NCIT:C20197");

        measureReportAdmin.patientsGroupAdmin.setGenderCounts((femaleCount>=0)?femaleCount:0, (maleCount>=0)?maleCount:0);
    }

    /**
     * Runs the query for the ethnicity stratifier.
     *
     * @param site
     * @param measureReportAdmin
     */
    private void runIndividualsEthnicityQueryAtSite(BeaconSite site,
                                                 MeasureReportAdmin measureReportAdmin) {
        Map<String, Integer> ethnicityCounts = new HashMap<String, Integer>();
        for (String ethnicity : Utils.getEthnicityNameNcit().keySet()) {
            Integer count = site.beaconQueryService.runFilterQueryAtSite(site.individuals,"id", Utils.getEthnicityNameNcit().get(ethnicity));
            log.info(ethnicity + ": " + count);
            ethnicityCounts.put(ethnicity, count);
        }
        measureReportAdmin.patientsGroupAdmin.setEthnicityCounts(ethnicityCounts);
    }

    /**
     * Run a query on the biosamples endpoint at a given Beacon site, using the supplied filters.
     *
     * The measureReportAdmin will be used to store the results of the query.
     *
     * @param site
     * @param measureReportAdmin
     */
    private void runBiosamplesQueryAtSite(BeaconSite site,
                                           MeasureReportAdmin measureReportAdmin) {
        Integer count = site.beaconQueryService.runBeaconEntryTypeQueryAtSite(site.biosamples, site.biosamples.baseFilters);
        measureReportAdmin.specimenGroupAdmin.setCount(count);
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

        measureReportAdmin.specimenGroupAdmin.setTypeCounts((bloodCount>=0)?bloodCount:0,
                (bloodSerumCount>=0)?bloodSerumCount:0,
                (bloodPlasmaCount>=0)?bloodPlasmaCount:0,
                (lymphCount>=0)?lymphCount:0);
    }
}
