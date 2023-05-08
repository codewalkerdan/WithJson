package club.someoneice.ovo.data

import club.someoneice.ovo.IDataGem


data class ItemTool(
    val name: String,
    val attackDamage: Int,
    val toolkit: String,
    val tool_meta: String       = "wood",
    val group: String           = "null"
): IDataGem
