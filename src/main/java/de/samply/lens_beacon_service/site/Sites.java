package de.samply.lens_beacon_service.site;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a list of known Beacon sites.
 *
 * This is basically a hard-coded list of sites that will be contacted when a
 * query is received. It would be better if this information resided in a database.
 * Some kind of registration API would also be a good idea.
 */

public class Sites {
    // Make this class a singleton
    private Sites() {}

    public static List<Site> getSites() {
        List<Site> sites = new ArrayList<Site>();

        // Site definitions
        sites.add(new HdCinecaSite());
//        sites.add(new EgaCinecaSite());
//        sites.add(new MolgenisMutationsSite());
//        sites.add(new RdcPlaygroundSite());
//        sites.add(new ProgenetixSite());

        return sites;
    }
}
