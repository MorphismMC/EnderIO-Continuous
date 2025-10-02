package crazypants.enderio.conduits.autosave;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.util.HandleSimpleCollection;
import info.loenwind.autosave.util.NBTAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConduitHandler implements IHandler<Conduit> {

    @NotNull
    @Override
    public Class<?> getRootType() {
        return Conduit.class;
    }

    @Override
    public boolean store(@NotNull Registry registry, @NotNull Set<NBTAction> phase, @NotNull NBTTagCompound data,
                         @NotNull Type type, @NotNull String name,
                         @NotNull Conduit object) throws IllegalArgumentException, IllegalAccessException,
                                                         InstantiationException, NoHandlerFoundException {
        if (object instanceof ConduitServer) {
            NBTTagCompound nbtData = new NBTTagCompound();
            ConduitUtil.writeToNBT((ConduitServer) object, nbtData);
            data.setTag(name, nbtData);
        } else {
            Log.error("Logic error: Attempting to store client conduit procy as NBT for phase(S) " + phase);
        }
        return true;
    }

    @Override
    public Conduit read(@NotNull Registry registry, @NotNull Set<NBTAction> phase, @NotNull NBTTagCompound data,
                        @NotNull Type type, @NotNull String name,
                        @Nullable Conduit object) throws IllegalArgumentException, IllegalAccessException,
                                                         InstantiationException, NoHandlerFoundException {
        if (data.hasKey(name)) {
            NBTTagCompound nbtData = data.getCompoundTag(name);
            object = read(phase, nbtData);
            if (object == null) {
                // TODO: Remove it, this is for compatibility with early 1.12.2 dev builds.
                NBTTagCompound conduitTag = NbtValue.CONDUIT.getTag(nbtData);
                if (conduitTag != null) {
                    object = read(phase, conduitTag);
                }
            }
        }
        return object;
    }

    private Conduit read(@NotNull Set<NBTAction> phase, @NotNull NBTTagCompound conduitTag) {
        return phase.contains(NBTAction.CLIENT) ? ConduitUtil.readClientConduitFromNBT(conduitTag) :
                ConduitUtil.readConduitFromNBT(conduitTag);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static class List extends HandleSimpleCollection<CopyOnWriteArrayList<Conduit>> {

        public List() throws NoHandlerFoundException {
            super((Class<CopyOnWriteArrayList<Conduit>>) (Class) CopyOnWriteArrayList.class);
        }

        protected List(Registry registry) throws NoHandlerFoundException {
            super((Class<CopyOnWriteArrayList<Conduit>>) (Class) CopyOnWriteArrayList.class, CopyOnWriteArrayList::new,
                    registry, Conduit.class);
        }

        @Override
        protected IHandler<? extends CopyOnWriteArrayList<Conduit>> create(@NotNull Registry registry,
                                                                           @NotNull Type... types) throws NoHandlerFoundException {
            if (types[0] == Conduit.class) {
                return new List(registry);
            }
            return null;
        }

        @Override
        public CopyOnWriteArrayList<Conduit> read(@NotNull Registry registry, @NotNull Set<NBTAction> phase,
                                                  @NotNull NBTTagCompound nbt, @NotNull Type type,
                                                  @NotNull String name,
                                                  @Nullable CopyOnWriteArrayList<Conduit> object) throws IllegalArgumentException,
                                                                                                         IllegalAccessException,
                                                                                                         InstantiationException,
                                                                                                         NoHandlerFoundException {
            final CopyOnWriteArrayList<Conduit> result = super.read(registry, phase, nbt, type, name, object);
            if (result != null) {
                // Remove null (missing) conduits
                while (result.remove(null)) {}
            }
            return result;
        }

    }

}
