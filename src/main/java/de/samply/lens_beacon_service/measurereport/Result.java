package de.samply.lens_beacon_service.measurereport;

public class Result {
    public Result(String siteName, String siteUrl, String measureReport) {
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.measureReport = measureReport;
    }

    public String siteName;
    public String siteUrl;
    public String measureReport;
}
