package de.samply.lens_beacon_service.site;

import de.samply.lens_beacon_service.beacon.model.BeaconQueryGranularityUc;
import de.samply.lens_beacon_service.entrytype.BiosamplesEntryType;
import de.samply.lens_beacon_service.entrytype.IndividualsEntryType;

public class RdcPlaygroundSite extends Site {
    public RdcPlaygroundSite() {
        name = "RDConnect playground";
        url = "https://playground.rd-connect.eu/beacon2/api";
        query = new BeaconQueryGranularityUc();
        entryTypes.add(new IndividualsEntryType());
        entryTypes.add(new BiosamplesEntryType());
        init();
    }
}
