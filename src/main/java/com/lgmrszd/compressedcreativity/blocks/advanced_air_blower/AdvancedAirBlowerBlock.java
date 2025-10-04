package com.lgmrszd.compressedcreativity.blocks.advanced_air_blower;

import com.lgmrszd.compressedcreativity.blocks.air_blower.AirBlowerBlock;
import com.lgmrszd.compressedcreativity.blocks.air_blower.AirBlowerBlockEntity;
import com.lgmrszd.compressedcreativity.index.CCBlockEntities;
import com.lgmrszd.compressedcreativity.items.MeshItem;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;

public class AdvancedAirBlowerBlock extends AirBlowerBlock {
    public AdvancedAirBlowerBlock(Properties properties) {
        super(properties);
    }


    @Override
    public boolean canConnect(Direction facing, BlockEntity other_te) {
        return other_te != null && (
                PNCCapabilities.getAirHandler(other_te, facing.getOpposite()).isPresent()
                || PNCCapabilities.getHeatLogic(other_te, facing.getOpposite()).isPresent()
        );
    }

    @Override
    public void onRemove(BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            AirBlowerBlockEntity be = getBlockEntity(world, pos);
            if (be instanceof AdvancedAirBlowerBlockEntity bbe) {
                Block.popResource(world, pos, bbe.getMesh());
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        AirBlowerBlockEntity be = getBlockEntity(world, pos);
        if (player != null && !world.isClientSide() && be instanceof AdvancedAirBlowerBlockEntity bbe) {
            ItemStack mesh = bbe.getMesh();
            if (!mesh.isEmpty()) {
                player.getInventory().placeItemBackInInventory(mesh);
                bbe.setMesh(ItemStack.EMPTY);
                IWrenchable.playRemoveSound(world, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return super.onWrenched(state, context);
    }



    @Override
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);
        BlockEntity te = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        if (te instanceof AdvancedAirBlowerBlockEntity be) {
            be.updateHeatExchanger();
        }
    }

    @Nonnull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean client = level.isClientSide();
        if(heldItem.getItem() instanceof MeshItem) {
            return onBlockEntityUseItemOn(level, pos, be -> {
                if (!(be instanceof AdvancedAirBlowerBlockEntity abbe))
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                ItemStack installedMesh = abbe.getMesh();
                // Ignoring if mesh is the same
                if (heldItem.getItem() == installedMesh.getItem()) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                if (client) return ItemInteractionResult.SUCCESS;
                ItemStack oldMesh = tryInstallMesh(level, pos, abbe, heldItem);
                if (!oldMesh.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(oldMesh);
                }
                return ItemInteractionResult.SUCCESS;
            });
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public ItemStack tryInstallMesh(Level world, BlockPos pos, AdvancedAirBlowerBlockEntity abte, ItemStack stack) {
        if (!(stack.getItem() instanceof MeshItem)) return ItemStack.EMPTY;
        IWrenchable.playRotateSound(world, pos);
        ItemStack oldMesh = abte.getMesh();
        abte.setMesh(stack);
        stack.shrink(1);
        return oldMesh;
    }

    @Override
    public BlockEntityType<? extends AdvancedAirBlowerBlockEntity> getBlockEntityType() {
        return CCBlockEntities.INDUSTRIAL_AIR_BLOWER.get();
    }
}
