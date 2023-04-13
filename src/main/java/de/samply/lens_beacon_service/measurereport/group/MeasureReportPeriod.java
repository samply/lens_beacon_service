package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.Period;

import java.util.Date;

public class MeasureReportPeriod extends Period {
    public MeasureReportPeriod() {
        Date startDate = new Date();
        startDate.setYear(2000);
        this.setStart(startDate);
        Date endDate = new Date();
        endDate.setYear(2030);
        this.setEnd(endDate);
    }
}
