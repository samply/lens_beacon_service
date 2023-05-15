package de.samply.lens_beacon_service.lens;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Model for one of the nodes in the AST (query tree) from Lens.
 *
 * Hierarchical tree structures can be realized by filling the "children" list.
 */

@Slf4j
public class AstNode implements Cloneable {
    public String operand;
    public List<AstNode> children;
    public String key;
    public String en;
    public String de;
    public String type;
    public String system;
    public Object value;

    /**
     * Turn the object into pretty-printed JSON.
     * @return
     */
    @Override
    public String toString() {
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
