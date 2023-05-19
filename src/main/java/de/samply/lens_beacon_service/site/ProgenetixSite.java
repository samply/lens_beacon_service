package de.samply.lens_beacon_service.site;

import de.samply.lens_beacon_service.beacon.model.BeaconQueryGranularityUc;
import de.samply.lens_beacon_service.entrytype.BiosamplesEntryType;
import de.samply.lens_beacon_service.entrytype.GenomicVariationsEntryType;
import de.samply.lens_beacon_service.entrytype.IndividualsEntryType;

public class ProgenetixSite extends Site {
    public ProgenetixSite() {
        name = "Progenetix";
        url = "https://progenetix.org/beacon";
        query = new BeaconQueryGranularityUc();
        entryTypes.add(new IndividualsEntryType());
        entryTypes.add(new BiosamplesEntryType());
        entryTypes.add(new GenomicVariationsEntryType());
        init();
    }
}
