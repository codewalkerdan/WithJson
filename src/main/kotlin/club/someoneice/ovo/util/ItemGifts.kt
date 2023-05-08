package club.someoneice.ovo.util

import club.someoneice.ovo.data.ItemGift
import club.someoneice.ovo.util.Util.findItemByText
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemGifts(settings: Settings, private val itemSet: ItemGift): Item(settings) {
    init {
        this.register(itemSet.name)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        stack.count--
        if (itemSet.items.size > 0) {
            for (itm in itemSet.items) {
                val getItem: Item? = findItemByText(itm.item.toString())
                (user as PlayerEntity).inventory.addPickBlock(ItemStack(getItem, itm.item_number))
            }
        }

        return super.finishUsing(stack, world, user)
    }
}