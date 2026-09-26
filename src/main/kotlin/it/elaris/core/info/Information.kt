package it.elaris.core.info
import it.elaris.core.model.*
enum class FactVisibility{UNKNOWN,KNOWN,RUMOR,FALSE}
data class KnowledgeState(val facts:Map<String,FactVisibility>)
object InformationEngine{fun visibleTo(c:CharacterId,s:WorldState)=KnowledgeState(s.events.associate{it.id.value to if(it.source==c)FactVisibility.KNOWN else FactVisibility.UNKNOWN})}
