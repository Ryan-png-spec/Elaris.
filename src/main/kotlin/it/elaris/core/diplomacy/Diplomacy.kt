package it.elaris.core.diplomacy
import it.elaris.core.model.*
import it.elaris.core.random.RandomEngine
data class DiplomaticCommand(val a:FactionId,val b:FactionId,val trustDelta:Int=0,val hostilityDelta:Int=0,val treaty:Boolean=false)
sealed interface ValidationResult{data object Valid:ValidationResult;data class Invalid(val reason:String):ValidationResult}
object DiplomacyEngine{
 fun validate(c:DiplomaticCommand,s:WorldState)=if(c.a==c.b)ValidationResult.Invalid("Faction cannot negotiate with itself")else if(c.a !in s.factions||c.b !in s.factions)ValidationResult.Invalid("Unknown faction")else ValidationResult.Valid
 fun apply(c:DiplomaticCommand,s:WorldState,r:RandomEngine):WorldState{require(validate(c,s) is ValidationResult.Valid);val k=c.a to c.b;val old=s.relationships[k]?:Relationship(c.a,c.b);val rel=old.copy(trust=(old.trust+c.trustDelta).coerceIn(-100,100),hostility=(old.hostility+c.hostilityDelta).coerceIn(0,100));return s.copy(relationships=s.relationships+(k to rel))}
}
