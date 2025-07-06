package crazypants.enderio.invpanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.EIOTags;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.invpanel.config.Config;
import crazypants.enderio.invpanel.network.PacketHandler;
import info.loenwind.autoconfig.ConfigHandler;

@Mod(modid = EnderIOInvPanel.MODID, name = EnderIOInvPanel.MOD_NAME, version = EnderIOInvPanel.VERSION)
public class EnderIOInvPanel implements IEnderIOAddon {

    public static final @Nonnull String MODID = "enderioinvpanel";
    public static final @Nonnull String DOMAIN = "enderio";
    public static final @Nonnull String MOD_NAME = "Ender IO Inventory Panel";
    public static final @Nonnull String VERSION = EIOTags.VERSION;

    @SuppressWarnings("unused")
    private static ConfigHandler configHandler;

    public EnderIOInvPanel() {
        SimpleMixinLoader.loadMixinSources(this);
    }

    @EventHandler
    public void preInit(@Nonnull FMLPreInitializationEvent event) {
        configHandler = new ConfigHandlerEIO(event, Config.F);
    }

    @EventHandler
    public void init(@Nonnull FMLInitializationEvent event) {
        PacketHandler.init(event);
    }

    @Override
    @Nonnull
    public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
        return new NNList<>(Triple.of(2, null, "invpanel"), Triple.of(9, null, "capacitor_invpanel"),
                Triple.of(9, null, "hiding_invpanel"));
    }

    @Override
    @Nullable
    public Configuration getConfiguration() {
        return Config.F.getConfig();
    }
}
