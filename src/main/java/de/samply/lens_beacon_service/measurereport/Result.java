package de.samply.lens_beacon_service.measurereport;

public class Result {
    public Result(String siteName, String measureReport) {
        this.siteName = siteName;
        this.measureReport = measureReport;
    }

    public String siteName;
    public String measureReport;
}
