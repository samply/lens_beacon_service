package de.samply.lens_beacon_service.measurereport.group;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.MeasureReport;
import de.samply.lens_beacon_service.measurereport.MeasureReportUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generate the patients group for the measure report.
 */

public class PatientsGroupAdmin extends GroupAdmin {
    /**
     * Generate group with all counts set to default initial values.
     *
     * @return The group object.
     */
    public MeasureReport.MeasureReportGroupComponent generate() {
        group.setCode(MeasureReportUtils.createTextCodeableConcept("patients"));

        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifiers = new ArrayList<MeasureReport.MeasureReportGroupStratifierComponent>();
        stratifiers.add(createStratifier("Gender"));
        stratifiers.add(createVitalStatusStratifier());
        stratifiers.add(createStratifier("Age"));
        stratifiers.add(createStratifier("Ethnicity"));
        group.setStratifier(stratifiers);

        return group;
    }

    /**
     * Add male and female counts to gender stratifier.
     *
     * @param femaleCount
     * @param maleCount
     */
    public void setGenderCounts(int femaleCount, int maleCount) {
        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifierComponents = group.getStratifier();
        for (MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent: stratifierComponents) {
            CodeableConcept code = stratifierComponent.getCode().get(0);
            String text = code.getText();
            if (text.equals("Gender")) {
                stratifierComponent.addStratum(MeasureReportUtils.createStratum("female", femaleCount));
                stratifierComponent.addStratum(MeasureReportUtils.createStratum("male", maleCount));
                break;
            }
        }
    }

    /**
     * Add counts for the various ethnicities to the ethnicity stratifier.
     *
     * @param ethnicityCounts
     */
    public void setEthnicityCounts(Map<String, Integer> ethnicityCounts) {
        List<MeasureReport.MeasureReportGroupStratifierComponent> stratifierComponents = group.getStratifier();
        for (MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent: stratifierComponents) {
            CodeableConcept code = stratifierComponent.getCode().get(0);
            String text = code.getText();
            if (text.equals("Ethnicity")) {
                // Order by key length. This looks better in the Lens histogram.
                for (String ethnicity: ethnicityCounts.keySet().stream()
                        .sorted((str1, str2) -> str1.length() - str2.length())
                        .collect(Collectors.toList()))
                    stratifierComponent.addStratum(MeasureReportUtils.createStratum(ethnicity, ethnicityCounts.get(ethnicity)));
                break;
            }
        }
    }

    private MeasureReport.MeasureReportGroupStratifierComponent createVitalStatusStratifier() {
        MeasureReport.MeasureReportGroupStratifierComponent stratifierComponent = createStratifier("75186-7");
        stratifierComponent.addStratum(MeasureReportUtils.createStratum("unbekannt", 0));

        return  stratifierComponent;
    }
}
