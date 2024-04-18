# Anti-Monopoly Android App

Herzlich willkommen im offiziellen GitHub-Repository der Anti-Monopoly Android App. Dieses Projekt ist eine moderne Adaption des klassischen Brettspiels Monopoly.
Im Unterschied zum Original treten hier Monopolisten gegen Wettbewerber in einer dynamischen Spielumgebung an, wobei beide Gruppen unterschiedliche Ziele und Strategien verfolgen.

## Spielübersicht

In Anti-Monopoly treten zwei Gruppen gegeneinander an:

- **Monopolisten** streben danach, möglichst viele Immobilien zu monopolisieren und hohe Mieten zu erzwingen.
- **Wettbewerber** zielen darauf ab, faire Preise zu wahren und den Markt offen zu halten.

Das Ziel des Spiels ist es, durch geschickte finanzielle Entscheidungen und strategische Bewegungen auf dem Spielbrett die Gegner zu überwinden und als dominanter Spieler hervorzugehen.

## Technische Architektur

Die App ist als Android-Anwendung konzipiert und kommuniziert mit einem Backend, das auf einem Java Spring Boot Server läuft. Die Datenhaltung erfolgt in einem PostgreSQL-Container, während der Redis-Stack-Server als schneller, transienter Datenspeicher dient, um Spielzustände und Benutzersessions zu verwalten.

### Komponenten

- **Android App**: Die Benutzeroberfläche und Logik der Client-Anwendung.
- **Java Spring Boot Server**: Backend-Server, der die Spiellogik, Benutzerverwaltung und API-Endpunkte bereitstellt.
- **Redis-Stack-Server**: Verwaltet Session-Daten und Caching, um die Leistung zu optimieren.
- **PostgreSQL-Container**: Dauerhafte Speicherung von Benutzerdaten und Spielhistorie.

## Setup

Um das Projekt lokal zu starten, sind folgende Schritte erforderlich:

### Voraussetzungen

- Android Studio
- Docker
- JDK 11 oder höher
- Git

### Anweisungen

1. **Klonen des Repositories**

   ```bash
   git clone https://github.com/username/anti-monopoly.git
   cd anti-monopoly
   ```

2.**Starten der Backend-Services**

  ```bash
  docker-compose up -d
  ```


Dies startet die PostgreSQL- und Redis-Stack-Server im Hintergrund.

3. **Starten des Spring Boot Servers**

  ```bash
  cd backend
  ./mvnw spring-boot:run
  ```

4. **Starten der Android App**

  Öffne das Projekt in Android Studio und starte die App auf einem Emulator oder angeschlossenen Gerät.

API-Dokumentation

Eine detaillierte API-Dokumentation findet sich unter /docs/api.md. Diese enthält alle verfügbaren Endpunkte, ihre Parameter und Beispiele.
