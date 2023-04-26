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

//        // Create a Wiretap connector for logging what happens in this API
//        HttpClient httpClient = HttpClient
//                .create()
//                .wiretap(this.getClass().getCanonicalName(),
//                        LogLevel.DEBUG,
//                        AdvancedByteBufFormat.TEXTUAL);
//        ClientHttpConnector conn = new ReactorClientHttpConnector(httpClient);
//
//        this.webClient =  WebClient.builder()
//                .clientConnector(conn)
//                .baseUrl(siteUrl)
//                .build();
    }

//    /**
//     * Get all individuals known to Beacon.
//     *
//     * Use a GET request.
//     *
//     * @return JSON-format list of individuals.
//     */
//    public BeaconResponse getIndividuals() {
//        return getObjectsOfType("individuals");
//    }
//
    /**
     * Get a filtered count of individuals know to Beacon.
     *
     * Use a POST request.
     *
     * @param beaconFilters Filters that will be applied to the query.
     * @return JSON-format list of individuals.
     */
    public BeaconResponse postIndividuals(List<BeaconFilter> beaconFilters) {
        return postObjectsOfType("individuals", beaconFilters);
    }

//    /**
//     * Get all biosamples known to Beacon.
//     *
//     * Use a GET request.
//     *
//     * @return JSON-format list of biosamples.
//     */
//    public BeaconResponse getBiosamples() {
//        return getObjectsOfType("biosamples");
//    }
//
    /**
     * Get a filtered count of biosamples know to Beacon.
     *
     * Use a POST request.
     *
     * @param beaconFilters Filters that will be applied to the query.
     * @return JSON-format list of biosamples.
     */
    public BeaconResponse postBiosamples(List<BeaconFilter> beaconFilters) {
        log.info("postBiosamples: beaconFilters=" + beaconFilters);
        return postObjectsOfType("biosamples", beaconFilters);
    }

//    /**
//     * Get all genomic variations known to Beacon.
//     *
//     * Use a GET request.
//     *
//     * @return JSON-format list of genomic variations.
//     */
//    public BeaconResponse getGenomicVariations() {
//        return getObjectsOfType("genomicVariations");
//    }
//
    /**
     * Get a filtered count of genomic variations know to Beacon.
     *
     * Use a POST request.
     *
     * @param beaconFilters Filters that will be applied to the query.
     * @return JSON-format list of genomic variations.
     */
    public BeaconResponse postGenomicVariations(List<BeaconFilter> beaconFilters) {
        log.info("postGenomicVariations: beaconFilters=" + beaconFilters);
        return postObjectsOfType("genomicVariations", beaconFilters);
    }

    /**
     * Get all objects of a given type known to Beacon.
     *
     * Use a GET request.
     *
     * @param type The type of the objects be queries, e.g. "individuals" or "biosamples".
     * @return JSON-format list of objects of a given type.
     */
    private BeaconResponse getObjectsOfType(String type) {
        String uri = "/" + type;

        log.info("\nFull URL: " + siteUrl + uri);

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
     * @param type The type of the objects be queries, e.g. "individuals" or "biosamples".
     * @param beaconFilters Filters that will be applied to the query.
     * @return JSON-format list of objects of a given type.
     */
    private BeaconResponse postObjectsOfType(String type, List<BeaconFilter> beaconFilters) {
        BeaconQuery beaconQuery = new BeaconQuery(beaconFilters);
        BeaconRequest beaconRequest = new BeaconRequest(new BeaconQuery(beaconFilters));
        String jsonBeaconRequest = "{}";
        try {
            jsonBeaconRequest = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(beaconRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String uri = "/" + type;

        log.info("\ntype: " + type);
        log.info("\njsonBeaconRequest: " + jsonBeaconRequest);
        log.info("\nFull URL: " + siteUrl + uri);

        return webClient
                .post()
                .uri(uri)
//                .contentType(MediaType.APPLICATION_JSON) // added
//                .contentType(MediaType.TEXT_PLAIN) // added
//                .accept(MediaType.APPLICATION_JSON) // added
//                .accept(MediaType.ALL) // added
                .bodyValue(jsonBeaconRequest)
                .retrieve()
                .bodyToMono(BeaconResponse.class)
                .block(REQUEST_TIMEOUT);
    }
}