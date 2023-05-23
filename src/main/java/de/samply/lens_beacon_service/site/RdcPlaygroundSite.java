package de.samply.lens_beacon_service.site;

import de.samply.lens_beacon_service.beacon.model.GranularityUcBeaconQuery;
import de.samply.lens_beacon_service.entrytype.biosamples.BiosamplesEntryType;
import de.samply.lens_beacon_service.entrytype.individuals.IndividualsEntryType;

public class RdcPlaygroundSite extends Site {
    public RdcPlaygroundSite() {
        name = "RDConnect playground";
        url = "https://playground.rd-connect.eu/beacon2/api";
        query = new GranularityUcBeaconQuery();
        entryTypes.add(new IndividualsEntryType());
        entryTypes.add(new BiosamplesEntryType());
        init();
    }
}
