package crazypants.enderio.base.material.alloy;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.ItemEIO;

public class ItemBlockAlloy extends ItemEIO {

    public ItemBlockAlloy(@Nonnull BlockAlloy block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public @Nonnull String getTranslationKey(@Nonnull ItemStack stack) {
        return getTranslationKey() + "." + Alloy.getTypeFromMeta(stack.getItemDamage()).getBaseName();
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) {
            NNList.of(Alloy.class).apply(new Callback<Alloy>() {

                @Override
                public void apply(@Nonnull Alloy alloy) {
                    list.add(new ItemStack(ItemBlockAlloy.this, 1, Alloy.getMetaFromType(alloy)));
                }
            });
        }
    }
}
