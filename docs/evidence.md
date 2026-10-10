# Week 7 Evidence

## 1. BEFORE

```bash
mvn -q exec:java
```

Play a few turns, then answer:

**Paste five consecutive `else if` branches from `gameLoop()`:**

```java
} else if (verb.equals("north") || verb.equals("south") || verb.equals("down")) {
} else if (verb.equals("attack")) {
} else if (verb.equals("look")) {
} else if (verb.equals("take")) {
} else if (here.getChest() != null) {
```

**You want to add `equip`. List every edit you would make:**
1. Main.java import dungeonforge.items.Weapon
2. Main.java a 12th else if (verb.equals("equip")) inside gameLoop()
3. Main.java set tookATurn = true in the new branch inside #2
4. Main.java the two hardcoded help print lines, lines 219–220
5. Player.java import dungeonforge.items.Weapon
6. Player.java private Weapon equippedWeapon; + equip(Weapon) + getEquippedWeapon()
7. Player.java make the weapon count toward damage
8. Player.describe() line 59 reads the field attackPower, not the getter
9. Combat.java int damage = player.getAttackPower();
10. src/test/java/dungeonforge/EquipTest.java
11. README-WEEK7.md and any verb list in docs

**You want `e` to mean `equip`. List every *additional* edit:**

1. Functional edit || verb.equals("e")
2. Documentation 2 lines edited by hand, or it silently drifts
3. Can it come from config? No
4. Can you list every alias? No
5. Collision safety silent
6. You would have to modify the conditional block to test for "e" OR "equip" in a case sensitive way.

**You want undo. Where in `gameLoop()` would the code go?** *(There is no good answer. Say
why in one sentence.)*


## 2. AFTER — US-5.1

**Paste your new `gameLoop()` in full. It should fit in a dozen lines:**

```java
  private static void gameLoop(CommandParser parser, GameContext ctx) {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        while (ctx.isRunning() && ctx.getPlayer().isAlive()) {
            System.out.print("\n[HP " + ctx.getPlayer().getHp() + "/" + ctx.getPlayer().getMaxHp() + "] > ");
            System.out.flush();

            String line;
            try { line = in.readLine(); } catch (Exception e) { break; }
            if (line == null) break;
            System.out.println();

            Command command = parser.parse(line, ctx);
            command.execute();

        }

    }
```

**Paste the registry lines that add `drop` and its alias:**

```java
register("drop", DropCommand::new);
```

## 3. AFTER — US-5.2

**The naive-undo failure table from Part D2 step 2:**

| | HP | XP | Gold | Monster HP |
|---|---|---|---|---|
| Before attack | | | | |
| After attack | | | | |
| After naive undo | | | | |

**The same table after `TurnSnapshot`:**

| | HP | XP | Gold | Monster HP |
|---|----|----|------|------------|
| Before attack | 80 | 0  | 0    | 5          |
| After attack | 80 | 20 | 40   | 0          |
| After snapshot undo | 80 | 0  | 0    | 5          |

## 4. AFTER — US-5.3 and US-5.4
You have to replay backwards because if you undid the children forward then you'd end up only going back in history 
1 command


**Paste a `loot` that picks up three things, then an `undo` that puts them all back:**

```Bash
look
[HP 76/80] > 
  [L1R1Level 1: Crypt
    "Damp stone. Something drips in the dark, patiently."
    Iron-bound Chest: 
      - Bone Shortsword [dmg 6] (2.0kg, 50g)
      - Bone Shortsword [dmg 6] (2.0kg, 50g)
      - Grave Shroud [def 3] (3.0kg, 43g)
    Exits: south, north

loot
[HP 76/80] > 
  You take Bone Shortsword.
  You take Bone Shortsword.
  You take Grave Shroud.

```

**Paste the hourglass rewinding a turn, with `inventory` before and after:**

```Bash
inventory
[HP 76/80] > 
  You are carrying:
    - Small Healing Draught [heals 22] (0.3kg, 20g)
    - Chronomaster's Hourglass [2 charges] (rewinds one turn)
    - Bone Shortsword [dmg 6] (2.0kg, 50g)
    - Bone Shortsword [dmg 6] (2.0kg, 50g)
    - Grave Shroud [def 3] (3.0kg, 43g)

use Chronomaster's Hourglass
[HP 76/80] > 
  Sand runs backwards. The last moment unhappens.

inventory
[HP 76/80] > 
  You are carrying:
    - Small Healing Draught [heals 22] (0.3kg, 20g)
    - Chronomaster's Hourglass [1 charges] (rewinds one turn)
```

**How long did US-5.4 actually take you?** __40__ minutes

## 5. The replay

**Paste your `history` output and the first ten lines of replaying it with `--script=`:**

```Bash
[INFO] --- exec:3.2.0:java (default-cli) @ dungeonforge ---
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.6.0   seed 20260829

  Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' reweinds a turn
  [L1R0Level 1: Crypt
    "burial niches line the wall. Must be empty. MOST."
    Exits: north

[script] > help
  Available: [north, south, down, attack, take, drop, use, look, loot, inventory, status, quests, help, quit]
 Aliases: {n=north, s=south, d=down, a=attack, kill=attack, hit=attack, get=take, t=take, drink=use, quaff=use, i=inventory, ?=help, exit=quit}

[script] > n
  You go north.
  Ghoul Ghoul looses a shot for 2

[script] > hit
  You hit Ghoul for 10
  Ghoul Ghoul looses a shot for 2

[script] > hit
  You hit Ghoul for 10
  ACHIEVEMENT: First Blood -- Defeat your first monster
  Ghoul dies.12

[script] > loot
  You take Bone Shortsword.
  You take Bone Shortsword.
  You take Grave Shroud.

[script] > n
  You go north.

[script] > quit
  You lay down your pack.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.132 s
[INFO] Finished at: 2026-10-10T11:48:20-07:00
[INFO] ------------------------------------------------------------------------
```

## 6. Tests and CI

**`mvn test` summary:**

```Bash
joser@Red_Phoenix MINGW64 ~/IdeaProjects/dungeonforge (main)
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
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- resources:3.4.0:testResources (default-testResources) @ dungeonforge ---
[INFO] skip non existing resourceDirectory C:\Users\joser\IdeaProjects\dungeonforge\src\test\resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ dungeonforge ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ dungeonforge ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running dungeonforge.CommandTest
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.080 s -- in dungeonforge.CommandTest
[INFO] Running dungeonforge.FactoryTest
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.028 s -- in dungeonforge.FactoryTest
[INFO] Running dungeonforge.SingletonTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.013 s -- in dungeonforge.SingletonTest
[INFO] Running dungeonforge.SkeletonTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in dungeonforge.SkeletonTest
[INFO] Running dungeonforge.StrategyObserverTest
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.017 s -- in dungeonforge.StrategyObserverTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 63, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.192 s
[INFO] Finished at: 2026-10-10T11:50:55-07:00
[INFO] ------------------------------------------------------------------------
```

**Green CI URL:**

## 7. Sprint review — one sentence

> What can the project do now that it could not do last week?

The project can now keep a history of the commands used during a game session, 
allowing us to review those actions and replay the session from a file.
