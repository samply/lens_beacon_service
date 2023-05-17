package de.samply.lens_beacon_service.lens;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * Encapsulates the result from a single site, to be sent back to Lens.
 *
 * This result is based around a FHIR measure report, with a few pieces
 * of additional information.
 */

@Slf4j
public class SiteResult {
    public SiteResult(String siteName, String siteUrl, String measureReport) {
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.measureReport = measureReport;
    }

    public String siteName;
    public String siteUrl;
    public String measureReport;

    public String toString() {
        String string = "";
        try {
            string = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while converting an object into JSON\n" + Utils.traceFromException(e));
        }

        return string;
    }
}
