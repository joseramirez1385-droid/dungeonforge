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
|---|---|---|---|---|
| Before attack | | | | |
| After attack | | | | |
| After snapshot undo | | | | |

## 4. AFTER — US-5.3 and US-5.4

**Paste a `loot` that picks up three things, then an `undo` that puts them all back:**

```

```

**Paste the hourglass rewinding a turn, with `inventory` before and after:**

```

```

**How long did US-5.4 actually take you?** ____ minutes

## 5. The replay

**Paste your `history` output and the first ten lines of replaying it with `--script=`:**

```

```

## 6. Tests and CI

**`mvn test` summary:**

```

```

**Green CI URL:**

## 7. Sprint review — one sentence

> What can the project do now that it could not do last week?

