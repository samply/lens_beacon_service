package de.samply.lens_beacon_service.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.Utils;
import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.beacon.model.BeaconSite;
import de.samply.lens_beacon_service.beacon.model.BeaconSites;
import de.samply.lens_beacon_service.convert.AstLeafPicker;
import de.samply.lens_beacon_service.convert.biosamples.AstNodeListConverterBiosamples;
import de.samply.lens_beacon_service.convert.genomicVariations.AstNodeListConverterGenomicVariations;
import de.samply.lens_beacon_service.convert.individuals.AstNodeListConverterIndividuals;
import de.samply.lens_beacon_service.lens.AstNode;
import de.samply.lens_beacon_service.lens.SiteResult;
import de.samply.lens_beacon_service.measurereport.MeasureReportAdmin;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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
        List<BeaconFilter> beaconFiltersGenomicVariations = new AstNodeListConverterGenomicVariations().convert(astNodeLeafNodeList);

        // Add filters to sites.
        // Create an object for holding the result objects for all sites.
        // Insert placeholders for the measure reports.
        List<SiteResult> siteResults = new ArrayList<SiteResult>();
        for (BeaconSite site: BeaconSites.getSites()) {
            site.individuals.baseFilters = beaconFiltersIndividuals;
            site.biosamples.baseFilters = beaconFiltersBiosamples;
            site.genomicVariations.baseFilters = beaconFiltersGenomicVariations;
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

        QueryIndividuals.runQueryAtSite(site, measureReportAdmin);
        QueryBiosamples.runQueryAtSite(site, measureReportAdmin);
        QueryGenomicVariations.runQueryAtSite(site, measureReportAdmin);

        return measureReportAdmin.toString();
    }
}
