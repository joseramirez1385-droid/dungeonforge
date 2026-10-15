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

```

```

**Paste an attempt to move during combat:**

```

```

**Paste the grep proving the guards are gone:**

```bash
grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
```

```

```

**Paste entering and leaving the pack, and the prompt in each mode:**

```

```

## 3. AFTER — US-6.3

**Paste the grep proving `Combat` no longer knows any state name:**

```bash
grep -n "getStateName\|\"enraged\"\|\"idle\"" src/main/java/dungeonforge/core/Combat.java
```

```

```

**Paste a fight showing a monster moving through its lifecycle:**

```

```

## 4. AFTER — US-6.4

**Paste the test output (or a demo) showing a healer that becomes aggressive when enraged:**

```

```

**In one sentence: which one changed the other, and could it have gone the other way?**


## 5. Tests and CI

**`mvn test` summary:**

```

```

**Green CI URL:**

## 6. Sprint review — one sentence

> What can the project do now that it could not do last week?

