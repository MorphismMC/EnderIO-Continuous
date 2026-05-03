package com.morphismmc.enderio.api.register;

import com.morphismmc.enderio.api.register.builder.BlockBuilder;
import com.morphismmc.enderio.api.register.builder.DefaultRegistryEntryBuilder;
import com.morphismmc.enderio.api.register.builder.ItemBuilder;
import com.morphismmc.enderio.api.register.builder.RegistryEntryBuilder;
import com.morphismmc.enderio.api.register.builder.RegistryEntryBuilderOp;
import com.morphismmc.enderio.api.register.builder.TileEntityBuilder;
import com.morphismmc.enderio.api.register.entry.TileEntityEntry;
import com.morphismmc.enderio.api.register.entry.BlockEntry;
import com.morphismmc.enderio.api.register.entry.ItemEntry;
import com.morphismmc.enderio.api.register.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = "enderio")
public class ModRegistration implements RegistryEntryBuilderOp {

    private final String modId;

    private record Registration<R extends IForgeRegistryEntry<R>, T extends R>(String name,
            IForgeRegistry<R> registry,
            Supplier<? extends T> creator,
            RegistryEntry<R, T> delegate,
            List<Consumer<? super T>> callbacks) {}

    private final ObjectList<Registration<?, ?>> registrations = new ObjectArrayList<>();
    private final ObjectList<TileEntityEntry<?>> tileEntities = new ObjectArrayList<>();

    public record PreInitTileEntity(ResourceLocation name, Class<? extends TileEntity> teClass) {}
    private final ObjectList<PreInitTileEntity> preTileEntities = new ObjectArrayList<>();

    public ModRegistration(final String modId) {
        this.modId = modId;
        MinecraftForge.EVENT_BUS.register(this);
    }

    // region Builder

    public <T extends Block> BlockBuilder<T, ModRegistration> block(final String name, final Supplier<? extends T> factory) {
        return new BlockBuilder<>(this, name, ForgeRegistries.BLOCKS, factory, this);
    }

    public <T extends Item> ItemBuilder<T, ModRegistration> item(final String name, final Supplier<? extends T> factory) {
        return new ItemBuilder<>(this, name, ForgeRegistries.ITEMS, factory, this);
    }

    public <T extends TileEntity> TileEntityBuilder<T, ModRegistration> tileEntity(final String name, final Class<T> teClass) {
        return new TileEntityBuilder<>(this, name, teClass, this);
    }

    public void tileEntity(final TileEntityEntry<?> entry) { // TODO: Inner it in TileEntityBuilder?
        tileEntities.add(entry);
    }

    public <T extends TileEntity> void tileEntity(final ResourceLocation name, final Class<T> teClass) {
        tileEntities.add(new TileEntityEntry<>(name, teClass));
    }

    public void tileEntityPre(final PreInitTileEntity te) {
        preTileEntities.add(te);
    }

    public <T extends TileEntity> void tileEntityPre(final ResourceLocation name, final Class<T> teClass) {
        preTileEntities.add(new PreInitTileEntity(name, teClass));
    }

    // endregion

    // region Forge Event

    @SubscribeEvent
    public void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        for (final Registration<?, ?> reg : registrations) {
            if (reg.registry.getRegistrySuperType() == Block.class) {
                registerEntry(event.getRegistry(), reg);
            }
        }
    }

    @SubscribeEvent
    public void onRegisterItems(final RegistryEvent.Register<Item> event) {
        for (final Registration<?, ?> reg : registrations) {
            if (reg.registry.getRegistrySuperType() == Item.class) {
                registerEntry(event.getRegistry(), reg);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegisterTileEntities(final RegistryEvent.Register<Block> event) {
        for (PreInitTileEntity preTE : preTileEntities) {
            GameRegistry.registerTileEntity(preTE.teClass, preTE.name);
        }
    }

    @SuppressWarnings("unchecked")
    private static <R extends IForgeRegistryEntry<R>, T extends R> void registerEntry(
            final IForgeRegistry<R> registry,
            final Registration<?, ?> rawReg) {
        final Registration<R, T> reg = (Registration<R, T>) rawReg;
        final T entry = reg.creator.get();
        registry.register(entry);
        for (final Consumer<? super T> callback : reg.callbacks) {
            callback.accept(entry);
        }
    }

    // endregion

    @SuppressWarnings("unchecked")
    @Override
    public <R extends IForgeRegistryEntry<R>, T extends R> RegistryEntry<R, T> accept(
            final String name,
            final IForgeRegistry<R> registry,
            final RegistryEntryBuilder<R, T, ?, ?> builder,
            final Supplier<? extends T> creator,
            final Function<RegistryEntry<R, T>, ? extends RegistryEntry<R, T>> entryFactory) {
        final ResourceLocation fullName = new ResourceLocation(modId, name);
        final RegistryEntry<R, T> baseEntry = new RegistryEntry<>(fullName, registry);
        final RegistryEntry<R, T> typedEntry = entryFactory.apply(baseEntry);

        final ObjectList<Consumer<? super T>> callbacks = new ObjectArrayList<>();
        if (builder instanceof DefaultRegistryEntryBuilder<?, ?, ?, ?> defaultBuilder) { // holy shxt generic...
            final var fixedBuilder = (DefaultRegistryEntryBuilder<R, T, ?, ?>) defaultBuilder;
            callbacks.add(fixedBuilder::fireOnRegisterCallbacks);
        }

        final Registration<R, T> reg = new Registration<>(name, registry, creator, typedEntry, callbacks);
        registrations.add(reg);

        return typedEntry;
    }

    public @Nullable BlockEntry<?> getBlock(final String name) {
        final ResourceLocation blockKey = new ResourceLocation(modId, name);
        if (ForgeRegistries.BLOCKS.containsKey(blockKey)) {
            return new BlockEntry<>(blockKey, ForgeRegistries.BLOCKS);
        }
        return null;
    }

    public @Nullable ItemEntry<?> getItem(final String name) {
        final ResourceLocation itemKey = new ResourceLocation(modId, name);
        if (ForgeRegistries.ITEMS.containsKey(itemKey)) {
            return new ItemEntry<>(itemKey, ForgeRegistries.ITEMS);
        }
        return null;
    }

    public @Unmodifiable List<TileEntityEntry<?>> getTileEntities() {
        return List.copyOf(tileEntities);
    }

    public @Unmodifiable List<PreInitTileEntity> getPreTileEntities() {
        return List.copyOf(preTileEntities);
    }

    public String modId() {
        return modId;
    }
}
