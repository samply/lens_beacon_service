package de.samply.lens_beacon_service.beacon.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.samply.lens_beacon_service.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * Describes the request that will be sent to Beacon.
 *
 * Right now, the only thing we are modeling is the query.
 *
 * I have coopted toString() as the serializer to turn this object into JSON.
 */
@Slf4j
public class BeaconRequest {
    public BeaconRequest(BeaconQuery query) {
        this.query = query;
    }

    public BeaconQuery query;

    /**
     * Serialize to JSON string.
     *
     * @return
     */
    public String toString() {
        String string = "{}";
        try {
            // AstNodeListConverter the request into JSON, to be sent in the body.
            string = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error(Utils.traceFromException(e));
        }

        return string;
    }
}
