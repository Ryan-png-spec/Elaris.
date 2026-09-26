package it.elaris.core.chronicles
import it.elaris.core.model.*
data class ChronicleEntry(val eventId:EventId,val time:GameTime,val text:String)
object ChronicleEngine{
 fun entriesFor(s:WorldState)=s.events.map{e->ChronicleEntry(e.id,e.time,when(e.type){"CITY_STABILITY_CHANGED"->"CITY_STABILITY_CHANGED" else->e.type})}
}
