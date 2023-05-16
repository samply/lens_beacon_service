package de.samply.lens_beacon_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.beacon.*;
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
        Integer geneticsCount = runBeaconEntryTypeQueryAtSite(site.genomicVariations, site.beaconQueryService, site.genomicVariations.baseFilters);
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
        Integer count = runBeaconEntryTypeQueryAtSite(site.individuals, site.beaconQueryService, site.individuals.baseFilters);
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
        Integer femaleCount = runFilterQueryAtSite(site.individuals, site.beaconQueryService, "id", "NCIT:C16576");
        Integer maleCount = runFilterQueryAtSite(site.individuals, site.beaconQueryService, "id", "NCIT:C20197");

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
            Integer count = runFilterQueryAtSite(site.individuals, site.beaconQueryService, "id", Utils.getEthnicityNameNcit().get(ethnicity));
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
        Integer count = runBeaconEntryTypeQueryAtSite(site.biosamples, site.beaconQueryService, site.biosamples.baseFilters);
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
        Integer bloodCount = runFilterQueryAtSite(site.biosamples, site.beaconQueryService, "id", "UBERON:0000178");
        Integer bloodSerumCount = runFilterQueryAtSite(site.biosamples, site.beaconQueryService, "id", "UBERON:0001977");
        Integer bloodPlasmaCount = runFilterQueryAtSite(site.biosamples, site.beaconQueryService, "id", "UBERON:0001969");
        Integer lymphCount = runFilterQueryAtSite(site.biosamples, site.beaconQueryService, "id", "UBERON:0002391");

        measureReportAdmin.specimenGroupAdmin.setTypeCounts((bloodCount>=0)?bloodCount:0,
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
     * @param filterName
     * @param filterValue
     * @return
     */
    private Integer runFilterQueryAtSite(BeaconEntryType beaconEntryType,
                                         BeaconQueryService beaconQueryService,
                                         String filterName,
                                         String filterValue) {
        List<BeaconFilter> localFilters = new ArrayList<BeaconFilter>(beaconEntryType.baseFilters); // Clone filters
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
     * Returns a count of the number of matching hits. If no count can be found, return -1.
     * Possible reasons for this are that the site does not present the endpoint for the
     * entry type or an error has occurred.
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
            BeaconResponse response = beaconQueryService.query(beaconEntryType, beaconFilters);
            if (response != null)
                count = response.getCount();
        } catch (Exception e) {
            log.error("runQuery: problem with " + beaconEntryType.getEntryType() + ", trace: " + Utils.traceFromException(e));
        }
        return count;
    }
}
