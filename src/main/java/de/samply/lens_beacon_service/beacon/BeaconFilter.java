package de.samply.lens_beacon_service.beacon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * Specify a single Beacon filter. A filter may have multiple terms, which can ba
 * added using the addFilterTerm() method.
 */

@Slf4j
public class BeaconFilter extends HashMap<String, String> {
    /**
     * Constructor that allows a simple filter with only one term to be specified.
     *
     * @param name Name of filter term.
     * @param value Value of filter term.
     */
    public BeaconFilter(String name, String value) {
        addFilterTerm(name, value);
    }

    /**
     * Add a single  term to the filter.
     * @param name Name of filter term.
     * @param value Value of filter term.
     */
    public void addFilterTerm(String name, String value) {
        this.put(name, value);
    }

    /**
     * Convert the filter object to a JSON string.
     *
     * @return JSON string.
     */
    public String toJson() {
        String jsonData = "{}";
        try {
            jsonData = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while processing JSON, check JSON syntax");
            e.printStackTrace();
        } catch (Exception e) {
            log.error("An unknown error occurred while converting into JSON");
            e.printStackTrace();
        }
        return jsonData;
    }
}
