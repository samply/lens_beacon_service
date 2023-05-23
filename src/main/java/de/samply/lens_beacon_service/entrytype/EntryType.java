package de.samply.lens_beacon_service.entrytype;

import de.samply.lens_beacon_service.beacon.model.BeaconEndpoint;
import de.samply.lens_beacon_service.beacon.model.BeaconFilter;
import de.samply.lens_beacon_service.ast2filter.AstNodeListConverter;
import de.samply.lens_beacon_service.lens.AstNode;
import de.samply.lens_beacon_service.measurereport.GroupAdmin;
import de.samply.lens_beacon_service.query.Query;

import java.util.List;

/**
 * Brings together all of the operations and data associated with a single entry type (e.g.
 * biosamples) for a single site (e.g. EGA Cineca or Molgenis mutations).
 */
public class EntryType {
    public EntryType() {
    }

    public EntryType(String uri, String method) {
        beaconEndpoint = new BeaconEndpoint(uri, method);
    }

    public BeaconEndpoint beaconEndpoint; // Information needed to query Beacon API for this entry type.
    public AstNodeListConverter astNodeListConverter; // Convert Lens AST to Beacon Filters.
    public List<BeaconFilter> baseFilters; // Filters for a regular query
    public Query query; // Query Beacon, pack results in measure report.
    public GroupAdmin groupAdmin; // Add counts and stuff to measure report group.

    public void convert(AstNode astNode) {
        baseFilters = astNodeListConverter.convert(astNode);
    }
}
