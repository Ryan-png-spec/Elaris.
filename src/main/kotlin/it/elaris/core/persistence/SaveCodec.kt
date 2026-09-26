package it.elaris.core.persistence

import it.elaris.core.model.*
import java.util.Base64

object SaveCodec {
    private const val VERSION = 1
    private fun e(s: String) = Base64.getEncoder().encodeToString(s.toByteArray(Charsets.UTF_8))
    private fun d(s: String) = String(Base64.getDecoder().decode(s), Charsets.UTF_8)
    private fun line(tag: String, vararg values: String) = tag + "\t" + values.joinToString("\t") { e(it) }

    fun encode(s: WorldState): String = buildString {
        appendLine("ELARIS_SAVE\t$VERSION")
        appendLine(line("TIME", s.time.tick.toString()))
        s.realms.values.sortedBy { it.id.value }.forEach { appendLine(line("REALM", it.id.value, it.name, it.ruler?.value ?: "")) }
        s.cities.values.sortedBy { it.id.value }.forEach { appendLine(line("CITY", it.id.value, it.realmId.value, it.name, it.population.toString(), it.stability.toString(), it.prosperity.toString())) }
        s.characters.values.sortedBy { it.id.value }.forEach { appendLine(line("CHAR", it.id.value, it.name, it.realmId?.value ?: "", it.level.toString(), it.powerLabel, it.isPlayer.toString())) }
        s.factions.values.sortedBy { it.id.value }.forEach { appendLine(line("FACTION", it.id.value, it.name)) }
        s.relationships.values.sortedWith(compareBy({it.a.value},{it.b.value})).forEach { appendLine(line("REL", it.a.value,it.b.value,it.trust.toString(),it.hostility.toString())) }
        s.inventory.toSortedMap(compareBy { it.value }).forEach { (cid, items) ->
            items.toSortedMap(compareBy { it.value }).forEach { (id,q) -> appendLine(line("ITEM",cid.value,id.value,q.toString())) }
        }
        s.events.forEach { ev ->
            appendLine(line("EVENT",ev.id.value,ev.time.tick.toString(),ev.type,ev.source?.value ?: "",ev.affected.joinToString(","),ev.payload.entries.joinToString(";") { "${it.key}=${it.value}" }))
        }
        s.appliedCommands.sortedBy { it.value }.forEach { appendLine(line("CMD",it.value)) }
        s.appliedEvents.sortedBy { it.value }.forEach { appendLine(line("APPEVENT",it.value)) }
    }

    fun decode(raw: String): WorldState {
        val rows = raw.lineSequence().filter { it.isNotBlank() }.toList()
        require(rows.firstOrNull()?.startsWith("ELARIS_SAVE\t$VERSION") == true) { "Unsupported or invalid save format" }
        var time = GameTime()
        val realms = mutableMapOf<RealmId,Realm>(); val cities=mutableMapOf<CityId,City>()
        val chars=mutableMapOf<CharacterId,Character>(); val factions=mutableMapOf<FactionId,Faction>()
        val rels=mutableMapOf<Pair<FactionId,FactionId>,Relationship>(); val inv=mutableMapOf<CharacterId,MutableMap<ItemId,Int>>()
        val events=mutableListOf<WorldEvent>(); val commands=mutableSetOf<CommandId>(); val applied=mutableSetOf<EventId>()
        fun vals(row:String)=row.split("\t").drop(1).map(::d)
        rows.drop(1).forEach { row ->
            val v=vals(row); when(row.substringBefore("\t")) {
                "TIME" -> time=GameTime(v[0].toLong())
                "REALM" -> { val id=RealmId(v[0]); realms[id]=Realm(id,v[1],v[2].takeIf{it.isNotEmpty()}?.let(::CharacterId)) }
                "CITY" -> { val id=CityId(v[0]); cities[id]=City(id,RealmId(v[1]),v[2],v[3].toLong(),v[4].toInt(),v[5].toInt()) }
                "CHAR" -> { val id=CharacterId(v[0]); chars[id]=Character(id,v[1],v[2].takeIf{it.isNotEmpty()}?.let(::RealmId),v[3].toInt(),v[4],v[5].toBoolean()) }
                "FACTION" -> { val id=FactionId(v[0]); factions[id]=Faction(id,v[1]) }
                "REL" -> { val a=FactionId(v[0]); val b=FactionId(v[1]); rels[a to b]=Relationship(a,b,v[2].toInt(),v[3].toInt()) }
                "ITEM" -> inv.getOrPut(CharacterId(v[0])){mutableMapOf()}[ItemId(v[1])]=v[2].toInt()
                "EVENT" -> { val affected=if(v[4].isBlank()) emptySet() else v[4].split(",").toSet(); val payload=if(v[5].isBlank()) emptyMap() else v[5].split(";").associate { it.substringBefore("=") to it.substringAfter("=") }; events+=WorldEvent(EventId(v[0]),GameTime(v[1].toLong()),v[2],v[3].takeIf{it.isNotEmpty()}?.let(::CharacterId),affected,payload) }
                "CMD" -> commands+=CommandId(v[0])
                "APPEVENT" -> applied+=EventId(v[0])
            }
        }
        return WorldState(time,realms,cities,chars,factions,rels,inv.mapValues{it.value.toMap()},events,commands,applied)
    }
}
