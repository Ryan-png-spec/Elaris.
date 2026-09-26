package it.elaris.core.economy
import it.elaris.core.model.*
import it.elaris.core.random.RandomEngine
data class MarketState(val stock:Long,val price:Int)
data class EconomyResult(val state:WorldState,val markets:Map<CityId,MarketState>)
object EconomyEngine{
 fun tick(s:WorldState,ticks:Long,r:RandomEngine):EconomyResult{require(ticks>=0);val m=s.cities.mapValues{(_,c)->MarketState(c.population/1000+ticks,(1000.0/c.population.coerceAtLeast(1)*1000).toInt().coerceIn(1,100))};return EconomyResult(s,m)}
}
