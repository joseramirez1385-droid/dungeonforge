# Week 13 Evidence

Fill this in as you go. Parts 0–3 and Part 5 are graded on the **measurement**, not the prose.

**Every counting command pipes through `scripts/codeonly.py`.** The reference solution's own
javadoc contains the strings `getChest()` and `private Chest chest`. Grep the code, not the
commentary.

---

## 0. The Sprint 9 carry-over (US-9.4)

Open `events/DamageMeter.java` and find where its warning level comes from.

**Paste that line.** ______

**`playerStartingHp` is a constant in `config.json`. In one sentence: what is the meter supposed
to be asking, and who is it asking instead?**


**Now try to write a test that proves it is wrong.**

**What stops you?** ______

**One sentence: why did this defect survive four sprints?**


---

## 1. BEFORE — count the files

```bash
for f in $(find src/main/java -name '*.java'); do
  python3 scripts/codeonly.py "$f" | grep -qE 'getChest\(\)|hasChest\(\)|setChest\(' && echo "$f"
done
```

**List them:** ______

```bash
src/main/java/dungeonforge/commands/TakeCommand.java
src/main/java/dungeonforge/commands/TurnSnapshot.java
src/main/java/dungeonforge/commands/CommandParser.java
src/main/java/dungeonforge/factory/RoomPopulator.java
src/main/java/dungeonforge/core/GameWorld.java
src/main/java/dungeonforge/core/Room.java
```

**How many?** ___6___

**Now the sentence. Name the count and say what it is a count OF.** *(Two points. "Six files use
getChest" is not it — six files know *something*, and naming that thing is the answer.)*

```
This counts the files that know a room stores loot in two separate places:
the floor collection and the chest's contents.

```

**And one more count:**

```bash
grep -rln "carriedWeight\|totalWeight" src/main/java/
```

**Result:** ___60kg___

**Every `Item` has declared `getWeight()` since Week 1. In one sentence, what does that result
mean about the number on your status line?**


---

## 2. BEFORE — the two halves

Open `commands/TakeCommand.java`. Put `takeFromFloor()` and `takeFromChest()` side by side.

**Paste both:**

```java
private Item takeFromFloor() {
        for (Item i : room().getFloorItems()) {
            if (matches(i)) { room().getFloorItems().remove(i); return i; }
        }
        return null;
    }

    private Item takeFromChest() {
        if (!room().hasChest()) return null;
        for (Item i : room().getChest().getContents()) {
            if (matches(i)) { room().getChest().getContents().remove(i); return i; }
        }
        return null;
    }
```

**Ignoring the collection each one walks, how many lines actually differ?** ____2__

**Now play the game.** `mvn -q exec:java`, then: go north, kill whatever is there, `look`,
`take <the chest's name>`.

**Paste those four lines of output:**

```
[Exploring | HP 75/80] > look

  [L1R1] Level 1: Crypt
  "Damp stone. Something drips in the dark, patiently."
  Iron-bound Chest:
    - Vial of Still Water [heals 21] (0.4kg, 25g)
    - Vampiric Bone Shortsword [dmg 6] [drain 1] (2.0kg, 140g)
    - Grave Shroud [def 3] (3.0kg, 43g)
  Exits: south, north

[Exploring | HP 75/80] > take Iron-bound

  There is nothing like that here.

[Exploring | HP 75/80] > 
```

**The game listed a chest, let you take something out of it, and then said it does not exist.
Which of those three statements is false?**

```
That the chesy doesn't exist is false. It clearly exists with look.

```


**Open `items/Chest.java` and read the comment at the top. When was it written?** ___week 4___

---

## 3. BEFORE — the counting

```bash
C="python3 scripts/codeonly.py"
$C src/main/java/dungeonforge/core/Room.java | grep -cE 'List<Item> floorItems|private Chest chest'
$C src/main/java/dungeonforge/commands/TakeCommand.java | grep -cE 'private Item takeFrom'
$C src/main/java/dungeonforge/items/Chest.java | grep -c 'implements Item'
```

| | Before    | After                      |
|---|-----------|----------------------------|
| files that know a room has two containers | 6         | 0                          |
| collections a `Room` keeps items in | 2         | 1                          |
| search methods in `TakeCommand` | 2         | 0—one inline iterator loop |
| `Chest` is an `Item` (yes/no) | no        | yes                        |
| files that sum item weight | 0         | 2                          |
| `take <chest>` works (yes/no) | no        | yes                        |
| `totalLoot()` after dropping two items | unchanged | Increases by 2             |

*(After column at the end of the sprint. **Nothing in it may be a guess.**)*

---

## 4. The gap US-10.2 leaves on purpose

Finish US-10.2, then play the game again and try `take <the chest's name>`.

**What happened?** __There is nothing like that there.____

**Complete this sentence:**

> A chest is an `Item` now and I still cannot pick one up, because
> Room still stores it in a separate chest field and the current iterator
returns its contents without returning the chest itself.


**Which story owns that, and which of its acceptance criteria?** ______

---

## 5. AFTER — the receipt

### The deletion

```bash
ls src/main/java/dungeonforge/core/RoomContentsIterator.java
grep -rl 'RoomContentsIterator' src/
mvn test
```

**Paste all three.**

```
ls: cannot access 'src/main/java/dungeonforge/core/RoomContentsIterator.java': No such file or directory



[INFO] Tests run: 173, Failures: 0, Errors: 0, Skipped: 0
```

**US-10.1's tests were written against a class that no longer exists, and they still pass. In one
sentence: what were they actually asserting?**


### The bug nobody reported

**Read the version of `totalLoot()` you started the week with. What did it count?** __Chest contents only____

**What did it NOT count?** Loose items on the floor

**Why was that invisible?** zero loose items on the floor, and everything started inside the chest, it was never
executed until a player dropped somthing.

**Paste your before-and-after measurement** — `totalLoot()` with nothing on the floor, then after
dropping two items.

```                             
                                 Starter  Finished 
totalLoot() at gereration          25        25
after dropping 2 on thwe floor     25        25
```

**One sentence, and be careful with it: did Iterator FIND this bug?** *(There is a wrong answer
that sounds good.)*


---

## 6. AFTER — what an iterator cannot do

**After US-10.1, how many files still knew a room had two containers?** 4

**Name them, and for each one say why `Room.contents()` could not serve it:**

| File | Why not |
|---|---|
| CommandParser.describeRoom() | Prints “On the floor: X” and “Iron-bound Chest: Y”—different sentences to a player. A walk that forgets structure cannot tell them apart. |
| TurnSnapshot | Has to put things back. A flat walk records what was in the room, but not which collection each thing returns to. |
| RoomPopulator | Has to put a chest somewhere, and that “somewhere” was a field of its own. |
| Room itself | Is allowed to know how it stores things. That is its job. |


**One of those three still reads the room's collections directly in your finished code. Which,
and is that a failure to finish the refactor? Answer yes or no and defend it.**

```
CommandParser.describeRoom(), and no. It needs location information because its job is to report where items are.
An iterator that hides structure cannot provide that distinction.
```

**For the other two: what fixed them — a better iterator, or something else?**

```
Composite fixed them by removing the second collection, so there was no longer a separate storage location to track. 
The change was to the data structure, rather than the traversal.
```
---

## 7. AFTER — the honest accounting

### Composite's trade

**Head First puts `add()` on the Component and lets leaves throw (p. 364). You did not.**

**Name one thing a caller cannot write because of that choice.** ______

**Name one thing that cannot go wrong at run time because of it.** ______

**Which would you choose for a library used by strangers? Is that the same answer you gave for
this codebase?**


### The two walks

| | walks | means |
|---|---|---|
| `take <name>` | | |
| `loot` | | |

**One sentence: what goes wrong if `loot` walks the tree?**


### Leaves

**How many Leaf classes did you change this week?** ______

**One sentence on what that number says about the pattern.**


### Retro line

| Pattern | Gave | Took |
|---|---|---|
| Iterator | | |
| Composite | | |

**And: the composite walk yields a container before its contents. Name the player-facing bug
that comes back if you reverse it.**


