package crazypants.enderio.api.teleport;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.UserIdent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface ITravelAccessable {

    enum AccessMode {
        PUBLIC,
        PRIVATE,
        PROTECTED
    }

    boolean canBlockBeAccessed(EntityPlayer playerName);

    boolean canSeeBlock(EntityPlayer playerName);

    boolean canUiBeAccessed(EntityPlayer username);

    boolean getRequiresPassword(EntityPlayer username);

    boolean authoriseUser(EntityPlayer username, ItemStack[] password);

    AccessMode getAccessMode();

    void setAccessMode(AccessMode accessMode);

    NNList<ItemStack> getPassword();

    void setPassword(NNList<ItemStack> password);

    ItemStack getItemLabel();

    void setItemLabel(ItemStack lableIcon);

    @Nullable
    String getLabel();

    void setLabel(@Nullable String label);

    UserIdent getOwner();

    void clearAuthorisedUsers();

    BlockPos getLocation();

    /**
     * Is this block a travel source for traveling to other travel anchors?
     */
    default boolean isTravelSource() {
        return true;
    }

    /**
     * Is this block a visible travel target for the staff or a travel anchor?
     */
    default boolean isVisible() {
        return true;
    }

    default void setVisible(boolean visible) {
    }

    /**
     * If this block is used as a travel source, how far is the travel range?
     */
    int getTravelRangeDeparting();
}
