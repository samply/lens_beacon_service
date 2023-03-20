package de.samply.lens_beacon_service.beacon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

/**
 * Send queries to the Beacon API.
 */

@Slf4j
public class BeaconQueryService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final WebClient webClient;

    /**
     * The constructor will set up the web client for the Beacon API, with a hard-coded
     * URL.
     */
    public BeaconQueryService() {
        this.webClient = WebClient.builder().baseUrl("http://beacon:5050").build();
    }

    /**
     * Get all individuals known to Beacon.
     *
     * Use a GET request.
     *
     * @return JSON-format list of individuals.
     */
    public BeaconResponse getIndividuals() {
        return webClient
                .get()
                .uri("/api/individuals/")
                .retrieve()
                .bodyToMono(BeaconResponse.class)
                .block(REQUEST_TIMEOUT);
    }

    /**
     * Get a filtered count of individuals know to Beacon.
     *
     * Use a POST request.
     *
     * @param beaconFilters Filters that will be applied to the query.
     * @return JSON-format list of individuals.
     */
    public BeaconResponse postIndividuals(List<BeaconFilter> beaconFilters) {
        BeaconQuery beaconQuery = new BeaconQuery(beaconFilters);
        BeaconManifest beaconManifest = new BeaconManifest(new BeaconQuery(beaconFilters));
        String jsonBeaconManifest = "{}";
        try {
            jsonBeaconManifest = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(beaconManifest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        // Filter for "male"
//        String jsonBeaconQuery = "{\n" +
//                "    \"query\": {\n" +
//                "        \"filters\": [\n" +
//                "            {\n" +
//                "                \"id\": \"NCIT:C16576\"\n" +
//                "            }\n" +
//                "        ],\n" +
//                "        \"includeResultsetResponses\": \"HIT\",\n" +
//                "        \"testMode\": false,\n" +
//                "        \"requestedGranularity\": \"count\"\n" +
//                "    }" +
//                "}";
        log.info("\njsonBeaconManifest: " + jsonBeaconManifest);
        return webClient
                .post()
                .uri("/api/individuals/")
                .bodyValue(jsonBeaconManifest)
                .retrieve()
                .bodyToMono(BeaconResponse.class)
                .block(REQUEST_TIMEOUT);
    }
}