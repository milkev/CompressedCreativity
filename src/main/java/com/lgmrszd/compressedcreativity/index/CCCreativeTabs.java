package com.lgmrszd.compressedcreativity.index;

import com.lgmrszd.compressedcreativity.CompressedCreativity;
import com.lgmrszd.compressedcreativity.index.CCBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import me.desht.pneumaticcraft.common.registry.ModCreativeModeTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = CompressedCreativity.MOD_ID)
public class CCCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CompressedCreativity.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASE_CREATIVE_TAB = REGISTER.register("base",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup."+CompressedCreativity.MOD_ID+".main"))
                    .icon(CCBlocks.ROTATIONAL_COMPRESSOR::asStack)
                    .displayItems((params, output) -> {
                        List<ItemStack> items = CompressedCreativity.REGISTRATE.getAll(Registries.ITEM)
                                .stream()
                                .map((regItem) -> new ItemStack(regItem.get()))
                                .toList();
                        output.acceptAll(items);
                    })
                    .build());

    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
