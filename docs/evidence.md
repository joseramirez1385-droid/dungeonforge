# Week 3 Evidence — the before-and-after

> Your Definition of Done asks for evidence that the acceptance criteria are met. This file
> is where it goes. Fill it in as you work, not at the end.

## 1. BEFORE — the problem, demonstrated

Do this **before writing any code**:

```bash
mvn -q exec:java > run1.txt
mvn -q exec:java > run2.txt
diff run1.txt run2.txt
```

**Paste a few lines of the diff:**

```
11,17c11,17
< L1R1: (empty)
< L1R2: Skeleton (16/16 HP, ATK 5)
< L1R3: Wight (16/16 HP, ATK 4)
< L1R4: Bone Priest (15/15 HP, ATK 4)  Wight (17/17 HP, ATK 5)
< L1R5: Skeleton (16/16 HP, ATK 5)  Skeleton (16/16 HP, ATK 5)
< L1R6: (empty)
< L1R7: Bone Priest (15/15 HP, ATK 4)
---
> L1R1: Skeleton (16/16 HP, ATK 4)  Skeleton (18/18 HP, ATK 5)
> L1R2: (empty)
> L1R3: (empty)
> L1R4: Bone Priest (18/18 HP, ATK 6)
> L1R5: Crypt Rat (16/16 HP, ATK 6)
> L1R6: Skeleton (16/16 HP, ATK 6)  Crypt Rat (17/17 HP, ATK 4)
> L1R7: Crypt Rat (17/17 HP, ATK 6)
19c19
< L2R0: (empty)
---
```

**How many separate `Random` objects did you find in the starter?** 3 
(`grep -rn "new Random(" src/main/java`)

```bash
src/main/java/dungeonforge/core/GameWorld.java:19:    private final Random random = new Random();
src/main/java/dungeonforge/core/Monster.java:16:    private static final Random RNG = new Random();
src/main/java/dungeonforge/core/Room.java:15:    private final Random rng = new Random();
```

**In one sentence: why does that make a bug report like "the boss room on level 2 was empty"
impossible for me to act on?**


## 2. AFTER — US-1.1, settings live in one place

```bash
grep -rn "playerStartingHp\|60\|new Random(" src/main/java/dungeonforge/core
```

**Paste the output. AC2 wants zero hardcoded literals outside the config class:**

```
joser@Red_Phoenix MINGW64 ~/IdeaProjects/dungeonforge (week-03-singleton)
$ grep -rn "playerStartingHp\|60\|new Random(" src/main/java/dungeonforge/core
src/main/java/dungeonforge/core/GameWorld.java:21:    private final Random random = new Random();
src/main/java/dungeonforge/core/Monster.java:16:    private static final Random RNG = new Random();
src/main/java/dungeonforge/core/Player.java:20:                GameConfig.getInstance().getInt("playerStartingHp"),
src/main/java/dungeonforge/core/Room.java:15:    private final Random rng = new Random();

joser@Red_Phoenix MINGW64 ~/IdeaProjects/dungeonforge (week-03-singleton)
$ 
```

**Change `playerStartingHp` in `config.json` to 200, run, and paste the player line:**

```bash
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.2.0

Delver  HP 200/200  ATK 10  DEF 3  Gold 0  XP 0  Carry 60.0kg

-- Level 1 --
L1R0: Skeleton (18/18 HP, ATK 5)
L1R1: Bone Priest (15/15 HP, ATK 6)  Wight (17/17 HP, ATK 6)
L1R2: Bone Priest (16/16 HP, ATK 4)  Bone Priest (18/18 HP, ATK 5)
L1R3: Skeleton (16/16 HP, ATK 4)  Crypt Rat (16/16 HP, ATK 6)
L1R4: Crypt Rat (14/14 HP, ATK 6)
L1R5: Skeleton (16/16 HP, ATK 5)
L1R6: (empty)
L1R7: Crypt Rat (14/14 HP, ATK 6)
```

**Rename `config.json` to `config.json.bak`, run again, and paste what happens (AC4):**

```bash
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.2.0

Delver  HP 80/80  ATK 10  DEF 2  Gold 0  XP 0  Carry 60.0kg

-- Level 1 --
L1R0: (empty)
L1R1: Bone Priest (16/16 HP, ATK 5)  Skeleton (14/14 HP, ATK 4)
L1R2: Skeleton (18/18 HP, ATK 4)  Skeleton (17/17 HP, ATK 5)
L1R3: (empty)
L1R4: Wight (16/16 HP, ATK 5)  Skeleton (18/18 HP, ATK 6)
L1R5: Wight (16/16 HP, ATK 5)
L1R6: Crypt Rat (16/16 HP, ATK 5)  Skeleton (17/17 HP, ATK 5)
L1R7: Crypt Rat (18/18 HP, ATK 5)
```

## 3. AFTER — US-1.2, the same seed produces the same dungeon

```bash
mvn -q exec:java > after1.txt
mvn -q exec:java > after2.txt
diff after1.txt after2.txt && echo "IDENTICAL"
```

**Result:**

```Bash
$ diff after1.txt after2.txt && echo "IDENTICAL"
IDENTICAL
```

**Now a different seed (AC4). Paste enough to show the world changed:**

```Bash
$ diff after1.txt after3.txt
10,17c10,17
< L1R0: Skeleton (17/17 HP, ATK 5)  Crypt Rat (16/16 HP, ATK 4)
< L1R1: Bone Priest (18/18 HP, ATK 5)
< L1R2: (empty)
< L1R3: (empty)
< L1R4: Skeleton (17/17 HP, ATK 5)
< L1R5: (empty)
< L1R6: Bone Priest (16/16 HP, ATK 5)
< L1R7: Crypt Rat (15/15 HP, ATK 5)  Skeleton (16/16 HP, ATK 4)
---
> L1R0: (empty)
> L1R1: (empty)
> L1R2: Crypt Rat (17/17 HP, ATK 4)
> L1R3: Bone Priest (14/14 HP, ATK 5)  Wight (18/18 HP, ATK 6)
> L1R4: (empty)
> L1R5: Bone Priest (17/17 HP, ATK 4)  Skeleton (18/18 HP, ATK 4)
> L1R6: (empty)
> L1R7: Crypt Rat (17/17 HP, ATK 6)
```

## 4. AFTER — US-1.3, the rule is enforced

**Paste your `mvn test` summary:**

```

```

**Paste the URL of the green CI check on your pull request:**


## 5. The one-line summary for your Sprint Review

> What can the project do now that it could not do last week?


