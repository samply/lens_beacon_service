package de.samply.lens_beacon_service.site;

import de.samply.lens_beacon_service.beacon.model.BeaconQueryGranularityUc;
import de.samply.lens_beacon_service.entrytype.BiosamplesEntryType;
import de.samply.lens_beacon_service.entrytype.GenomicVariationsEntryType;
import de.samply.lens_beacon_service.entrytype.IndividualsEntryType;

public class MolgenisMutationsSite extends Site {
    public MolgenisMutationsSite() {
        name = "Molgenis mutations";
        url = "https://mutatiedatabases.molgeniscloud.org/api/beacon";
        query = new BeaconQueryGranularityUc();
        entryTypes.add(new IndividualsEntryType());
        entryTypes.add(new BiosamplesEntryType("/biosamples", "GET")); // Uses GET, deviates from Beacon 2 standard
        entryTypes.add(new GenomicVariationsEntryType("/g_variants", "GET")); // Uses GET, deviates from Beacon 2 standard
        init();
    }
}
