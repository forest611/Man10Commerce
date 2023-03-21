package red.man10.man10commerce.menu

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import red.man10.man10commerce.Man10Commerce
import red.man10.man10commerce.data.ItemDataOld
import red.man10.man10commerce.sort.Sort

class MaterialMenu(p:Player,private val material: Material) : ListMenuOld("§l同じ種類のリスト",p) {
    override fun open() {
        val keys = Sort.materialSort(material, ItemDataOld.orderMap.keys().toList())

        listInventory(keys)

        p.openInventory(menu)

        pushStack()
    }

    override fun click(e: InventoryClickEvent, menu: MenuOld, id: String, item: ItemStack) {
        val meta = item.itemMeta!!

        val orderID = meta.persistentDataContainer[NamespacedKey(Man10Commerce.plugin,"order_id"), PersistentDataType.INTEGER]?:-1
        val itemID = meta.persistentDataContainer[NamespacedKey(Man10Commerce.plugin,"item_id"), PersistentDataType.INTEGER]?:-1

        if (orderID == -1)return

        if (e.action != InventoryAction.MOVE_TO_OTHER_INVENTORY){
            Man10Commerce.es.execute { OneItemList(p,itemID).open() }
            return
        }

        ItemDataOld.buy(p,itemID,orderID){
            Bukkit.getScheduler().runTask(Man10Commerce.plugin, Runnable {menu.open()})
        }

    }
}