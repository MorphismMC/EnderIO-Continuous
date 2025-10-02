package crazypants.enderio.conduits.conduit.item;

import static crazypants.enderio.base.init.ModObject.itemConduitProbe;
import static crazypants.enderio.conduits.init.ConduitObject.item_item_conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitNetwork;
import crazypants.enderio.base.conduit.ConduitTexture;
import crazypants.enderio.base.conduit.GuiExternalConnection;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.item.FunctionUpgrade;
import crazypants.enderio.base.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.filter.item.ItemFilter;
import crazypants.enderio.base.filter.item.items.IItemFilterItemUpgrade;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.capability.CapabilityUpgradeHolder;
import crazypants.enderio.conduits.capability.IUpgradeHolder;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.conduits.conduit.power.PowerConduit;
import crazypants.enderio.conduits.conduit.power.PowerConduitImpl;
import crazypants.enderio.conduits.gui.ItemSettings;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.conduits.render.ConduitTextureWrapper;
import crazypants.enderio.powertools.lang.Lang;
import crazypants.enderio.util.EnumReader;
import crazypants.enderio.util.Prep;

public class ItemConduitImpl extends AbstractConduit implements ItemConduit, IFilterHolder<IItemFilter>, IUpgradeHolder {

    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

    public static final @Nonnull String EXTERNAL_INTERFACE_GEOM = "ExternalInterface";

    public static final @Nonnull ConduitTexture ICON_KEY = new crazypants.enderio.conduits.render.ConduitTexture(
            TextureRegistry.registerTexture("blocks/conduit"), crazypants.enderio.conduits.render.ConduitTexture.arm(0));

    public static final @Nonnull ConduitTexture ICON_KEY_CORE = new crazypants.enderio.conduits.render.ConduitTexture(
            TextureRegistry.registerTexture("blocks/item_conduit_core"),
            crazypants.enderio.conduits.render.ConduitTexture.core());

    public static final @Nonnull ConduitTexture ICON_KEY_ENDER = new crazypants.enderio.conduits.render.ConduitTexture(
            TextureRegistry.registerTexture("blocks/ender_still"),
            new Vector4f(1.5f / 16f, 6 / 16f, 14.5f / 16f, 10 / 16f));

    ItemConduitNetwork network;

    protected final @Nonnull EnumMap<EnumFacing, RedstoneControlMode> extractionModes = new EnumMap<EnumFacing, RedstoneControlMode>(
            EnumFacing.class);
    protected final @Nonnull EnumMap<EnumFacing, DyeColor> extractionColors = new EnumMap<EnumFacing, DyeColor>(
            EnumFacing.class);

    protected final @Nonnull EnumMap<EnumFacing, IItemFilter> outputFilters = new EnumMap<EnumFacing, IItemFilter>(
            EnumFacing.class);
    protected final @Nonnull EnumMap<EnumFacing, IItemFilter> inputFilters = new EnumMap<EnumFacing, IItemFilter>(
            EnumFacing.class);
    protected final @Nonnull EnumMap<EnumFacing, ItemStack> outputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(
            EnumFacing.class);
    protected final @Nonnull EnumMap<EnumFacing, ItemStack> inputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(
            EnumFacing.class);
    protected final @Nonnull EnumMap<EnumFacing, ItemStack> functionUpgrades = new EnumMap<EnumFacing, ItemStack>(
            EnumFacing.class);

    protected final @Nonnull EnumMap<EnumFacing, Boolean> selfFeed = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

    protected final @Nonnull EnumMap<EnumFacing, Boolean> roundRobin = new EnumMap<EnumFacing, Boolean>(
            EnumFacing.class);

    protected final @Nonnull EnumMap<EnumFacing, Integer> priorities = new EnumMap<EnumFacing, Integer>(
            EnumFacing.class);

    protected final @Nonnull EnumMap<EnumFacing, DyeColor> outputColors = new EnumMap<EnumFacing, DyeColor>(
            EnumFacing.class);
    protected final @Nonnull EnumMap<EnumFacing, DyeColor> inputColors = new EnumMap<EnumFacing, DyeColor>(
            EnumFacing.class);

    private int metaData;

    public ItemConduitImpl() {
        this(0);
    }

    public ItemConduitImpl(int itemDamage) {
        metaData = itemDamage;
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
            EnumFacing dir = itr.next();
            outputFilterUpgrades.put(dir, ItemStack.EMPTY);
            inputFilterUpgrades.put(dir, ItemStack.EMPTY);
            functionUpgrades.put(dir, ItemStack.EMPTY);
        }
    }

    @Override
    protected void readTypeSettings(@Nonnull EnumFacing direction, @Nonnull NBTTagCompound data) {
        setConnectionMode(direction, EnumReader.get(ConnectionMode.class, data.getShort("connectionMode")));
        setExtractionSignalColor(direction, EnumReader.get(DyeColor.class, data.getShort("extractionSignalColor")));
        setExtractionRedstoneMode(
                EnumReader.get(RedstoneControlMode.class, data.getShort("extractionRedstoneMode")), direction);
        setInputColor(direction, EnumReader.get(DyeColor.class, data.getShort("inputColor")));
        setOutputColor(direction, EnumReader.get(DyeColor.class, data.getShort("outputColor")));
        setSelfFeedEnabled(direction, data.getBoolean("selfFeed"));
        setRoundRobinEnabled(direction, data.getBoolean("roundRobin"));
        setOutputPriority(direction, data.getInteger("outputPriority"));
    }

    @Override
    protected void writeTypeSettingsToNBT(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound data) {
        data.setShort("connectionMode", (short) getConnectionMode(dir).ordinal());
        data.setShort("extractionSignalColor", (short) getExtractionSignalColor(dir).ordinal());
        data.setShort("extractionRedstoneMode", (short) getExtractionRedstoneMode(dir).ordinal());
        data.setShort("inputColor", (short) getInputColor(dir).ordinal());
        data.setShort("outputColor", (short) getOutputColor(dir).ordinal());
        data.setBoolean("selfFeed", isSelfFeedEnabled(dir));
        data.setBoolean("roundRobin", isRoundRobinEnabled(dir));
        data.setInteger("outputPriority", getOutputPriority(dir));
    }

    @Override
    public @Nonnull NNList<ItemStack> getDrops() {
        NNList<ItemStack> res = super.getDrops();
        for (ItemStack stack : functionUpgrades.values()) {
            res.add(stack);
        }
        for (ItemStack stack : inputFilterUpgrades.values()) {
            res.add(stack);
        }
        for (ItemStack stack : outputFilterUpgrades.values()) {
            res.add(stack);
        }
        return res;
    }

    @Override
    public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res,
                                    @Nonnull List<RaytraceResult> all) {
        if (ConduitUtil.isProbeEquipped(player, hand)) {
            return false;
        } else {
            final CollidableComponent component = res.component();
            if (ToolUtil.isToolEquipped(player, hand)) {
                if (!getBundle().getTileEntity().getWorld().isRemote) {
                    if (component != null) {
                        EnumFacing faceHit = res.movingObjectPosition().sideHit;
                        if (component.isCore()) {
                            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
                                setConnectionMode(faceHit, getNextConnectionMode(faceHit));
                                return true;
                            }
                            // Attempt to join networks
                            return ConduitUtil.connectConduits(this, faceHit);
                        } else {
                            EnumFacing connDir = component.direction();
                            if (externalConnections.contains(connDir)) {
                                setConnectionMode(connDir, getNextConnectionMode(connDir));
                            } else if (containsConduitConnection(connDir)) {
                                ConduitUtil.disconnectConduits(this, connDir);
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setInputFilter(@Nonnull EnumFacing dir, @Nonnull IItemFilter filter) {
        inputFilters.put(dir, filter);
        if (network != null) {
            network.routesChanged();
        }
        setClientStateDirty();
    }

    @Override
    public void setOutputFilter(@Nonnull EnumFacing dir, @Nonnull IItemFilter filter) {
        outputFilters.put(dir, filter);
        if (network != null) {
            network.routesChanged();
        }
        setClientStateDirty();
    }

    @Override
    public IItemFilter getInputFilter(@Nonnull EnumFacing dir) {
        return inputFilters.get(dir);
    }

    @Override
    public IItemFilter getOutputFilter(@Nonnull EnumFacing dir) {
        return outputFilters.get(dir);
    }

    @Override
    public void setInputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack) {
        inputFilterUpgrades.put(dir, stack);
        setInputFilter(dir, FilterRegistry.<IItemFilter>getFilterForUpgrade(stack));
        setClientStateDirty();
    }

    @Override
    public void setOutputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack) {
        outputFilterUpgrades.put(dir, stack);
        setOutputFilter(dir, FilterRegistry.<IItemFilter>getFilterForUpgrade(stack));
        setClientStateDirty();
    }

    @Override
    @Nonnull
    public ItemStack getInputFilterUpgrade(@Nonnull EnumFacing dir) {
        return inputFilterUpgrades.get(dir);
    }

    @Override
    @Nonnull
    public ItemStack getOutputFilterUpgrade(@Nonnull EnumFacing dir) {
        return outputFilterUpgrades.get(dir);
    }

    @Override
    public void setFunctionUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack upgrade) {
        functionUpgrades.put(dir, upgrade);
        setClientStateDirty();
    }

    @Override
    @Nonnull
    public ItemStack getFunctionUpgrade(@Nonnull EnumFacing dir) {
        return functionUpgrades.get(dir);
    }

    @Override
    public int getMetaData() {
        return metaData;
    }

    @Override
    public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing direction) {
        extractionModes.put(direction, mode);
    }

    @Override
    @Nonnull
    public RedstoneControlMode getExtractionRedstoneMode(@Nonnull EnumFacing direction) {
        RedstoneControlMode res = extractionModes.get(direction);
        if (res == null) {
            res = RedstoneControlMode.NEVER;
        }
        return res;
    }

    @Override
    public void setExtractionSignalColor(@Nonnull EnumFacing direction, @Nonnull DyeColor color) {
        extractionColors.put(direction, color);
    }

    @Override
    @Nonnull
    public DyeColor getExtractionSignalColor(@Nonnull EnumFacing direction) {
        DyeColor result = extractionColors.get(direction);
        if (result == null) {
            return DyeColor.RED;
        }
        return result;
    }

    @Override
    public boolean isExtractionRedstoneConditionMet(@Nonnull EnumFacing dir) {
        RedstoneControlMode mode = getExtractionRedstoneMode(dir);
        return ConduitUtil.isRedstoneControlModeMet(this, mode, getExtractionSignalColor(dir), dir);
    }

    @Override
    public int getMaximumExtracted(@Nonnull EnumFacing dir) {
        ItemStack stack = functionUpgrades.get(dir);
        if (stack.isEmpty()) {
            return FunctionUpgrade.BASE_MAX_EXTRACTED;
        }
        FunctionUpgrade functionUpgrade = ItemFunctionUpgrade.getFunctionUpgrade(stack);
        return functionUpgrade.getMaximumExtracted(stack.getCount());
    }

    @Override
    public float getTickTimePerItem(@Nonnull EnumFacing dir) {
        float maxExtract = 10f / getMaximumExtracted(dir);
        return maxExtract;
    }

    @Override
    public void itemsExtracted(int numExtracted, int slot) {}

    @Override
    public void externalConnectionAdded(@Nonnull EnumFacing direction) {
        super.externalConnectionAdded(direction);
        checkInventoryConnections(direction);
    }

    @Override
    public IItemHandler getExternalInventory(@Nonnull EnumFacing direction) {
        World world = getBundle().getBundleworld();
        BlockPos loc = getBundle().getLocation().offset(direction);
        return ItemTools.getExternalInventory(world, loc, direction.getOpposite());
    }

    @Override
    public void externalConnectionRemoved(@Nonnull EnumFacing direction) {
        externalConnections.remove(direction);
        connectionsChanged();
        checkInventoryConnections(direction);
    }

    private void checkInventoryConnections(@Nonnull EnumFacing direction) {
        if (network != null) {
            BlockPos p = getBundle().getTileEntity().getPos().offset(direction);
            NetworkedInventory networkedInventory = network.getInventory(this, direction);
            if (externalConnections.contains(direction) && getConnectionMode(direction) != ConnectionMode.DISABLED) {
                if (networkedInventory == null) {
                    network.inventoryAdded(this, direction, p, getExternalInventory(direction));
                }
            } else {
                if (networkedInventory != null) {
                    network.inventoryRemoved(this, p);
                }
            }
        }
    }

    @Override
    public void setConnectionMode(@Nonnull EnumFacing direction, @Nonnull ConnectionMode mode) {
        ConnectionMode oldVal = connectionModes.get(direction);
        if (oldVal == mode) {
            return;
        }
        super.setConnectionMode(direction, mode);
        checkInventoryConnections(direction);
        if (network != null) {
            network.routesChanged();
        }
    }

    @Override
    public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreDisabled) {
        return getExternalInventory(direction) != null;
    }

    @Override
    @Nonnull
    protected ConnectionMode getDefaultConnectionMode() {
        return ConnectionMode.INPUT;
    }

    @Override
    @Nonnull
    public Class<? extends Conduit> getBaseConduitType() {
        return ItemConduit.class;
    }

    @Override
    @Nonnull
    public ItemStack createItem() {
        ItemStack result = new ItemStack(item_item_conduit.getItemNN(), 1, metaData);
        return result;
    }

    @Override
    public @Nullable ItemConduitNetwork getNetwork() {
        return network;
    }

    @Override
    public boolean setNetwork(@Nonnull ConduitNetwork<?, ?> network) {
        this.network = (ItemConduitNetwork) network;
        return super.setNetwork(network);
    }

    @Override
    public void clearNetwork() {
        this.network = null;
    }

    // ----------------------------------------
    // ENDER CONDUIT
    // ----------------------------------------

    @Override
    @Nonnull
    public Map<EnumFacing, DyeColor> getInputColors() {
        return inputColors;
    }

    @Override
    @Nonnull
    public Map<EnumFacing, DyeColor> getOutputColors() {
        return outputColors;
    }

    @Override
    @Nonnull
    public Map<EnumFacing, Boolean> getSelfFeed() {
        return selfFeed;
    }

    @Override
    @Nonnull
    public Map<EnumFacing, Boolean> getRoundRobin() {
        return roundRobin;
    }

    @Override
    @Nonnull
    public Map<EnumFacing, Integer> getOutputPriorities() {
        return priorities;
    }

    @Override
    public void markDirty() {
        setClientStateDirty();
        collidablesDirty = true;
    }

    @Override
    public void refreshConnection(@Nonnull EnumFacing direction) {
        if (network == null) {
            return;
        }
        network.routesChanged();
    }

    // -------------------------------------------
    // Textures
    // ------------------------------------------

    @SideOnly(Side.CLIENT)
    @Override
    public @Nonnull ConduitTexture getEnderIcon() {
        return ICON_KEY_ENDER;
    }

    @SideOnly(Side.CLIENT)
    public @Nonnull ConduitTexture getCoreIcon() {
        return ICON_KEY_CORE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public ConduitTexture getTextureForState(@Nonnull CollidableComponent component) {
        if (component.isCore()) {
            return getCoreIcon();
        }
        if (EXTERNAL_INTERFACE_GEOM.equals(component.data())) {
            return getCoreIcon();
        }
        if (PowerConduitImpl.COLOR_CONTROLLER_ID.equals(component.data())) {
            return new ConduitTextureWrapper(IconUtil.instance.whiteTexture);
        }
        return ICON_KEY;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public ConduitTexture getTransmitionTextureForState(@Nonnull CollidableComponent component) {
        return getEnderIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
        return null;
    }

    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        for (Entry<EnumFacing, IItemFilter> entry : inputFilters.entrySet()) {
            if (entry.getValue() != null) {
                IItemFilter f = entry.getValue();
                if (!isDefault(f)) {
                    NBTTagCompound itemRoot = new NBTTagCompound();
                    FilterRegistry.writeFilterToNbt(f, itemRoot);
                    data.setTag("inFilts." + entry.getKey().name(), itemRoot);
                }
            }
        }

        for (Entry<EnumFacing, ItemStack> entry : functionUpgrades.entrySet()) {
            ItemStack up = entry.getValue();
            if (up != null && Prep.isValid(up)) {
                NBTTagCompound itemRoot = new NBTTagCompound();
                up.writeToNBT(itemRoot);
                data.setTag("functionUpgrades." + entry.getKey().name(), itemRoot);
            }
        }

        for (Entry<EnumFacing, IItemFilter> entry : outputFilters.entrySet()) {
            IItemFilter f = entry.getValue();
            if (f != null && !isDefault(f)) {
                NBTTagCompound itemRoot = new NBTTagCompound();
                FilterRegistry.writeFilterToNbt(f, itemRoot);
                data.setTag("outFilts." + entry.getKey().name(), itemRoot);
            }
        }

        for (Entry<EnumFacing, ItemStack> entry : inputFilterUpgrades.entrySet()) {
            ItemStack up = entry.getValue();
            if (up != null && Prep.isValid(up)) {
                IItemFilter filter = getInputFilter(entry.getKey());
                FilterRegistry.writeFilterToStack(filter, up);

                NBTTagCompound itemRoot = new NBTTagCompound();
                up.writeToNBT(itemRoot);
                data.setTag("inputFilterUpgrades." + entry.getKey().name(), itemRoot);
            }
        }

        for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
            ItemStack up = entry.getValue();
            if (up != null && Prep.isValid(up)) {
                IItemFilter filter = getOutputFilter(entry.getKey());
                FilterRegistry.writeFilterToStack(filter, up);

                NBTTagCompound itemRoot = new NBTTagCompound();
                up.writeToNBT(itemRoot);
                data.setTag("outputFilterUpgrades." + entry.getKey().name(), itemRoot);
            }
        }

        for (Entry<EnumFacing, RedstoneControlMode> entry : extractionModes.entrySet()) {
            if (entry.getValue() != null) {
                short ord = (short) entry.getValue().ordinal();
                data.setShort("extRM." + entry.getKey().name(), ord);
            }
        }

        for (Entry<EnumFacing, DyeColor> entry : extractionColors.entrySet()) {
            if (entry.getValue() != null) {
                short ord = (short) entry.getValue().ordinal();
                data.setShort("extSC." + entry.getKey().name(), ord);
            }
        }

        for (Entry<EnumFacing, Boolean> entry : selfFeed.entrySet()) {
            if (entry.getValue() != null) {
                data.setBoolean("selfFeed." + entry.getKey().name(), entry.getValue());
            }
        }

        for (Entry<EnumFacing, Boolean> entry : roundRobin.entrySet()) {
            if (entry.getValue() != null) {
                data.setBoolean("roundRobin." + entry.getKey().name(), entry.getValue());
            }
        }

        for (Entry<EnumFacing, Integer> entry : priorities.entrySet()) {
            if (entry.getValue() != null) {
                data.setInteger("priority." + entry.getKey().name(), entry.getValue());
            }
        }

        for (Entry<EnumFacing, DyeColor> entry : inputColors.entrySet()) {
            if (entry.getValue() != null) {
                short ord = (short) entry.getValue().ordinal();
                data.setShort("inSC." + entry.getKey().name(), ord);
            }
        }

        for (Entry<EnumFacing, DyeColor> entry : outputColors.entrySet()) {
            if (entry.getValue() != null) {
                short ord = (short) entry.getValue().ordinal();
                data.setShort("outSC." + entry.getKey().name(), ord);
            }
        }
    }

    private boolean isDefault(IItemFilter f) {
        if (f instanceof ItemFilter) {
            return ((ItemFilter) f).isDefault();
        }
        return false;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("metaData")) {
            metaData = data.getShort("metaData");
        } else {
            metaData = 0;
        }

        for (EnumFacing dir : EnumFacing.VALUES) {

            String key = "inFilts." + dir.name();
            if (data.hasKey(key)) {
                NBTTagCompound filterTag = (NBTTagCompound) data.getTag(key);
                IItemFilter filter = (IItemFilter) FilterRegistry.loadFilterFromNbt(filterTag);
                inputFilters.put(dir, filter);
            }

            key = "functionUpgrades." + dir.name();
            if (data.hasKey(key)) {
                NBTTagCompound upTag = (NBTTagCompound) data.getTag(key);
                ItemStack ups = new ItemStack(upTag);
                functionUpgrades.put(dir, ups);
            }

            key = "inputFilterUpgrades." + dir.name();
            if (data.hasKey(key)) {
                NBTTagCompound upTag = (NBTTagCompound) data.getTag(key);
                ItemStack ups = new ItemStack(upTag);
                inputFilterUpgrades.put(dir, ups);
            }

            key = "outputFilterUpgrades." + dir.name();
            if (data.hasKey(key)) {
                NBTTagCompound upTag = (NBTTagCompound) data.getTag(key);
                ItemStack ups = new ItemStack(upTag);
                outputFilterUpgrades.put(dir, ups);
            }

            key = "outFilts." + dir.name();
            if (data.hasKey(key)) {
                NBTTagCompound filterTag = (NBTTagCompound) data.getTag(key);
                IItemFilter filter = (IItemFilter) FilterRegistry.loadFilterFromNbt(filterTag);
                outputFilters.put(dir, filter);
            }

            key = "extRM." + dir.name();
            if (data.hasKey(key)) {
                short ord = data.getShort(key);
                if (ord >= 0 && ord < RedstoneControlMode.values().length) {
                    extractionModes.put(dir, RedstoneControlMode.values()[ord]);
                }
            }
            key = "extSC." + dir.name();
            if (data.hasKey(key)) {
                short ord = data.getShort(key);
                if (ord >= 0 && ord < DyeColor.values().length) {
                    extractionColors.put(dir, DyeColor.values()[ord]);
                }
            }
            key = "selfFeed." + dir.name();
            if (data.hasKey(key)) {
                boolean val = data.getBoolean(key);
                selfFeed.put(dir, val);
            }

            key = "roundRobin." + dir.name();
            if (data.hasKey(key)) {
                boolean val = data.getBoolean(key);
                roundRobin.put(dir, val);
            }

            key = "priority." + dir.name();
            if (data.hasKey(key)) {
                int val = data.getInteger(key);
                priorities.put(dir, val);
            }

            key = "inSC." + dir.name();
            if (data.hasKey(key)) {
                short ord = data.getShort(key);
                if (ord >= 0 && ord < DyeColor.values().length) {
                    inputColors.put(dir, DyeColor.values()[ord]);
                }
            }

            key = "outSC." + dir.name();
            if (data.hasKey(key)) {
                short ord = data.getShort(key);
                if (ord >= 0 && ord < DyeColor.values().length) {
                    outputColors.put(dir, DyeColor.values()[ord]);
                }
            }
        }

        connectionsDirty = true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
        super.hashCodeForModelCaching(hashCodes);
        hashCodes.addEnum(outputColors);
        hashCodes.addEnum(inputColors);
        hashCodes.addEnum(extractionColors);
        hashCodes.addEnum(extractionModes);
    }

    @Override
    public void invalidate() {
        if (network != null) {
            final BlockPos pos = getBundle().getTileEntity().getPos();
            for (EnumFacing direction : externalConnections) {
                try {
                    BlockPos p = pos.offset(direction);
                    network.inventoryRemoved(this, p);
                } catch (Throwable t) {
                    // silent
                }
            }
        }
    }

    @Override
    @Nonnull
    public ItemConduitNetwork createNetworkForType() {
        return new ItemConduitNetwork();
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    public ITabPanel createGuiPanel(@Nonnull GuiExternalConnection gui, @Nonnull ConduitClient conduit) {
        return new ItemSettings(gui, conduit);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean updateGuiPanel(@Nonnull ITabPanel panel) {
        if (panel instanceof ItemSettings) {
            return ((ItemSettings) panel).updateConduit(this);
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getGuiPanelTabOrder() {
        return 0;
    }

    // Only uses the Filter and Upgrade Capabilities, since conduits don't have an inventory
    @Override
    public boolean hasInternalCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY ||
                capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY &&
                        getExternalConnections().contains(facing)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getInternalCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY ||
                capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY) {
            return (T) this;
        }
        return null;
    }

    @Override
    public IItemFilter getFilter(int filterId, int param1) {
        if (filterId == FilterGuiUtil.INDEX_INPUT_ITEM) {
            return getInputFilter(EnumFacing.byIndex(param1));
        } else if (filterId == FilterGuiUtil.INDEX_OUTPUT_ITEM) {
            return getOutputFilter(EnumFacing.byIndex(param1));
        }
        return null;
    }

    @Override
    public void setFilter(int filterId, EnumFacing side, @Nonnull IItemFilter filter) {
        if (filterId == FilterGuiUtil.INDEX_INPUT_ITEM) {
            setInputFilter(side, filter);
        } else if (filterId == FilterGuiUtil.INDEX_OUTPUT_ITEM) {
            setOutputFilter(side, filter);
        }
    }

    @Override
    @Nullable
    public IItemHandler getInventoryForSnapshot(int filterId, int param1) {
        ItemConduitNetwork icn = getNetwork();
        if (icn != null) {
            return icn.getInventory(this, EnumFacing.byIndex(param1)).getInventory();
        }
        return null;
    }

    @Override
    @Nonnull
    public ItemStack getFilterStack(int filterIndex, EnumFacing side) {
        if (filterIndex == FilterGuiUtil.INDEX_INPUT_ITEM) {
            return getInputFilterUpgrade(side);
        } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT_ITEM) {
            return getOutputFilterUpgrade(side);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setFilterStack(int filterIndex, EnumFacing side, @Nonnull ItemStack stack) {
        if (filterIndex == FilterGuiUtil.INDEX_INPUT_ITEM) {
            setInputFilterUpgrade(side, stack);
        } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT_ITEM) {
            setOutputFilterUpgrade(side, stack);
        }
    }

    @Override
    @Nonnull
    public ItemStack getUpgradeStack(int param1) {
        return getFunctionUpgrade(EnumFacing.byIndex(param1));
    }

    @Override
    public void setUpgradeStack(int param1, @Nonnull ItemStack stack) {
        setFunctionUpgrade(EnumFacing.byIndex(param1), stack);
    }

    @Override
    public int getUpgradeSlotLimit(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemFunctionUpgrade ?
                ((ItemFunctionUpgrade) stack.getItem()).getUpgradeSlotLimit() :
                IUpgradeHolder.super.getUpgradeSlotLimit(stack);
    }

    @Override
    @Nonnull
    public List<String> getFunctionUpgradeToolTipText(@Nonnull EnumFacing dir) {
        return new NNList<>(crazypants.enderio.conduits.lang.Lang.GUI_ITEM_FUNCTION_UPGRADE_DETAILS.get(),
                crazypants.enderio.conduits.lang.Lang.GUI_ITEM_FUNCTION_UPGRADE_DETAILS2.get(getMaximumExtracted(dir)));
    }

    @Override
    public int getInputFilterIndex() {
        return FilterGuiUtil.INDEX_INPUT_ITEM;
    }

    @Override
    public int getOutputFilterIndex() {
        return FilterGuiUtil.INDEX_OUTPUT_ITEM;
    }

    @Override
    public boolean isFilterUpgradeAccepted(@Nonnull ItemStack stack, boolean isInput) {
        return stack.getItem() instanceof IItemFilterItemUpgrade;
    }

    @Override
    @Nonnull
    public NNList<ITextComponent> getConduitProbeInformation(@Nonnull EntityPlayer player) {
        final NNList<ITextComponent> result = super.getConduitProbeInformation(player);

        if (getExternalConnections().isEmpty()) {
            ITextComponent elem = Lang.GUI_CONDUIT_PROBE_ITEM_HEADING_NO_CONNECTIONS.toChatServer();
            elem.getStyle().setColor(TextFormatting.GOLD);
            result.add(elem);
        } else {
            ItemStack input = player.getHeldItemMainhand();
            if (input.getItem() == itemConduitProbe.getItemNN()) {
                input = player.getHeldItemOffhand();
            }
            ItemConduitNetwork icn = getNetwork();

            for (EnumFacing dir : getExternalConnections()) {
                if (dir == null) {
                    continue;
                }
                ConnectionMode mode = getConnectionMode(dir);

                ITextComponent elem = Lang.GUI_CONDUIT_PROBE_ITEM_HEADING
                        .toChatServer(new TextComponentTranslation(EnderIO.lang.addPrefix("facing." + dir)));
                elem.getStyle().setColor(TextFormatting.GREEN);
                result.add(elem);

                if (icn != null && mode.acceptsInput()) {
                    List<String> targets = icn.getTargetsForExtraction(getBundle().getLocation().offset(dir), this,
                            input);
                    if (input.isEmpty()) {
                        if (targets.isEmpty()) {
                            elem = Lang.GUI_CONDUIT_PROBE_EXTRACT_NO_ITEM_NO_TARGET.toChatServer();
                        } else {
                            elem = Lang.GUI_CONDUIT_PROBE_EXTRACT_NO_ITEM_TARGETS.toChatServer();
                        }
                    } else {
                        if (targets.isEmpty()) {
                            elem = Lang.GUI_CONDUIT_PROBE_EXTRACT_ITEM_NO_TARGET.toChatServer(input.getDisplayName());
                        } else {
                            elem = Lang.GUI_CONDUIT_PROBE_EXTRACT_ITEM_TARGETS.toChatServer(input.getDisplayName());
                        }
                    }
                    elem.getStyle().setColor(TextFormatting.BLUE);
                    result.add(elem);
                    for (String str : targets) {
                        result.add(new TextComponentString("  -> " + str));
                    }
                }

                if (icn != null && mode.acceptsOutput()) {
                    List<String> sources = icn.getInputSourcesFor(this, dir, input);
                    if (sources.isEmpty()) {
                        if (input.isEmpty()) {
                            elem = Lang.GUI_CONDUIT_PROBE_NO_ITEMS.toChatServer();
                        } else {
                            elem = Lang.GUI_CONDUIT_PROBE_NO_ITEM.toChatServer(input.getDisplayName());
                        }
                    } else {
                        if (input.isEmpty()) {
                            elem = Lang.GUI_CONDUIT_PROBE_RECEIVE_ITEMS.toChatServer();
                        } else {
                            elem = Lang.GUI_CONDUIT_PROBE_RECEIVE_ITEM.toChatServer(input.getDisplayName());
                        }
                    }
                    elem.getStyle().setColor(TextFormatting.BLUE);
                    result.add(elem);
                    for (String str : sources) {
                        result.add(new TextComponentString("  -> " + str));
                    }
                }
            }
        }

        return result;
    }

    @Override
    @Nonnull
    public Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key) {
        Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
        final EnumFacing keydir = key.direction;
        if (keydir == null) {
            return baseCollidables;
        }

        BoundingBox bb = ConduitGeometryUtil.getINSTANCE().createBoundsForConnectionController(keydir, key.offset);
        CollidableComponent cc = new CollidableComponent(ItemConduit.class, bb, keydir,
                PowerConduit.COLOR_CONTROLLER_ID);

        List<CollidableComponent> result = new ArrayList<CollidableComponent>();
        result.addAll(baseCollidables);
        result.add(cc);

        return result;
    }
}
