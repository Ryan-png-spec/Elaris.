package it.elaris.core.characters
import it.elaris.core.model.*
import it.elaris.core.random.RandomEngine
data class Goal(val id:String,val priority:Int)
data class Memory(val eventId:EventId,val summary:String)
sealed interface NpcDecision{data class Rest(val npc:CharacterId):NpcDecision;data class Improve(val npc:CharacterId):NpcDecision}
class NpcDecisionEngine{
 fun decide(npc:CharacterId,s:WorldState,g:List<Goal>,r:RandomEngine):NpcDecision{require(s.characters[npc]!=null);val x=g.maxByOrNull{it.priority};return if(x!=null&&x.priority>50&&r.nextInt(100)<80)NpcDecision.Improve(npc)else NpcDecision.Rest(npc)}
 fun remember(s:WorldState,e:WorldEvent)=listOf(Memory(e.id,e.type))
}
