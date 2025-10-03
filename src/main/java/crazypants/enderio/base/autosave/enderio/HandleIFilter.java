package crazypants.enderio.base.autosave.enderio;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.Filter;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;

public class HandleIFilter implements IHandler<Filter> {

    @Override
    public @Nonnull Class<?> getRootType() {
        return Filter.class;
    }

    @Override
    public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt,
                         @Nonnull Type type, @Nonnull String name, @Nonnull Filter object)
                                                                                            throws IllegalArgumentException,
                                                                                            IllegalAccessException,
                                                                                            InstantiationException,
                                                                                            NoHandlerFoundException {
        NBTTagCompound root = new NBTTagCompound();
        FilterRegistry.writeFilterToNbt(object, root);
        nbt.setTag(name, root);
        return true;
    }

    @Override
    @Nullable
    public Filter read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt,
                       @Nonnull Type type, @Nonnull String name,
                       @Nullable Filter object) throws IllegalArgumentException, IllegalAccessException,
                                                  InstantiationException, NoHandlerFoundException {
        if (object == null && !nbt.hasKey(name)) {
            // Note: This will be called with no nbt when a fresh itemstack is placed---output should be null!
            return object;
        }
        object = FilterRegistry.loadFilterFromNbt(nbt.getCompoundTag(name));
        return object;
    }
}
