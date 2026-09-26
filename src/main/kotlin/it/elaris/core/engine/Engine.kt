package it.elaris.core.engine
import it.elaris.core.model.*
import it.elaris.core.random.RandomEngine
sealed interface GameCommand{val id:CommandId
 data class AdvanceTime(override val id:CommandId,val ticks:Long):GameCommand
 data class ChangeCityStability(override val id:CommandId,val cityId:CityId,val delta:Int):GameCommand}
sealed interface CommandResult{data class Success(val state:WorldState,val event:WorldEvent?):CommandResult;data class Failure(val reason:String):CommandResult}
class WorldEngine{
 fun execute(state:WorldState,command:GameCommand,rng:RandomEngine):CommandResult{
  if(command.id in state.appliedCommands)return CommandResult.Failure("Duplicate command")
  if(command is GameCommand.AdvanceTime&&command.ticks<=0)return CommandResult.Failure("Ticks must be positive")
  val pair=when(command){
   is GameCommand.AdvanceTime->{\n    val next = state.copy(time=GameTime(state.time.tick+command.ticks),appliedCommands=state.appliedCommands+command.id)\n    val event = WorldEvent(EventId("cmd:"+command.id.value), next.time, "TIME_ADVANCED", null, emptySet(), mapOf("ticks" to command.ticks.toString()))\n    next.copy(events=next.events+event, appliedEvents=next.appliedEvents+event.id) to event\n   }
   is GameCommand.ChangeCityStability->{val c=state.cities[command.cityId]?:return CommandResult.Failure("Unknown city");val u=c.copy(stability=(c.stability+command.delta).coerceIn(0,100));state.copy(cities=state.cities+(c.id to u),appliedCommands=state.appliedCommands+command.id) to WorldEvent(EventId("cmd:"+command.id.value),state.time,"CITY_STABILITY_CHANGED",null,setOf(c.id.value),mapOf("delta" to command.delta.toString()))}
  }
  val(n,e)=pair
  return CommandResult.Success(if(e==null)n else n.copy(events=n.events+e,appliedEvents=n.appliedEvents+e.id),e)
 }
}
