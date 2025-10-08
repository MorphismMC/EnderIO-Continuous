package crazypants.enderio.conduit.me.conduit;

import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;

public class MEConduitNetwork extends AbstractConduitNetwork<MEConduit, MEConduit> {

    public MEConduitNetwork() {
        super(MEConduit.class, MEConduit.class);
    }
}
