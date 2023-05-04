package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.MeasureReport;
import de.samply.lens_beacon_service.measurereport.Utils;

import java.util.ArrayList;
import java.util.List;

public class PatientsGroupGenerator extends GroupGenerator {
    public MeasureReport.MeasureReportGroupComponent generate() {
        group.setCode(Utils.createTextCodeableConcept("patients"));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createStratifier("Gender"));
        stratifiers.add(createVitalStatusStratifier());
        stratifiers.add(createStratifier("Age"));
        stratifiers.add(createStratifier("Ethnicity"));
//        stratifiers.add(createSitesStratifier());
        group.setStratifier(stratifiers);

        return group;
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createVitalStatusStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("75186-7");
        stratifierComponent.addStratum(Utils.createStratum("unbekannt", 0));

        return  stratifierComponent;
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createSitesStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("sites");
        MeasureReport.StratifierGroupComponent stratum = new MeasureReport.StratifierGroupComponent();
        stratum.setValue(Utils.createTextCodeableConcept("AnInventedSiteName"));
        stratifierComponent.addStratum(stratum);

        return  stratifierComponent;
    }
}
