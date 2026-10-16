# Week 8 Evidence

## 1. BEFORE — the two problems

### Problem A: mode checks scattered across commands

```bash
grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
```

**Paste it. How many command classes re-implement the same check?**

```Bash
joser@Red_Phoenix MINGW64 ~/IdeaProjects/dungeonforge (week-08-state)
$ grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
src/main/java/dungeonforge/commands/CommandParser.java:172:        if (room.hasLivingMonsters()) {
src/main/java/dungeonforge/commands/DropCommand.java:21:        if (room().hasLivingMonsters()) {
src/main/java/dungeonforge/commands/MoveCommand.java:19:        if (room().hasLivingMonsters()) {
src/main/java/dungeonforge/commands/TakeCommand.java:13:        if (room().hasLivingMonsters()) {

```

**Play a turn: start a fight, then type `north`. Paste what happens:**

```Bash
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.7.0   seed 20260818

  Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' rewinds a turn.
  [L1R0] Level 1: Crypt
  "Burial niches line the walls. Most are empty. Most."
  Exits: north

[HP 80/80] > north

  You go north.
  ACHIEVEMENT: Cornered -- Force an enemy to change tactics
  Ghoul changes tactics: idle -> hunting.
  Ghoul hits you for 2.

[HP 78/80] > attack

  You hit Ghoul for 10.
  Ghoul changes tactics: hunting -> enraged.
  Ghoul hits you for 3.

[HP 75/80] > north

  You cannot walk away with enemies still standing.

[HP 75/80] >  
```

**Now type `help` during that fight. Does it list `north`?** __Yes__
**Should it?**

It can go both ways I guess, since we are still in a fight we shouldn't be able to go north 
unless we are in a large room inside the room to move around in and not stuck in a fix position. Sort of like
Legend of Zelda, Link can move (N,S,E,or W) in a level if there's a path to avoid a monster if he has taken too 
much damage.


### Problem B: the monster lifecycle as a string plus conditionals

```bash
grep -n "getStateName" src/main/java/dungeonforge/core/Combat.java
```

**Paste it. Which three methods branch on the state name?**

```Bash
joser@Red_Phoenix MINGW64 ~/IdeaProjects/dungeonforge (week-08-state)
$ grep -n "getStateName" src/main/java/dungeonforge/core/Combat.java
65:        String current = m.getStateName();
109:        String state = m.getStateName();
147:        if (m.getStateName().equals("enraged")) {
```

**Answer before you refactor:**

1. To add a "stunned" state, which methods must you edit?

the methods that handle state transitions, decide whether a monster can act, and calculate state-dependent damage.

2. Reading `updateMonsterState()`, which states can follow `enraged`? How long did it take you
   to be sure?

An enraged monster becomes fleeing if its HP fraction falls below
fleeThreshold, or dead if it is no longer alive. Otherwise, it stays
enraged. It took me __10_ minutes to trace the conditions and confirm this.

3. What stops `setStateName("dancing")` from compiling?


Nothing stops it from compiling because "dancing" is a valid Java String. The compiler does not know 
which strings represent valid monster states.

## 2. AFTER — US-6.1 and US-6.2

**Paste `help` in Exploring and in Combat, side by side:**

```Bash
[Exploring| HP 80/80] > help

  [Exploring] you can: north, south, down, look, take, loot, drop, use, inventory, pack, status, quests, history, undo, help, quit

[Exploring| HP 80/80] > north

  You go north.
  Ghoul is now hunting.
  Combat! 1 hostile(s). Try 'attack'.

[Combat| HP 80/80] > help

  [Combat] you can: attack, look, use, inventory, status, history, undo, help, quit
```

**Paste an attempt to move during combat:**

```Bash
[Combat| HP 80/80] > north

  You cannot do that in Combat mode.
Available here: attack, look, use, inventory, status, history, undo, help, quit
```

**Paste the grep proving the guards are gone:**

```bash
grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
```

```Bash
$ grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
src/main/java/dungeonforge/commands/CommandParser.java:179:        if (room.hasLivingMonsters()) {
src/main/java/dungeonforge/commands/GameContext.java:59:        if (currentRoom.hasLivingMonsters())
 transitionTo(new CombatState());
```

**Paste entering and leaving the pack, and the prompt in each mode:**

```Bash
back
[Pack| HP 78/80] > 

[Exploring| HP 78/80] > take

  You take Vial of Still Water.

[Exploring| HP 78/80] > eake

  You cannot 'eake' here. Type 'help' for what you can do.

[Exploring| HP 78/80] > take

  You take Bone Shortsword.

[Exploring| HP 78/80] > take

  You take Grave Shroud.

[Exploring| HP 78/80] > take

  There is nothing like that here.

[Exploring| HP 78/80] > pack

  --- Your pack ---('back' to close)

inv
[Pack| HP 78/80] > 
  You are carrying:
    - Small Healing Draught [heals 22] (0.3kg, 20g)
    - Chronomancer's Hourglass [2 charges] (rewinds one turn)
    - Vial of Still Water [heals 21] (0.4kg, 25g)
    - Bone Shortsword [dmg 6] (2.0kg, 50g)
    - Grave Shroud [def 3] (3.0kg, 43g)

back
[Pack| HP 78/80] > 
```

## 3. AFTER — US-6.3

**Paste the grep proving `Combat` no longer knows any state name:**

```bash
grep -n "getStateName\|\"enraged\"\|\"idle\"" src/main/java/dungeonforge/core/Combat.java
```

```Bash
joser@Red_Phoenix MINGW64 ~/IdeaProjects/dungeonforge (main)
$ grep -n "getStateName\|\"enraged\"\|\"idle\"" src/main/java/dungeonforge/core/Combat.java
```

**Paste a fight showing a monster moving through its lifecycle:**

```Bash
[Exploring| HP 80/80] > north

  You go north.
  Ghoul is now hunting.
  Combat! 1 hostile(s). Try 'attack'.

[Combat| HP 80/80] > help

  [Combat] you can: attack, look, use, inventory, status, history, undo, help, quit

[Combat| HP 80/80] > north

  You cannot do that in Combat mode.
Available here: attack, look, use, inventory, status, history, undo, help, quit

[Combat| HP 80/80] > pack

  You cannot do that in Combat mode.
Available here: attack, look, use, inventory, status, history, undo, help, quit

[Combat| HP 80/80] > hit

  You hit Ghoul for 10.
  Ghoul hits you for 2.
  ACHIEVEMENT: Cornered -- Force an enemy to change tactics
  Ghoul changes tactics: dungeonforge.behavior.RangedStrategy@144326eb -> aggressive.
  Ghoul is now enraged.

[Combat| HP 78/80] > hit

  You hit Ghoul for 10.
  ACHIEVEMENT: First Blood -- Defeat your first monster
  Ghoul dies. +12 XP.
```

## 4. AFTER — US-6.4

**Paste the test output (or a demo) showing a healer that becomes aggressive when enraged:**

```Bash
$ mvn test
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------< edu.redwoods.cis18:dungeonforge >-------------------
[INFO] Building DungeonForge 0.7.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.4.0:resources (default-resources) @ dungeonforge ---
[INFO] Copying 3 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ dungeonforge ---
[INFO] Recompiling the module because of added or removed source files.
[INFO] Compiling 71 source files with javac [debug release 21] to target\classes
[INFO] 
[INFO] --- resources:3.4.0:testResources (default-testResources) @ dungeonforge ---
[INFO] skip non existing resourceDirectory C:\Users\joser\IdeaProjects\dungeonforge\src\test\resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ dungeonforge ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 6 source files with javac [debug release 21] to target\test-classes
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ dungeonforge ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running dungeonforge.CommandTest
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.086 s -- in dungeonforge.CommandTest
[INFO] Running dungeonforge.FactoryTest
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.029 s -- in dungeonforge.FactoryTest
[INFO] Running dungeonforge.SingletonTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.011 s -- in dungeonforge.SingletonTest
[INFO] Running dungeonforge.SkeletonTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in dungeonforge.SkeletonTest
[INFO] Running dungeonforge.StateTest
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.040 s -- in dungeonforge.StateTest
[INFO] Running dungeonforge.StrategyObserverTest
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.015 s -- in dungeonforge.StrategyObserverTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 85, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.125 s
[INFO] Finished at: 2026-10-16T15:21:39-07:00
[INFO] ------------------------------------------------------------------------

```

**In one sentence: which one changed the other, and could it have gone the other way?**

The state change the strategy we prove in the unit test that it didn't go to the other way.
(i.e. strategy > state)

## 5. Tests and CI

**`mvn test` summary:**

```
$ mvn test
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------< edu.redwoods.cis18:dungeonforge >-------------------
[INFO] Building DungeonForge 0.7.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.4.0:resources (default-resources) @ dungeonforge ---
[INFO] Copying 3 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ dungeonforge ---
[INFO] Recompiling the module because of added or removed source files.
[INFO] Compiling 71 source files with javac [debug release 21] to target\classes
[INFO] 
[INFO] --- resources:3.4.0:testResources (default-testResources) @ dungeonforge ---
[INFO] skip non existing resourceDirectory C:\Users\joser\IdeaProjects\dungeonforge\src\test\resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ dungeonforge ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 6 source files with javac [debug release 21] to target\test-classes
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ dungeonforge ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running dungeonforge.CommandTest
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.086 s -- in dungeonforge.CommandTest
[INFO] Running dungeonforge.FactoryTest
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.029 s -- in dungeonforge.FactoryTest
[INFO] Running dungeonforge.SingletonTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.011 s -- in dungeonforge.SingletonTest
[INFO] Running dungeonforge.SkeletonTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in dungeonforge.SkeletonTest
[INFO] Running dungeonforge.StateTest
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.040 s -- in dungeonforge.StateTest
[INFO] Running dungeonforge.StrategyObserverTest
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.015 s -- in dungeonforge.StrategyObserverTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 85, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.125 s
[INFO] Finished at: 2026-10-16T15:21:39-07:00
[INFO] ------------------------------------------------------------------------

```

**Green CI URL:**

## 6. Sprint review — one sentence

> What can the project do now that it could not do last week?

changes the available commands based on whether the player is exploring, fighting, viewing their pack, or at Game Over. 
Monsters also move through states such as idle, hunting, enraged, fleeing, and dead, and entering a state can 
change their combat strategy.