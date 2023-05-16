package de.samply.lens_beacon_service.beacon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

/**
 * Send queries to the Beacon API. Both GET and POST requests are possible.
 *
 * The main things here are the URI and the filter.
 *
 * The URI specifies the endpoint we are talking to, e,g, "individuals/".
 *
 * The filter is the thing that one would generally understand as a query. It
 * generally comprises of a resource name (e.g. Omim), an ID, and possibly
 * a matching value. It is packed in JSON.
 *
 * Only the POST endpoints work with filters sent in the request body.
 */

@Slf4j
public class BeaconQueryService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private BeaconQuery query; // BeaconQuery without filters.

    private WebClient webClient; // Talks directly to site.
    private String siteUrl; // URL of Beacon 2 site.

    /**
     * Set up Beacon querying service.
     *
     * @param siteUrl URL of Beacon 2 site.
     * @param query Query object that will be converted to JSON and sent to the site. Filters will be
     *              added to a clone of this object as necessary by the postQuery method.
     */
    public BeaconQueryService(String siteUrl, BeaconQuery query) {
        this.siteUrl = siteUrl;
        this.query = query;
        this.webClient = WebClient.builder().baseUrl(siteUrl).build();
    }

    /**
     * Run a query against a Beacon endpoint. If the entry type specifies POST, then the supplied
     * filters will also be applied. Otherwise, a GET request will be assumed. In this latter case,
     * no filtering will be applied, because Beacon GET endpoints don't accept filters in their
     * bodies.
     *
     * @param entryType
     * @param filters
     * @return
     */
    public BeaconResponse query(BeaconEntryType entryType, List<BeaconFilter> filters) {
        if (entryType == null)
            return null;
        if (entryType.method.equals("POST"))
            return postQuery(entryType.uri, filters);
        else
            return getQuery(entryType.uri);
    }

    /**
     * Get all objects of a given type known to Beacon.
     *
     * Use a GET request.
     *
     * @param uri The URI of the objects be queried, e.g. "individuals" or "biosamples".
     * @return JSON-format list of objects of a given type.
     */
    private BeaconResponse getQuery(String uri) {
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
    private BeaconResponse postQuery(String uri, List<BeaconFilter> beaconFilters) {
        String jsonBeaconRequest = (new BeaconRequest(query.clone(beaconFilters))).toString();
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