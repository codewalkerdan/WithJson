package club.someoneice.ovo.core

import club.someoneice.ovo.OVOMain
import club.someoneice.ovo.core.`object`.DataList
import club.someoneice.ovo.core.`object`.Info
import club.someoneice.ovo.data.Group
import club.someoneice.ovo.json.data.CoreDataOutput
import club.someoneice.ovo.json.data.helper.RecipeJsonHelper
import club.someoneice.ovo.util.*
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.MapColor
import net.minecraft.item.*
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier


class DataProcessor(private val mod_id: String?) {
    companion object {
        val REGISTRY_MAP : HashMap<String, RegistryKey<ItemGroup>> = HashMap()
        val ITEM_SETTINGS_MAP : HashMap<String, FabricItemSettings> = HashMap();
    }
    init {
        toolInfoProcessing()
        toolGroupProcessing()
        toolItemProcessing()
        toolBlockProcessing()
    }

    private fun toolInfoProcessing() {
        Info.modid = this.mod_id ?: OVOMain.modid
        OVOMain.Logger.info("Get modid: $mod_id !")
    }

    private fun toolGroupProcessing() {
        OVOMain.Logger.info("Now create the new group!")

        for (group in DataList.dataGroup) {
            try {
                DataList.getGroup[group.name] = toolCreateGroupHelper(group)
                OVOMain.Logger.info("Create group: ${group.name} !")
            } catch (_: Exception) {
                OVOMain.Logger.error("Cannot create the new group!")
            }
        }
    }

    private fun toolCreateGroupHelper(group: Group): ItemGroup {
        val itemGroup = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier(Info.modid, group.name))
        REGISTRY_MAP[group.name] = itemGroup
        ITEM_SETTINGS_MAP[group.name] = FabricItemSettings()
        return Registry.register(Registries.ITEM_GROUP, itemGroup, FabricItemGroup.builder()
            .displayName(Text.translatable(Info.modid + "." + group.name))
            .icon { ItemStack(Util.findItemByText(group.icon) ?: Items.APPLE) }
            .build())
    }

    private fun toolItemProcessing() {
        for (item in DataList.dataItem) {
            ItemGroupEvents.modifyEntriesEvent(REGISTRY_MAP[item.group])
                .register{content: FabricItemGroupEntries -> content.add(DataList.getGroup[item.group]?.icon)}
            ITEM_SETTINGS_MAP[item.group]?.let { ItemBase(it.maxCount(item.max_size), item) }
        }

        for (item in DataList.dataItemFood) {
            ItemGroupEvents.modifyEntriesEvent(REGISTRY_MAP[item.group])
                .register{content: FabricItemGroupEntries -> content.add(DataList.getGroup[item.group]?.icon)}

            val build = FoodComponent.Builder()
            build.hunger(item.hunger)
            build.saturationModifier(item.saturation)
            if (item.wolf) build.meat()
            if (item.fast_food) build.snack()
            if (item.always_eat) build.alwaysEdible()
            ITEM_SETTINGS_MAP[item.group]?.let { ItemFoodsBase(it.maxCount(item.max_size).food(build.build()), item) }
        }

        for (item in DataList.dataItemGift) {
            ItemGroupEvents.modifyEntriesEvent(REGISTRY_MAP[item.group])
                .register{content: FabricItemGroupEntries -> content.add(DataList.getGroup[item.group]?.icon)}

            ITEM_SETTINGS_MAP[item.group]?.let { ItemGifts(it.maxCount(item.max_size), item) }
        }

        for (tool in DataList.dataItemTool) {
            ItemGroupEvents.modifyEntriesEvent(REGISTRY_MAP[tool.group])
                .register{content: FabricItemGroupEntries -> content.add(DataList.getGroup[tool.group]?.icon)}
            ITEM_SETTINGS_MAP[tool.group]?.let { ItemTools(tool, it.maxCount(1)) }
        }

        for (weapons in DataList.dataItemWeapons) {
            val mate = when (weapons.tool_meta) {
                "wood" -> ToolMaterials.WOOD
                "stone" -> ToolMaterials.STONE
                "iron" -> ToolMaterials.WOOD
                "gold" -> ToolMaterials.GOLD
                "diamond" -> ToolMaterials.DIAMOND

                else -> ToolMaterials.WOOD
            }

            ItemGroupEvents.modifyEntriesEvent(REGISTRY_MAP[weapons.group])
                .register{content: FabricItemGroupEntries -> content.add(DataList.getGroup[weapons.group]?.icon)}

            ITEM_SETTINGS_MAP[weapons.group]?.let { ItemWeapons(it.maxCount(1), weapons, mate) }
            //ItemWeapons(Item.Settings().group(DataList.getGroup[weapons.group]).maxCount(1), weapons, mate)
        }
    }

    private fun toolBlockProcessing() {
        if (DataList.dataBlock.size >= 1) {
            for (block in DataList.dataBlock) {

                BlockBase(FabricBlockSettings.create().strength(block.hard).mapColor(MapColor.STONE_GRAY), block)
            }
        }
    }

    private fun recipeDataProcessor() {
        for (recipe in DataList.dataRecipe) {
            CoreDataOutput(RecipeJsonHelper(), recipe)
        }
    }
}