package de.samply.lens_beacon_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.convert.ConvertBiosamples;
import de.samply.lens_beacon_service.convert.ConvertIndividuals;
import de.samply.lens_beacon_service.measurereport.MeasureReportGenerator;
import de.samply.lens_beacon_service.lens.LensQuery;
import de.samply.lens_beacon_service.lens.LensQueryLeafPicker;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/* Run a query. */

@Slf4j
public class QueryService {
    private BeaconQueryService beaconQueryService = new BeaconQueryService();

    public String runQuery() {
        return runQuery(new LensQuery());
    }

    public String runQuery(LensQuery lensQuery) {
        List<LensQuery> lensQueryLeafNodeList = new LensQueryLeafPicker().crawl(lensQuery);
        List<BeaconFilter> beaconFiltersIndividuals = new ConvertIndividuals().convert(lensQueryLeafNodeList);
        List<BeaconFilter> beaconFiltersBiosamples = new ConvertBiosamples().convert(lensQueryLeafNodeList);

        try {
            log.info("\npostPatientCount: lensQuery" + lensQuery);
            log.info("\njsonLensQueryLeafNodeList: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(lensQueryLeafNodeList));
            log.info("\nbeaconFiltersIndividuals: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(beaconFiltersIndividuals));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Integer countIndividuals = (Integer) beaconQueryService.postIndividuals(beaconFiltersIndividuals).responseSummary.get("numTotalResults");
        log.info("countIndividuals: " + countIndividuals);
        Integer countBiosamples = (Integer) beaconQueryService.postBiosamples(beaconFiltersBiosamples).responseSummary.get("numTotalResults");
        log.info("countBiosamples: " + countBiosamples);
        MeasureReportGenerator measureReportGenerator = new MeasureReportGenerator();
        measureReportGenerator.setPatientCount(countIndividuals);
        measureReportGenerator.setSpecimenCount(countBiosamples);
        String jsonResult = measureReportGenerator.toString();
        log.info("\njsonResult: " + jsonResult);
        return jsonResult;
    }
}
