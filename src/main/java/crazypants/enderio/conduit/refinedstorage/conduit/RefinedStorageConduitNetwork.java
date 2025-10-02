package crazypants.enderio.conduit.refinedstorage.conduit;

import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;

public class RefinedStorageConduitNetwork extends
                                          AbstractConduitNetwork<RefinedStorageConduit, RefinedStorageConduit> {

    protected RefinedStorageConduitNetwork() {
        super(RefinedStorageConduit.class, RefinedStorageConduit.class);
    }
}
