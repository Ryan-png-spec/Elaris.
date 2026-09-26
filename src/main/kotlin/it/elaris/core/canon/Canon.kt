package it.elaris.core.canon
import it.elaris.core.model.*
object ElarisCanon{
 val realmNames=listOf("Thalorien","Maraveth","Avernia","Eldoria","Nharak","Valoria","Kharad","Sylvaris","Draken","Lunareth","Veyria","Ashkar","Eryndor")
 fun createInitialWorld():WorldState{
  val realms=realmNames.associate{RealmId(it.lowercase()) to Realm(RealmId(it.lowercase()),it,null)}.toMutableMap()
  val ryan=CharacterId("ryan-shadow"); val thal=realms[RealmId("thalorien")]!!
  realms[thal.id]=thal.copy(ruler=ryan)
  return WorldState(GameTime(),realms,emptyMap(),mapOf(ryan to Character(ryan,"Ryan Shadow",thal.id,1,"NON_QUANTIFIABLE",true)),emptyMap(),emptyMap(),emptyMap(),emptyList(),emptySet(),emptySet())
 }
}
