package it.elaris.core.combat
import it.elaris.core.random.RandomEngine
data class Combatant(val id:String,val power:Long,val hp:Long=100)
data class CombatInput(val attacker:Combatant,val defender:Combatant,val maxRounds:Int=100)
data class CombatResult(val winnerId:String?,val rounds:Int,val timedOut:Boolean)
class CombatEngine{
 fun resolve(i:CombatInput,r:RandomEngine):CombatResult{
  require(i.maxRounds>0);var a=i.attacker.hp;var d=i.defender.hp;var rounds=0
  while(rounds<i.maxRounds&&a>0&&d>0){rounds++;d-=(i.attacker.power/10+r.nextInt(10)).coerceAtLeast(1);if(d<=0)break;a-=(i.defender.power/10+r.nextInt(10)).coerceAtLeast(1)}
  val w=when{a>0&&d<=0->i.attacker.id;d>0&&a<=0->i.defender.id;else->null}
  return CombatResult(w,rounds,w==null)
 }
}
