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

    private WebClient webClient;
    private String siteUrl;

    /**
     * The constructor will set up the web client for the Beacon API, with a hard-coded
     * URL.
     */
    public BeaconQueryService(String siteUrl) {
        this.siteUrl = siteUrl;
        this.webClient = WebClient.builder().baseUrl(siteUrl).build();
    }

    public BeaconResponse queryEntry(BeaconEntryType entryType, List<BeaconFilter> filters) {
        if (entryType == null)
            return null;
        if (entryType.method.equals("POST"))
            return postObjectsOfType(entryType.uri, filters);
        else
            return getObjectsOfType(entryType.uri);
    }

    /**
     * Get all objects of a given type known to Beacon.
     *
     * Use a GET request.
     *
     * @param uri The URI of the objects be queried, e.g. "individuals" or "biosamples".
     * @return JSON-format list of objects of a given type.
     */
    private BeaconResponse getObjectsOfType(String uri) {
        log.info("\nGET Full URL: " + siteUrl + uri);

        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(BeaconResponse.class)
                .block(REQUEST_TIMEOUT);
    }

    /**
     * Get a filtered count of objects of a given type know to Beacon.
     *
     * Use a POST request.
     *
     * @param uri The URI of the objects be queried, e.g. "individuals" or "biosamples".
     * @param beaconFilters Filters that will be applied to the query.
     * @return JSON-format list of objects of a given type.
     */
    private BeaconResponse postObjectsOfType(String uri, List<BeaconFilter> beaconFilters) {
        BeaconRequest beaconRequest = new BeaconRequest(new BeaconQuery(beaconFilters));
        String jsonBeaconRequest = "{}";
        try {
            jsonBeaconRequest = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(beaconRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("\nuri: " + uri);
        log.info("\njsonBeaconRequest: " + jsonBeaconRequest);
        log.info("\nPOST Full URL: " + siteUrl + uri);

        return webClient
                .post()
                .uri(uri)
                .bodyValue(jsonBeaconRequest)
                .retrieve()
                .bodyToMono(BeaconResponse.class)
                .block(REQUEST_TIMEOUT);
    }
}