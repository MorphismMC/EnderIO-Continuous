package crazypants.enderio.conduits.autosave;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.powertools.machine.monitor.StatCollector;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public class StatCollectorHandler implements IHandler<StatCollector> {

    @NotNull
    @Override
    public Class<?> getRootType() {
        return StatCollector.class;
    }

    @Override
    public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound data, Type type, String name,
                         StatCollector object) throws IllegalArgumentException, IllegalAccessException,
                                                      InstantiationException, NoHandlerFoundException {
        NBTTagCompound nbtData = new NBTTagCompound();
        nbtData.setInteger("pos", object.getPos());
        nbtData.setInteger("count", object.getCollectCount());
        nbtData.setByteArray("data", object.getData());
        data.setTag(name, nbtData);
        return true;
    }

    @Override
    public StatCollector read(Registry registry, Set<NBTAction> phase, NBTTagCompound data, Type type, String name,
                              @Nullable StatCollector object) throws IllegalArgumentException, IllegalAccessException,
                                                                     InstantiationException, NoHandlerFoundException {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        NBTTagCompound nbtData = data.getCompoundTag(name);
        object.setPos(nbtData.getInteger("pos"));
        object.setCollectCount(nbtData.getInteger("count"));
        object.setData(nbtData.getByteArray("data"));
        return object;
    }

}
