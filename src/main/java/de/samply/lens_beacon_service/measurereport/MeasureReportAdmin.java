package de.samply.lens_beacon_service.measurereport;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.samply.lens_beacon_service.measurereport.group.GeneticsGroupAdmin;
import de.samply.lens_beacon_service.measurereport.group.PatientsGroupAdmin;
import de.samply.lens_beacon_service.measurereport.group.SpecimenGroupAdmin;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.MeasureReport;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Generates a single FHIR measure report.
 *
 * Which groups get added to this report is hard-coded into the constructor.
 *
 */
@Slf4j
public class MeasureReportAdmin {
    private MeasureReport measureReport = new MeasureReport();
    public PatientsGroupAdmin patientsGroupAdmin = new PatientsGroupAdmin();
    public SpecimenGroupAdmin specimenGroupAdmin = new SpecimenGroupAdmin();
    public GeneticsGroupAdmin geneticsGroupAdmin = new GeneticsGroupAdmin();

    public MeasureReportAdmin() {
        measureReport.setStatus(MeasureReport.MeasureReportStatus.COMPLETE);
        measureReport.setType(MeasureReport.MeasureReportType.SUMMARY);
        measureReport.setDate(new Date());
        measureReport.setMeasure("urn:uuid:" + UUID.randomUUID());
        measureReport.addExtension(createExtension());
        measureReport.setPeriod(createPeriod());
        measureReport.addGroup(patientsGroupAdmin.generate());
        measureReport.addGroup(specimenGroupAdmin.generate());
        measureReport.addGroup(geneticsGroupAdmin.generate());
    }

    /**
     * AstNodeListConverter the measure report into a JSON-formatted string.
     *
     * @return JSON-formatted string.
     */
    @Override
    public String toString() {
        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        parser.setPrettyPrint(true);
        String stringJsonMeasureReport = parser.encodeResourceToString(measureReport);

        return stringJsonMeasureReport;
    }

    // Taken from Lens, may not be strictly necessary.
    private Extension createExtension() {
        Extension extension = new Extension("https://samply.github.io/blaze/fhir/StructureDefinition/eval-duration");
        Quantity quantity = new Quantity();
        quantity.setCode("s");
        quantity.setSystem("http://unitsofmeasure.org");
        quantity.setUnit("s");
        quantity.setValue(new BigDecimal(0.3398866));
        extension.setValue(quantity);

        return extension;
    }

    // Taken from Lens, may not be strictly necessary.
    private Period createPeriod() {
        Period period = new Period();
        Date startDate = new Date();
        startDate.setYear(2000);
        period.setStart(startDate);
        Date endDate = new Date();
        endDate.setYear(2030);
        period.setEnd(endDate);

        return period;
    }
}
