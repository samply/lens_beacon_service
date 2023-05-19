package de.samply.lens_beacon_service.site;

import de.samply.lens_beacon_service.beacon.model.BeaconQueryGranularityLc;
import de.samply.lens_beacon_service.entrytype.BiosamplesEntryType;
import de.samply.lens_beacon_service.entrytype.GenomicVariationsEntryType;
import de.samply.lens_beacon_service.entrytype.IndividualsEntryType;

public class HdCinecaSite extends Site {
    public HdCinecaSite() {
        name = "HD Cineca";
        url = "http://beacon:5050/api";
        query = new BeaconQueryGranularityLc();
        entryTypes.add(new IndividualsEntryType("/individuals/", "POST")); // Error 380 w/o trailing slash
        entryTypes.add(new BiosamplesEntryType("/biosamples/", "POST")); // Error 380 w/o trailing slash
        entryTypes.add(new GenomicVariationsEntryType("/g_variants/", "POST")); // Error 380 w/o trailing slash
        init();
    }
}
