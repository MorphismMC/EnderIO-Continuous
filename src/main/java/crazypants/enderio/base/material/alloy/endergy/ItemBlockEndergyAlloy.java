package crazypants.enderio.base.material.alloy.endergy;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.ItemEIO;

public class ItemBlockEndergyAlloy extends ItemEIO {

    public ItemBlockEndergyAlloy(@Nonnull BlockEndergyAlloy block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public @Nonnull String getTranslationKey(@Nonnull ItemStack stack) {
        return getTranslationKey() + "." + AlloyEndergy.getTypeFromMeta(stack.getItemDamage()).getBaseName();
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) {
            NNList.of(AlloyEndergy.class).apply(new Callback<AlloyEndergy>() {

                @Override
                public void apply(@Nonnull AlloyEndergy alloy) {
                    list.add(new ItemStack(ItemBlockEndergyAlloy.this, 1, AlloyEndergy.getMetaFromType(alloy)));
                }
            });
        }
    }
}
