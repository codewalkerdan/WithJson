package club.someoneice.ovo.util

import club.someoneice.ovo.data.BlockData
import club.someoneice.ovo.util.Util.findItemByText
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class BlockBase(settings: Settings, private val blockSet: BlockData): Block(settings) {
    init {
        this.register(blockSet.name, blockSet.group)
    }

    private fun getDroppedStacks(): MutableList<ItemStack> {
        val itemlist = ArrayList<ItemStack>()
        when(blockSet.drop_item) {
            "null" -> return itemlist
            "this" -> itemlist.add(ItemStack(this))
            else -> itemlist.add(ItemStack(findItemByText(blockSet.drop_item)))
        }

        return itemlist
    }

    fun getDroppedStacks(
        state: BlockState,
        world: ServerWorld?,
        pos: BlockPos?,
        blockEntity: BlockEntity?
    ): List<ItemStack> {
        return getDroppedStacks()
    }

    fun getDroppedStacks(
        state: BlockState,
        world: ServerWorld?,
        pos: BlockPos?,
        blockEntity: BlockEntity?,
        entity: Entity?,
        stack: ItemStack
    ): List<ItemStack> {
        return getDroppedStacks()
    }
}