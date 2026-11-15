# Week 12 Evidence

Fill this in as you go. Parts 1–3 and Part 5 are graded on the **measurement**, not on the code.

**Every counting command here pipes through `scripts/codeonly.py`.** The reason is in that
file's header: a bare grep for `ctx.syncState()` finds the javadoc that *mentions*
`ctx.syncState()`. Grep the code, not the commentary.

---

## 0. The Sprint 8 carry-over (US-8.5)

```bash
grep -rn 'MONSTER_SATE_CHANGED' src/main/java | wc -l
```

**How many files share the misspelling?** ____3__

**It works perfectly. In one sentence: what does it cost, and when?**


**Now the other one.** Before changing anything, write a test asserting
`EnchantmentRegistry.isKnown("FLAMING")`. Run it.

**Paste the failure.**

```

```

**One sentence: 137 tests passed and this did not work. What were those 137 tests about?**


---

## 1. BEFORE — count the listeners

```bash
ls src/main/java/dungeonforge/events/*.java | xargs grep -l 'implements GameEventListener' | wc -l
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'bus.subscribe('
```

**Listener classes in `events/`:** ___4___  **`bus.subscribe` calls in `Main`:** ___4___

**Those two numbers are equal and that is not reassuring. Why not?** *(Look at what the fourth
subscription actually subscribes, and where that class lives.)*


**Which listener class is never subscribed?** __DamageMeter____

**Open it. What does it do, and when was it written?**


**Now the sentence. Put both counts and the consequence in one sentence.** *(This is the most
important sentence you will write this week. Two points.)*

> There are 4 listener classes in the events folder and 4 subscription calls in Main, 
> but one of those calls subscribes ConsolePrinter, so DamageMeter is still left out.

**Nobody broke it. In one sentence: what kind of mistake is this, if it is not a bug?**


---

## 2. BEFORE — the two games

```bash
printf 'north\nlook\nnorth\nlook\n' > demo.txt
mvn -q exec:java -Dexec.args="--script=demo.txt"
```

**Paste the scripted output:**

```
$ printf 'north\nlook\nnorth\nlook\n' > demo.txt
mvn -q exec:java -Dexec.args="--script=demo.txt"
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.12.0   seed 20260818

  Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' rewinds a turn.
  [L1R0] Level 1: Crypt
  "Burial niches line the walls. Most are empty. Most."
  Exits: north

[script] > north
  You go north.
  Ghoul is now hunting.

[script] > look
  [L1R1] Level 1: Crypt
  "Damp stone. Something drips in the dark, patiently."
  Hostile: Ghoul (18/18 HP, ATK 2, hunting)
  Iron-bound Chest:
    - Vial of Still Water [heals 21] (0.4kg, 25g)
    - Vampiric Bone Shortsword [dmg 6] [drain 1] (2.0kg, 140g)
    - Grave Shroud [def 3] (3.0kg, 43g)
  Exits: south, north

[script] > north
  You go north.

[script] > look
  [L1R2] Level 1: Crypt
  "Damp stone. Something drips in the dark, patiently."
  Exits: south, north

  Delver  HP 80/80  ATK 10  DEF 3  Gold 0  XP 0  Carry 60.0kg  (unarmed)
  Actions this session: 4   |   undo stack depth: 2
```

Now run `mvn -q exec:java` and type the same four commands by hand.

**Paste that output:**

```
$ mvn -q exec:java
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.12.0   seed 20260818

  Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' rewinds a turn.
  [L1R0] Level 1: Crypt
  "Burial niches line the walls. Most are empty. Most."
  Exits: north

[Exploring | HP 80/80] > north

  You go north.
  Ghoul is now hunting.
  Combat! 1 hostile(s). Try 'attack'.

[Combat | HP 80/80] > look

  [L1R1] Level 1: Crypt
  "Damp stone. Something drips in the dark, patiently."
  Hostile: Ghoul (18/18 HP, ATK 2, hunting)
  Iron-bound Chest:
    - Vial of Still Water [heals 21] (0.4kg, 25g)
    - Vampiric Bone Shortsword [dmg 6] [drain 1] (2.0kg, 140g)
    - Grave Shroud [def 3] (3.0kg, 43g)
  Exits: south, north

[Combat | HP 80/80] > north

  You cannot do that in Combat mode. 
    Available here: attack, look, use, equip, inventory, status, history, undo, help, quit

[Combat | HP 80/80] > look

  [L1R1] Level 1: Crypt
  "Damp stone. Something drips in the dark, patiently."
  Hostile: Ghoul (18/18 HP, ATK 2, hunting)
  Iron-bound Chest:
    - Vial of Still Water [heals 21] (0.4kg, 25g)
    - Vampiric Bone Shortsword [dmg 6] [drain 1] (2.0kg, 140g)
    - Grave Shroud [def 3] (3.0kg, 43g)
  Exits: south, north

[Combat | HP 80/80] > quit

  You lay down your pack.
  The room falls quiet.
  === YOU leave the dungeon behind === 'quests' for your record, or 'quit'.

  Delver  HP 80/80  ATK 10  DEF 3  Gold 0  XP 0  Carry 60.0kg  (unarmed)
  Actions this session: 5   |   undo stack depth: 1
```

**They differ. Name the file, the method and the line that is the difference.** ______

**Put the two turn sequences side by side:**

| | the turn |
|---|---|
| `gameLoop()` | |
| `runScript()` | |

**What can a script do that a player cannot?**


**`runScript`'s javadoc has been promising something since Week 7. Quote it, and say whether it
is true.**


---

## 3. BEFORE — the counting

```bash
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'ctx.syncState()'
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'history.push('
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'DamageMeter'
awk '/public static void main/{on=1} on{n++} on&&/^    }$/&&n>3{print n; exit}' src/main/java/dungeonforge/Main.java
```

| | Before | After |
|---|--------|-------|
| `main()` lines | 191    | 127   |
| `ctx.syncState()` in `Main` | 3      | 0     |
| copies of the turn sequence | 3      | 0     |
| `DamageMeter` mentioned outside its own file | 0      | 1     |
| listener classes subscribed, of 4 | 3      | 4     |
| monster blueprints available | 19     | 29    |
| a script can walk past a live hostile | yes    | no    |

*(Fill the After column at the end of the sprint. **Nothing in it may be a guess.**)*

---

## 4. AFTER — the two receipts

### US-9.1, the Adapter

```bash
git diff --stat HEAD~1          # on the us-9.1 commit
diff src/main/java/dungeonforge/legacy/LegacyBestiary.java  <the file you were given>
diff src/main/java/dungeonforge/factory/MonsterFactory.java <the file you started with>
python3 scripts/codeonly.py src/main/java/dungeonforge/factory/MonsterFactory.java \
  | grep -ciE 'bestiary|csv|MOB_ID|HIT_DICE'
```

**Paste all four.**

```
$ git diff --stat HEAD~1          # on the us-9.1 commit
diff src/main/java/dungeonforge/legacy/LegacyBestiary.java  <the file you were given>
diff src/main/ja![img.png](img.png)va/dungeonforge/factory/MonsterFactory.java <the file you started with>
python3 scripts/codeonly.py src/main/java/dungeonforge/factory/MonsterFactory.java \
  | grep -ciE 'bestiary|csv|MOB_ID|HIT_DICE'
 .gitignore                                         |   1 +
 docs/adapter-facade-clinic.md                      | 145 +++++++
 docs/evidence.week10.md                            |  14 +-
 docs/evidence.week12.md                            | 312 ++++++++++++++
 docs/sprint-09-plan.md                             | 449 +++++++++++++++++++++
 docs/template-method-clinic.md                     |  12 +-
 pom.xml                                            |   4 +-
 scripts/codeonly.py                                |  21 +
 src/main/java/dungeonforge/Main.java               |  29 +-
 .../dungeonforge/commands/AbstractCommand.java     |   5 +-
 src/main/java/dungeonforge/core/Combat.java        |   2 +-
 src/main/java/dungeonforge/events/CombatLog.java   |   2 +-
 src/main/java/dungeonforge/events/EventType.java   |   2 +-
 .../java/dungeonforge/factory/MonsterSource.java   |   9 +
 .../dungeonforge/items/EnchantmentRegistry.java    |   8 +-
 .../java/dungeonforge/legacy/LegacyBestiary.java   |  89 ++++
 .../dungeonforge/legacy/LegacyBestiaryAdapter.java | 108 +++++
 src/main/resources/data/bestiary.csv               |  24 ++
 src/test/java/dungeonforge/AdapterFacadeTest.java  | 205 ++++++++++
 19 files changed, 1416 insertions(+), 25 deletions(-)
```

**Two of those commands are the deliverable. Which two, and why?**

Both diff commands cause they don't produce no output at all.

**`blueprintCount()` before:** ___19___ **after:** ___29___

### US-9.2, the Facade

```bash
git diff --stat HEAD~1          # on the us-9.2 commit
```

**Paste it.**

```
$ git diff --stat HEAD~1 
 docs/evidence.week12.md                            |  29 ++-
 src/main/java/dungeonforge/Main.java               | 115 +++------
 src/main/java/dungeonforge/core/GameWorld.java     |  10 +-
 .../java/dungeonforge/factory/MonsterSource.java   |   9 +
 .../dungeonforge/legacy/LegacyBestiaryAdapter.java | 108 ++++++++
 .../java/dungeonforge/session/GameSession.java     | 153 +++++++++++
 src/test/java/dungeonforge/AdapterFacadeTest.java  | 280 +++++++++++++++++++++
 7 files changed, 615 insertions(+), 89 deletions(-)
```

**One sentence each on what the two numbers mean.** *(They mean different things. One is about a
format; one is about an order.)*


---

## 5. AFTER — what you did not have to change

**List every file that learned a CSV exists.** ___LegacyBestiary.java 
 LegacyBestiaryAdapter.java
GameSession.java___

**Is `MonsterFactory` one of them?** ___no___

**`ThemeKit` picks which monsters appear in a room. How many lines of it did you change to make
ten new monsters appear?** __0___

**In two or three sentences: explain how a class can use a blueprint that arrived from a file
format it has never heard of.**

LegacyBestiaryAdapter converts the CSV records into MonsterDef objects and registers them with MonsterFactory. 
ThemeKit uses those same MonsterDef blueprints, so it does not need to know about CSV files or how the data was originally stored.
---

## 6. AFTER — the honest accounting

### The Adapter's honest half

**`bestiary.csv` has no xp column. Which of the three options did you take?** _I derived an XP value from HP and attack
and documented that decision.

**Where in your code does a reader find out that the number is derived?** ___In LegacyBestiaryAdapter.experienceFor(),
which adds a note that XP was derived because the export has no XP column. Those notes are available through translationNotes().___

**One sentence: what would have gone wrong if you had silently defaulted it to 5?**
Someone reading the blueprint could mistake 5 for an actual value from the CSV instead of a decision I made.

**`SAPPER` has no counterpart. What did you do, and what happens when the eleventh unknown AI
name arrives next semester?**
I mapped SAPPER to the aggressive strategy and recorded a translation note. Any future unknown AI name uses that
same fallback and produces a note so the unsupported value is visible.

### The Facade's honest half

**Complete this honestly:**

> `GameSession` now knows about eight collaborators and the order they go together in, which
> means the coupling ______GameSession now knows about eight collaborators and the order they go together in, which
> means the coupling moved from Main into GameSession instead of disappearing.__

**Name one thing that got HARDER because `Main` no longer knows those eight classes.** *(There
is a real answer. Think about what a test needs.)*

Setting up an isolated test got harder because GameSession.start() builds the whole session, even when a test 
only needs a player and an event bus.
---

## 7. Retro line

**One sentence on something each pattern gave you, and one on something each one took away.**
Four sentences, and the two costs are not the same cost.

| Pattern | Gave | Took |
|---|---|---|
| Adapter | Adapter let me use the old bestiary without changing MonsterFactory.| I had to decide how to handle missing XP and unsupported AI values.|
| Facade |Facade gave me one place to assemble the game and run each turn. |Setting up smaller tests became harder because start() builds the entire session. |

**And the one-sentence distinction, in your own words, having now written both:** Adapter and
Facade are both "a class in front of other classes." What made them different in practice?

Adapter translated the old bestiary into an interface the game could use, while Facade brought the game’s setup and 
turn sequence together behind one entry point.
