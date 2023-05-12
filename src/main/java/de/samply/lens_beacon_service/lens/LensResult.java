package de.samply.lens_beacon_service.lens;

/**
 * Encapsulates the result from a single site, to be sent back to Lens.
 */

public class LensResult {
    public LensResult(String siteName, String siteUrl, String measureReport) {
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.measureReport = measureReport;
    }

    public String siteName;
    public String siteUrl;
    public String measureReport;
}
