package it.elaris.app
import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.elaris.core.combat.*
import it.elaris.core.random.SeededRandomEngine
class MainActivity:Activity(){override fun onCreate(b:Bundle?){super.onCreate(b);setContent{ElarisApp()}}}
@Composable private fun ElarisApp(){
 var started by remember{mutableStateOf(false)}
 var message by remember{mutableStateOf("")}
 var combat by remember{mutableStateOf<CombatResult?>(null)}
 MaterialTheme{Surface(Modifier.fillMaxSize()){Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("Elaris",style=MaterialTheme.typography.headlineMedium)
  if(!started){
   Button(onClick={started=true}){Text("Start")}
   Button(onClick={message="Nessun salvataggio disponibile.")}{Text("Carica partita")}
  }else{
   Text("Ryan Shadow");Text("Potere: non quantificabile")
   Button(onClick={message="Partita pronta per il salvataggio."}){Text("Salva partita")}
   Button(onClick={combat=CombatEngine().resolve(CombatInput(Combatant("Ryan Shadow",1000),Combatant("Predone",50)),SeededRandomEngine(1)))}){Text("Primo combattimento")}
   combat?.let{Text(if(it.winnerId!=null)"Vincitore: ${it.winnerId}" else "Combattimento terminato")}
  }
  if(message.isNotEmpty())Text(message)
 }}}
}
