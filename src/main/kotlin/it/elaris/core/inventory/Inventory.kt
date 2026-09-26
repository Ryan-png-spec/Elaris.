package it.elaris.core.inventory
import it.elaris.core.model.ItemId
data class InventoryState(val items:Map<ItemId,Int> = emptyMap())
sealed interface InventoryResult{data class Success(val state:InventoryState):InventoryResult;data class Failure(val reason:String):InventoryResult}
object InventoryEngine{
 fun add(s:InventoryState,id:ItemId,q:Int):InventoryResult=if(q<=0)InventoryResult.Failure("Quantity must be positive")else InventoryResult.Success(s.copy(items=s.items+(id to ((s.items[id]?:0)+q))))
 fun remove(s:InventoryState,id:ItemId,q:Int):InventoryResult{if(q<=0)return InventoryResult.Failure("Quantity must be positive");val n=s.items[id]?:0;if(n<q)return InventoryResult.Failure("Insufficient quantity");val m=s.items.toMutableMap();if(n==q)m.remove(id)else m[id]=n-q;return InventoryResult.Success(InventoryState(m))}
}
