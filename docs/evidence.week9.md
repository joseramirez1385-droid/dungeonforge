# Week 9 Evidence

Fill this in as you go. Parts 0–2 and Part 5 are graded on the *measurement*, not on the code.

---

## 0. The Sprint 6 carry-over (US-6.7)

Before any Decorator work. `docs/sprint-07-plan.md` has the story.

```bash
grep -n 'isRunning\|!running' src/main/java/dungeonforge/Main.java \
                              src/main/java/dungeonforge/commands/GameContext.java
```

**Paste the output. How many places spell out the game-over rule?** ______

**Now open `GameOverState.java`. It lists four verbs. Try to reach one in play. What happens,
and why?**


**One sentence: `running` and `GameOverState` both mean "the game is over". Do they? Say what
each one actually means.**


---

## 1. BEFORE — count the problem

```bash
ls src/main/java/dungeonforge/items/ | grep -E 'Weapon|Enchant'
grep -rln "damage + 4" src/main/java/dungeonforge/items/
grep -rho "instanceof" src/main/java/dungeonforge/items/*.java | wc -l
grep -c "return null" src/main/java/dungeonforge/items/EnchantmentForge.java
wc -l src/main/java/dungeonforge/items/EnchantmentForge.java
```

| | Count |
|---|-------|
| Weapon/enchantment classes | 8     |
| Files containing the fire damage bonus | 4     |
| `instanceof` tests in `items/` | 34    |
| `return null` paths in `EnchantmentForge` | 13    |
| Lines in `EnchantmentForge` | 108   |

**Open `FlamingFrostVampiricWeapon.java`. Which of its methods contain arithmetic that also
appears, word for word, in another file?**

getDamage() is the method and getValue()

**You want a fourth enchantment, `keen`. Before you look anything up: how many new classes
would this package need?** ___15___

*(Write it down before reading on.)* For N enchantments the package needs 2^N − 1 classes.
N = 3 is 7. N = 4 is 15. N = 5 is 31.

---

## 2. BEFORE — reproduce all four defects

You start carrying a **Notched Shortsword**, so this works on turn one. Run
`mvn -q exec:java` and type exactly this:

```
equip shortsword
status
enchant shortsword with flaming
status
inventory
equip shortsword
status
enchant shortsword with flaming
enchant shortsword with keen
quit
```

**(a) Paste the two `status` lines from before and after the first `enchant`:**

```Bash
[Exploring | HP 80/80] > status

  Delver  HP 80/80  ATK 15  DEF 3  Gold 0  XP 0  Carry 60.0kg  wielding Notched Shortsword [dmg 5] (2.5kg, 25g)
[Exploring | HP 80/80] > enchant shortsword with flaming

  The Notched Shortsword is now Flaming Notched Shortsword [dmg 9] (2.5kg, 85g)

[Exploring | HP 80/80] > status

  Delver  HP 80/80  ATK 15  DEF 3  Gold 0  XP 0  Carry 60.0kg  wielding Notched Shortsword [dmg 5] (2.5kg, 25g)
  
```

**Did your damage change? Explain in one sentence why not.**


**(b) Paste the refusal from the second `enchant ... with flaming`:**

```Bash
[Exploring | HP 80/80] > enchant shortsword with flaming

  That weapon cannot take another flaming enchantment.
```

**Is there anything wrong with what you asked for? Yes or no — then say what the refusal was
really reporting.**


**(c) Paste the response to `enchant ... with keen`:**

```Bash
[Exploring | HP 80/80] > enchant shortsword with keen

  No such enchantment: keen
```

**(d) Open `EnchantmentForge.baseDamage()`. In one sentence: why does this method exist at all?
What would have to be true for it to be unnecessary?**


**(e) Classify all four.** Two of these are the *design*; one is an ordinary *bug* that would
exist in any design. Which is which, and how do you know?

| Defect | Design, or ordinary bug? | How you can tell |
|---|---|---|
| (a) stale hand |Design | |
| (b) no double enchantment | Design| |
| (c) no fourth enchantment |Design | |
| (d) base-value recovery |bug | |

---

## 3. AFTER — US-7.3, the receipt

**Paste `git diff --stat HEAD~1` for the commit that added the fourth enchantment:**

```Bash
$ git diff --stat HEAD~1
 .../java/dungeonforge/commands/EnchantCommand.java |  29 +--
 .../java/dungeonforge/commands/TurnSnapshot.java   |   4 +
 .../java/dungeonforge/items/EnchantmentForge.java  | 118 -----------
 .../dungeonforge/items/EnchantmentRegistry.java    |  32 +++
 src/main/java/dungeonforge/items/KeenWeapon.java   |  18 ++
 src/test/java/dungeonforge/DecoratorTest.java      | 228 +++++++++++++++++++++
 6 files changed, 297 insertions(+), 132 deletions(-)
```

**New files:** ___1___  **Modified files:** ___1___  **Lines added to modified files:** ___1___

**Now re-run the Part 1 counts on the finished package:**

| | Before | After |
|---|--------|-------|
| Weapon/enchantment classes | 8      | 7     |
| Files containing the fire bonus | 4      | 0     |
| `instanceof` in `items/` | 34     | 0     |
| `return null` paths in the selection class | 13     | 0     |
| Lines in the selection class | 108    | 33    |

---

## 4. AFTER — US-7.4, order

**Paste your two order tests, and their four numbers:**

```java
    @Test
    void orderChangesTheResultForMultiplyingDecorators() {
        int keenThenFire = new FlamingWeapon(new KeenWeapon(sword())).getDamage();
        int fireThenKeen = new KeenWeapon(new FlamingWeapon(sword())).getDamage();
        assertEquals(16, keenThenFire);
        assertEquals(18, fireThenKeen);
        assertNotEquals(keenThenFire, fireThenKeen, "if these were equal, order would not matter "
                + "and this test would be pointless");
    }

    @Test
    void orderChangesHowMuchAVampiricWeaponDrinks() {
        assertEquals(3, new VampiricWeapon(new FlamingWeapon(sword())).lifesteal());
        assertEquals(2, new FlamingWeapon(new VampiricWeapon(sword())).lifesteal());
    }
```

| Stack | Result      |
|---|-------------|
| `Flaming(Keen(sword))` | dmg ___16___ |
| `Keen(Flaming(sword))` | dmg __18__  |
| `Vampiric(Flaming(sword))` | drain ___3___ |
| `Flaming(Vampiric(sword))` | drain ___2___ |

**One paragraph. Why is this correct behaviour rather than a bug?** *(There is one idea to
state, and everything else follows from it.)*

This is correct behavior because the different results come from the order of the operations, so they are 
expected rather than a bug.


---

## 5. The payoff — what did NOT change

```bash
git diff --stat main -- src/main/java/dungeonforge/core/
```

**Which files in `core/` changed during this entire sprint?** ___25___

**`Combat.playerStrikes()` computes the damage the player deals and the hit points they drink
back. You added four enchantments this week, two of which change damage and one of which
changes healing. How many lines of `Combat.java` did you edit?** ___0___

**Explain that number** in two or three sentences, using the phrase *programs to the
interface* and naming which interface.

I changed 0 lines in Combat.java because it programs to the interface, which is Weapon. 
Each enchanted weapon uses that same interface, so Combat can get its damage and lifesteal without needing to
know which enchantments it has.


---

## 6. The honest accounting

**Of the four defects in Part 2, which one did Decorator fix, and which one did you fix?**


**Quote the lines that fixed the one Decorator did not.**

```java
Decorator fixed the design problems with repeated enchantments, adding a fourth enchantment, and needing to recover 
base values. I fixed the stale-hand problem by making sure the player equips the new enchanted weapon if they were 
holding the original.

The lines that fixed the stale-hand problem were:

wasEquipped = (player().getEquippedWeapon() == w);

Then, after replacing the weapon in the inventory:
        
if (wasEquipped) { player().equip(after); }
```

**Would those lines have worked before the refactor?** ___Yes___

---

## 7. Retro line

**One sentence on something Decorator gave you, and one on something it did not.**

Decorator let me combine and repeat enchantments without creating a new class for every combination. It did not 
automatically update the weapon in the player’s hand, so I had to fix that in EnchantCommand
