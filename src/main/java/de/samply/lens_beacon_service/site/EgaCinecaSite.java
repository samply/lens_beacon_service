package de.samply.lens_beacon_service.site;

import de.samply.lens_beacon_service.beacon.model.BeaconQueryGranularityUc;
import de.samply.lens_beacon_service.entrytype.BiosamplesEntryType;
import de.samply.lens_beacon_service.entrytype.GenomicVariationsEntryType;
import de.samply.lens_beacon_service.entrytype.IndividualsEntryType;

public class EgaCinecaSite extends Site {
    public EgaCinecaSite() {
        name = "EGA Cineca";
        url = "https://ega-archive.org/beacon-apis/cineca";
        query = new BeaconQueryGranularityUc();
        entryTypes.add(new IndividualsEntryType());
        entryTypes.add(new BiosamplesEntryType());
        entryTypes.add(new GenomicVariationsEntryType());
        init();
    }
}
