package de.samply.lens_beacon_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.beacon.BeaconFilter;
import de.samply.lens_beacon_service.beacon.BeaconQueryService;
import de.samply.lens_beacon_service.convert.Convert;
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
        List<BeaconFilter> beaconFilters = new Convert().convert(lensQueryLeafNodeList);

        try {
            log.info("\npostPatientCount: lensQuery" + lensQuery);
            log.info("\njsonLensQueryLeafNodeList: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(lensQueryLeafNodeList));
            log.info("\nbeaconFilterList: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(beaconFilters));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Integer individualCount = (Integer) beaconQueryService.postIndividuals(beaconFilters).responseSummary.get("numTotalResults");
        log.info("individualCount: " + individualCount);
        return individualCount.toString();
    }
}
