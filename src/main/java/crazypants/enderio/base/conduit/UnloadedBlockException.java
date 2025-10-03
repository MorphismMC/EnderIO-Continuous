package crazypants.enderio.base.conduit;

import lombok.Getter;

/**
 * Used to marked the conduit network which will be destroyed but not be loaded.
 */
@Getter
public class UnloadedBlockException extends Exception {

    @SuppressWarnings("MissingSerialAnnotation")
    private static final long serialVersionUID = 2130974035860715939L;

    private final ConduitNetwork<?, ?> networkToDestroy;

    public UnloadedBlockException(ConduitNetwork<?, ?> networkToDestroy) {
        this.networkToDestroy = networkToDestroy;
    }

}
