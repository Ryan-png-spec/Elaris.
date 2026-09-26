package it.elaris.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.elaris.core.canon.ElarisCanon
import it.elaris.core.chronicles.ChronicleEngine
import it.elaris.core.combat.*
import it.elaris.core.model.*
import it.elaris.core.random.SeededRandomEngine

private enum class Screen { MENU, WORLD, CHARACTER, ADVENTURE, COMBAT, CHRONICLES, INVENTORY, SAVE }

class MainActivity : Activity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContent { ElarisApp() }
    }
}

@Composable
private fun ElarisApp() {
    var screen by remember { mutableStateOf(Screen.MENU) }
    var world by remember { mutableStateOf(ElarisCanon.createInitialWorld()) }
    var combat by remember { mutableStateOf<CombatResult?>(null) }
    var saveMessage by remember { mutableStateOf("") }

    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("ELARIS", style = MaterialTheme.typography.headlineMedium)
                Text("Mondo persistente • tempo ${world.time.tick}")

                when (screen) {
                    Screen.MENU -> {
                        Button(onClick = { screen = Screen.WORLD }) { Text("Start") }
                        OutlinedButton(onClick = { saveMessage = "Caricamento locale: sistema di salvataggio in integrazione." }) { Text("Carica partita") }
                        OutlinedButton(onClick = { screen = Screen.SAVE }) { Text("Salva partita") }
                        if (saveMessage.isNotEmpty()) Text(saveMessage)
                    }
                    Screen.WORLD -> {
                        SectionTitle("Mondo")
                        Text("I 13 regni canonici sono presenti nel nucleo del mondo.")
                        world.realms.values.forEach { realm ->
                            Text("• ${realm.name}" + if (realm.ruler != null) " — sovrano assegnato" else "")
                        }
                        NavButton("Personaggio") { screen = Screen.CHARACTER }
                        NavButton("Avventura") { screen = Screen.ADVENTURE }
                        NavButton("Cronache") { screen = Screen.CHRONICLES }
                        NavButton("Inventario") { screen = Screen.INVENTORY }
                        NavButton("Salvataggio") { screen = Screen.SAVE }
                        BackButton { screen = Screen.MENU }
                    }
                    Screen.CHARACTER -> {
                        SectionTitle("Personaggio")
                        val ryan = world.characters.values.first { it.isPlayer }
                        Text(ryan.name, style = MaterialTheme.typography.titleLarge)
                        Text("Livello: ${ryan.level}")
                        Text("Potere: ${ryan.powerLabel}")
                        Text("Regno: ${world.realms[ryan.realmId]?.name ?: "sconosciuto"}")
                        Text("Il giocatore controlla direttamente Ryan. Il mondo controlla gli altri personaggi.")
                        BackButton { screen = Screen.WORLD }
                    }
                    Screen.ADVENTURE -> {
                        SectionTitle("Avventura")
                        Text("Prima area giocabile: pattuglia nei territori di Thalorien.")
                        Text("Qui verranno collegati città, incontri, missioni e conseguenze persistenti.")
                        Button(onClick = { screen = Screen.COMBAT }) { Text("Affronta un predone") }
                        BackButton { screen = Screen.WORLD }
                    }
                    Screen.COMBAT -> {
                        SectionTitle("Combattimento")
                        Text("Ryan Shadow vs Predone")
                        Button(onClick = {
                            combat = CombatEngine().resolve(
                                CombatInput(Combatant("Ryan Shadow", 1000), Combatant("Predone", 50)),
                                SeededRandomEngine(world.time.tick + 1)
                            )
                        }) { Text("Combatti") }
                        combat?.let {
                            Text(if (it.winnerId != null) "Vincitore: ${it.winnerId}" else "Combattimento terminato")
                        }
                        Text("Il combattimento usa un percorso a terminazione garantita; nessun loop infinito.")
                        BackButton { screen = Screen.ADVENTURE }
                    }
                    Screen.CHRONICLES -> {
                        SectionTitle("Cronache")
                        val entries = ChronicleEngine.entriesFor(world)
                        if (entries.isEmpty()) Text("Nessun evento storico registrato.")
                        entries.forEach { Text("T${it.time.tick} — ${it.text}") }
                        BackButton { screen = Screen.WORLD }
                    }
                    Screen.INVENTORY -> {
                        SectionTitle("Inventario")
                        val ryan = world.characters.values.first { it.isPlayer }
                        val items = world.inventory[ryan.id].orEmpty()
                        if (items.isEmpty()) Text("Inventario vuoto.")
                        items.forEach { (id, quantity) -> Text("• ${id.value}: $quantity") }
                        Text("Le operazioni di inventario passano dal dominio e non dalla UI.")
                        BackButton { screen = Screen.WORLD }
                    }
                    Screen.SAVE -> {
                        SectionTitle("Salvataggio")
                        Text("Stato corrente: tick ${world.time.tick}")
                        Button(onClick = {
                            saveMessage = "Salvataggio locale persistente in integrazione."
                        }) { Text("Salva") }
                        Text(saveMessage)
                        BackButton { screen = Screen.MENU }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall)
}

@Composable
private fun NavButton(label: String, action: () -> Unit) {
    OutlinedButton(onClick = action, modifier = Modifier.fillMaxWidth()) { Text(label) }
}

@Composable
private fun BackButton(action: () -> Unit) {
    OutlinedButton(onClick = action, modifier = Modifier.fillMaxWidth()) { Text("Indietro") }
}
