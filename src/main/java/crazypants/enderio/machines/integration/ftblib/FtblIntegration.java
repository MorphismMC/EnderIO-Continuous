package crazypants.enderio.machines.integration.ftblib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Loader;

import com.enderio.core.common.util.UserIdent;

public class FtblIntegration {

    private static Method sameTeamMethod = null;
    private static boolean checked = false;

    public static boolean isInSameTeam(@Nonnull UserIdent identA, @Nonnull UserIdent identB) {
        if (Loader.isModLoaded("ftblib")) {
            return isInSameTeamUnsafe(identA, identB);
        }
        return false;
    }

    private static boolean isInSameTeamUnsafe(@Nonnull UserIdent identA, @Nonnull UserIdent identB) {
        if (!checked) {
            try {
                Class<?> clazz = Class.forName("com.feed_the_beast.ftblib.lib.data.FTBLibAPI");
                sameTeamMethod = clazz.getDeclaredMethod("arePlayersInSameTeam", UUID.class, UUID.class);
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {} finally {
                checked = true;
            }
        }
        if (sameTeamMethod != null) {
            try {
                return (boolean) sameTeamMethod.invoke(null, identA.getUUID(), identB.getUUID());
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
        }
        return false;
    }
}
