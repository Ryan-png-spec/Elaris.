package it.elaris.core.random
data class RandomState(val state:Long)
interface RandomEngine{fun nextInt(bound:Int):Int;fun nextLong():Long;fun snapshot():RandomState}
class SeededRandomEngine(seed:Long):RandomEngine{
 private var state=seed
 override fun nextLong():Long{state=state*6364136223846793005L+1442695040888963407L;return state}
 override fun nextInt(bound:Int):Int{require(bound>0);return ((nextLong() ushr 1)%bound).toInt()}
 override fun snapshot()=RandomState(state)
 companion object{fun restore(s:RandomState)=SeededRandomEngine(s.state)}
}
