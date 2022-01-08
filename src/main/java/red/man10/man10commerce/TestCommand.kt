package red.man10.man10commerce

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import red.man10.man10commerce.Man10Commerce.Companion.debug
import red.man10.man10commerce.Man10Commerce.Companion.es
import red.man10.man10commerce.data.ItemData

class TestCommand :CommandExecutor{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (label!="amzntest"){return true}

        if (sender!is Player)return true

        if (args.isEmpty()){

            Utility.sendMsg(sender,"amzntest sell <amount> : 手持ちアイテムを指定個数売るテスト")
            Utility.sendMsg(sender,"amzntest sell2 <amount> <multi> : 並列で手持ちアイテムを指定個数売るテスト")
            Utility.sendMsg(sender,"amzntest buy id(primary keyの最終値) itemID  : 手持ちアイテムをidの値まで買うテスト")
            Utility.sendMsg(sender,"amzntest buy2 id(primary keyの最終値) <multi> itemID  : 並列で手持ちアイテムをidの値まで買うテスト")
            Utility.sendMsg(sender,"amzntest debugoff  : デバッグモードを終了")

            return true
        }

        when(args[0]){

            "sell" ->{
                if (!sender.hasPermission(Man10Commerce.OP))return false

                val amount = args[1].toInt()

                debug = true

                val item = sender.inventory.itemInMainHand

                es.execute {
                    testSell(sender, amount, item)
                    debug = false
                }

            }

            "sell2" ->{

                if (!sender.hasPermission(Man10Commerce.OP))return false

                val amount = args[1].toInt()
                val multi = args[2].toInt()

                debug = true
                val item = sender.inventory.itemInMainHand
                for (i in 0 until multi){
                    es.execute{testSell(sender, amount, item)}
                }


            }

            "buy" ->{
                if (!sender.hasPermission(Man10Commerce.OP))return false

                if (args.size<3){
                    Utility.sendMsg(sender, "/amzn testbuy idKey itemID")
                    return true
                }

                val key = args[1].toInt()
                val itemID = args[2].toInt()

                debug = true

                Bukkit.getLogger().info("StartTestBuy")

                es.execute {testBuy(sender, key, itemID)}
            }

            "buy2" ->{
                if (!sender.hasPermission(Man10Commerce.OP))return false

                if (args.size<4){
                    Utility.sendMsg(sender, "/amzn buy2 idKey itemID thread")
                    return true
                }

                val key = args[1].toInt()
                val itemID = args[2].toInt()
                val multi = args[3].toInt()

                debug = true
                Bukkit.getLogger().info("StartTestBuy")

                for (i in 0 until multi){
                    es.execute {testBuy(sender, key, itemID)}
                }

            }

            "debugoff" ->{ debug = false }
        }

        return true
    }


    private fun testSell(sender:Player, amount:Int, item:ItemStack){
        val price = 10.0

        Bukkit.getLogger().info("StartTestSell:${amount}")

        for (i in 0 .. amount){

            if (!ItemData.sell(sender,item,price))continue

            Utility.sendMsg(sender, "§e§l出品成功しました！")

            Bukkit.getLogger().info("TestFinish:${i}")
        }

        Bukkit.getLogger().info("FinishedTestSell")

    }

    private fun testBuy(sender:Player, key:Int,itemID:Int){
        for (i in 0 .. key){
            ItemData.buy(sender, itemID, i) { code: Int ->
                Bukkit.getLogger().info("TestFinish:${i} code:${code}")
                if ((i-1) == key) {
                    Bukkit.getLogger().info("FinishedTestBuy")
                    debug = false
                }
            }

        }

    }
}