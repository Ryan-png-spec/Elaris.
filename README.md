# Elaris — Android Core Rebuild

Nuovo nucleo Android/Kotlin di Elaris. Il vecchio prototipo HTML non è la fonte di verità.

- Core Kotlin isolato dalla UI
- 13 regni canonici e Ryan Shadow controllato dal giocatore
- RNG deterministico
- comandi validati e duplicati bloccati
- Cronache da eventi
- NPC, informazioni, diplomazia, economia, inventario
- combattimento bounded
- salvataggi versionati
- shell Android Start / Salva / Carica / primo combattimento

Il core è verificabile con kotlinc. L'ambiente corrente non dispone dell'Android SDK, quindi l'APK non è stato compilato qui.
