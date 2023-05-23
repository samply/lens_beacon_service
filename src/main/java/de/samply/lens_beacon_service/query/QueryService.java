package de.samply.lens_beacon_service.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.site.Site;
import de.samply.lens_beacon_service.entrytype.EntryType;
import de.samply.lens_beacon_service.site.Sites;
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

        // Create a fresh list of known Beacon sites.
        List<Site> sites = Sites.getSites();

        // Add filters to sites.
        // Create an object for holding the result objects for all sites.
        // Insert placeholders for the measure reports.
        List<SiteResult> siteResults = new ArrayList<SiteResult>();
        for (Site site: sites) {
            for (EntryType entryType: site.entryTypes)
                entryType.convert(astNode);
            SiteResult siteResult = new SiteResult(site.name, site.url, "PLACEHOLDER" + site.name);
            siteResults.add(siteResult);
        }

        // Convert results object into a string.
        String jsonResults = siteResults.toString();

        // Run Beacon query at each site, serialize measure reports into JSON strings,
        // replace placeholders in results object with serialized measure reports.
        for (Site site: sites) {
            MeasureReportAdmin measureReportAdmin = new MeasureReportAdmin();

            for (EntryType entryType: site.entryTypes)
                measureReportAdmin.measureReport.addGroup(entryType.query.runQueryAtSite(site.beaconQueryService, entryType));

            String jsonMeasure = measureReportAdmin.toString();
            jsonResults = jsonResults.replaceAll("\"PLACEHOLDER" + site.name + "\"", "\n" + jsonMeasure.replaceAll("^", "        "));
        }

        return jsonResults;
    }
}
