# ManuAI 🤖

Ein autonomer KI-Survival-Mitspieler fuer Minecraft Java 26.2 (Fabric).

**Status: v0.1.0 - Fundament.** Diese Version bringt ManuAI als echten Mob
ins Spiel, mit einem Task-System und einer einfachen deutschen
Sprachsteuerung ("hol 5 stacks eichenholz"). Navigation, Mining, das
Kistenlager-System mit Item Frames usw. sind als naechste Schritte geplant
(siehe Roadmap unten) - das war mit Claude so abgesprochen, weil das
Gesamtprojekt (60 Abschnitte im Lastenheft) realistisch ein Wochen-Projekt
ist, kein Ein-Schritt-Task.

## Warum ist das noch nicht "die fertige JAR"?

Claude entwickelt dieses Projekt in einer Sandbox ohne Zugriff auf
`maven.fabricmc.net` bzw. Mojangs Server - genau die Server, von denen
Gradle/Loom die Minecraft-Bibliotheken, den Fabric Loader und die
Fabric API herunterladen muss. Deshalb kann der Build hier nicht
tatsaechlich ausgefuehrt werden. Du baust lokal (dort funktioniert der
Download normal), und wir fixen Fehler gemeinsam anhand deiner Logs.

## Voraussetzungen

- **Java 25** (Minecraft 26.2 braucht das - `java -version` pruefen)
- **Minecraft Java Edition 26.2** (ueber die Modrinth App oder den
  offiziellen Launcher)
- Internetzugang beim ersten Build (Gradle laedt automatisch Minecraft,
  Fabric Loader, Fabric API und Mappings herunter)

## Bauen

```bash
# Windows
gradlew.bat build

# macOS/Linux
./gradlew build
```

Das fertige Mod-JAR landet danach in `build/libs/manuai-0.1.0.jar`
(nicht die `-sources.jar` oder `-dev.jar` verwenden).

Falls beim ersten Lauf lange nichts passiert: Gradle laedt im Hintergrund
Minecraft + Mappings herunter, das kann ein paar Minuten dauern.

## Installation

1. [Fabric Loader fuer 26.2](https://fabricmc.net/use/) installieren
   (Modrinth App macht das automatisch, wenn du dort ein 26.2-Fabric-Profil
   anlegst).
2. [Fabric API](https://modrinth.com/mod/fabric-api) fuer 26.2 herunterladen
   und in den `mods`-Ordner deines Profils legen.
3. `manuai-0.1.0.jar` ebenfalls in den `mods`-Ordner legen.
4. Minecraft mit dem Fabric-26.2-Profil starten.

## Befehle (v0.1.0)

| Befehl | Wirkung |
|---|---|
| `/manuai help` | Zeigt alle Befehle |
| `/manuai spawn` | Spawnt ManuAI neben dir |
| `/manuai task <Anweisung>` | z.B. `/manuai task hol 5 stacks eichenholz` |
| `/manuai status` | Zeigt offene Aufgaben und Fortschritt |
| `/manuai stop` | Bricht die aktuelle Aufgabe ab |
| `/manuai pause` / `/manuai resume` | Pausiert/setzt die Aufgabenliste fort |
| `/manuai scanstorage` | Noch nicht implementiert (Platzhalter) |
| `/manuai storage` | Noch nicht implementiert (Platzhalter) |

Die Sprachsteuerung versteht aktuell: Eichenholz/Fichtenholz/Birkenholz/Holz,
Setzlinge, Cobblestone/Stein, Eisen, Kohle, Gold, Diamant, Redstone, Lapis -
mit oder ohne "X Stacks" davor (siehe `ResourceAliases.java`, leicht
erweiterbar).

**Wichtig:** ManuAI sammelt aktuell noch nichts wirklich - Tasks werden
angenommen und im Task-System verwaltet, aber die Ausfuehrung (loslaufen,
Baum faellen, Holz einsammeln) ist der naechste Meilenstein.

## Projektstruktur

```
src/main/java/dev/manuai/
  ManuAI.java              - Mod-Einstiegspunkt
  task/                    - Task, TaskState, TaskManager
  nlp/                     - deutsche Sprachsteuerung (regelbasiert)
  entity/                  - ManuAIEntity (der Mob), Registrierung
  command/                 - /manuai Befehle
  storage/                 - (leer, naechster Schritt)
src/client/java/dev/manuai/client/
  ManuAIClient.java        - Client-Einstiegspunkt
  entity/                  - Model, Renderer, RenderState fuer ManuAI
```

## Roadmap (naechste Schritte, nach den Prioritaeten aus dem Lastenheft)

- [x] Priority 1-2: Mod startet auf 26.2, ManuAI existiert als Mob
- [x] Priority 3 (Basis): Task-System + einfache Sprachsteuerung
- [ ] Priority 4: echtes Pathfinding zu Ressourcen/Lager (Navigation-Goal)
- [ ] Priority 5: Mining (Baeume faellen, Erz abbauen)
- [ ] Priority 6: Inventarverwaltung, "Inventar voll -> zurueck zum Lager"
- [ ] Priority 7: Kistenlager mit Item-Frame-Erkennung (`/manuai scanstorage`)
- [ ] Priority 8: Crafting (Werkzeuge selbst herstellen)
- [ ] Priority 9: Nachhaltiges Holzsystem (Setzlinge sammeln & pflanzen)
- [ ] Priority 10: Survival-/Gefahren-System (Mobs, Nacht, Hunger)
- [ ] Priority 11: Datenspeicherung (SavedData, ueberlebt Weltneustart)
- [ ] Priority 12: GUI/HUD, Fortschrittsanzeige

## Wenn der Build fehlschlaegt

Schick mir einfach die komplette Fehlermeldung aus der Konsole (nicht nur
die letzte Zeile) - dann schauen wir uns das zusammen an. Haeufige
Ursachen: falsche Java-Version installiert, oder Firewall/Antivirus
blockiert den Download von `maven.fabricmc.net`.
