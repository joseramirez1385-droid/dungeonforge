# The Iterator / Composite Clinic — Week 13, Part D

Ten points. Graded on **measurement and precision**, not prose. The rule from Week 9 still
holds: **a number without the command that produced it earns half marks.**

---

## D1 — The class that was right and then was wrong · 3 pts 🔎

**Step 1.** `git log --oneline` your own branches. Find the commit where you created
`RoomContentsIterator` and the commit where you deleted it. **How many commits apart?** ___2___

**Step 2.** Nothing was wrong with that class. Write down, in one sentence, **what changed about
the data** that made a correct class the wrong class.

```
Chest became an Item, so the room's contents changed from two flat lists into a tree. The old iterator
could reach the items inside a chest, but it did not return the chest itself.
```

**Step 3.** Run US-10.1's tests against the finished build.

**Command:** ```mvn -Dtest=IteratorCompositeTest test``` **Result:** ```Tests run: 24, Failures: 0, Errors: 0, Skipped: 0```

**Step 4 — the one worth the points.** Those tests were written against a class that no longer
exists, and they never changed. **What were they asserting?**

The question's premise is wrong: three of the seven tests never changed, but four did.

| US-10.1 test | After US-10.3 |
|---|---|
| removeBeforeNextIsAnErrorRatherThanAGuess | Unchanged |
| takeCommandHasExactlyOneSearchPath | Unchanged |
| countingLootNoLongerForgetsTheFloor | Unchanged |
| oneWalkReachesItemsWhereverTheRoomKeepsThem | 3 items becomes 4 |
| theWalkIsHonestWhenOneCollectionIsEmpty | 1 becomes 2 |
| removeTakesTheItemOutOfWhicheverCollectionItWasIn | Two assertions change |
| twoOfTheSixFilesNoLongerKnowARoomHasTwoContainers | The expectation is inverted |

The three unchanged tests assert the iterator's contract: remove() before next() throws, TakeCommand has one search path, and totalLoot() counts floor items.

The four updated tests depended on the room's structure. Composite changed that structure because the chest itself is now an item, which allows take <chest> to work.

The tests should assert the contract callers depend on, rather than the implementation behind it.

> ____________________________________________________________________

**Step 5.** Write one sentence a future you could use as a rule, beginning *"A test should assert
…"*.
```
A test should assert the contract a caller depends on, rather than the shape of the data that happens to 
satisfy it today.
```
---

## D2 — Transparency versus safety · 3 pts

**Step 1.** Paste the declaration of `children()` from your `Item`.

```java
 default Iterator<Item> children() {
        return Collections.emptyIterator();
    }

    default boolean isContainer() {
        return children().hasNext();
    }
```

**Step 2.** Head First p. 364 puts `add()` on the Component. Write the two-line version of what
`Potion.add(x)` would have to do in that design.

```java
public void add(Item item) {
    throw new UnsupportedOperationException("A potion holds nothing");
}
```

**Step 3.** Fill this in:

| | our version | Head First's |
|---|-------------|--------------|
| `item.add(thing)` compiles? | no          | yes          |
| `potion.add(thing)` throws at run time? |    Not applicable—the call does not compile          | yes          |
| a caller putting something away needs |   That it holds a Chest, using its declared type, a cast, or instanceof          | nothing      |

**Step 4.** In Week 12 you met the same trade twice — `Arrays.asList(...).add()` and
`EnumerationIterator.remove()`. **Which column were those in?** ___heads first transparency__

```
Arrays.asList(...).add() and EnumerationIterator.remove() use transparency. They expose the operation through the 
interface but throw UnsupportedOperationException when they cannot support it
```

**Step 5 — the one worth the points.** You are shipping this as a library. A thousand strangers
will write against `Item`. **Which column do you pick, and does your answer change?** Two
sentences, and the second one has to name what changed.

```
For a library used by strangers, I would choose transparency because callers get a uniform interface and can follow the 
documented unsupported operations. For this project, I would choose safety because our team controls the callers and can
 catch invalid add() calls at compile time.
```

---

## D3 — Why `remove()` could not throw · 2 pts 🔨

**Step 1.** Head First's composite iterator throws on `remove()` (p. 368). Paste your
`CompositeIterator.remove()`.

```java
@Override
public void remove() {
    if (lastUsed == null) throw new IllegalStateException("remove() before next()");
    if (descendInto != null && stack.peek() == descendInto) stack.pop();
    lastUsed.remove();
    lastUsed = null;
    descendInto = null;
}
```

**Step 2 — the experiment.** Replace its body with
`throw new UnsupportedOperationException();` and run `mvn test`.

**How many tests fail?** 9 ( 1 failure, 8 errors) **Name two.** see below

```bash
[ERROR] Failures: 
[ERROR]   IteratorCompositeTest.removeBeforeNextIsAnErrorRatherThanAGuess:166 Unexpected exception type thrown, expected: <java.lang.IllegalStateException> but was: <java.lang.UnsupportedOperationException>                                                                                                                    
[ERROR] Errors: 
[ERROR]   CommandTest.aMacroPicksUpEverythingAndUndoPutsItAllBack:215->run:52 » UnsupportedOperation
[ERROR]   IteratorCompositeTest.carriedWeightSumsWhatYouAreHoldingAndStopsWhenItIsTooMuch:392->run:49 » UnsupportedOperation
[ERROR]   IteratorCompositeTest.lootTakesTheTopLevelBecauseTheBoxCarriesItsOwnContents:408->run:49 » UnsupportedOperation
[ERROR]   IteratorCompositeTest.removeTakesTheItemOutOfWhicheverCollectionItWasIn:143 » UnsupportedOperation
[ERROR]   IteratorCompositeTest.removingAContainerStopsTheWalkDescendingIntoIt:315 » UnsupportedOperation
[ERROR]   IteratorCompositeTest.undoRestoresANestedContainersContents:374->run:49 » UnsupportedOperation
[ERROR]   IteratorCompositeTest.undoRestoresWhatWasInsideAContainer:356->run:49 » UnsupportedOperation
[ERROR]   IteratorCompositeTest.youCanPickUpTheChestTheGameToldYouAboutAndAlsoReachInsideIt:337->run:49 » UnsupportedOperation
```

**Put it back.**

**Step 3.** `TakeCommand` is the caller that forced this. **If `remove()` threw, what would
`TakeCommand` have to do instead?** One sentence.

```bash
TakeCommand would have to find out which collection contained the item so it could remove the item from that collection.
```

**Step 4.** That answer is the exact thing the iterator was introduced to stop. Write the rule in
one sentence, beginning *"Whether an iterator supports removal is a question about …"*.

```aiignore
Whether an iterator supports removal is a question about what its callers need to do.
Our iterator serves TakeCommand, which must remove items from the room. Without iterator removal, TakeCommand would
need to know the storage details the iterator was introduced to hide.
```

---

## D4 — The two walks and the leaves · 2 pts

**Step 1.**

| | walks | one example of something it must find |
|---|---|---|
| `take <name>` | The whole tree|A vial inside a chest  |
| `loot` | The top level|The chest itself, which brings its contents |

**Step 2.** Change `loot` to walk the tree. Run it in a room with a chest holding two items.

**What happens, and why?** ______

**Put it back.**

**Step 3.** `git diff --stat` the whole sprint.

**How many Leaf classes did you change?** 0 **How many lines of `Potion.java`?** 0

**One sentence on what those numbers say.**

```
Composite let us add a tree structure without changing 
the leaf classes or making them know that a tree exists
```

**Step 4 — the sentence to take into the final.**

| Pattern | What it is actually for |
|---|---|
| Iterator |Giving callers sequential access to items without exposing how those items are stored |
| Composite | Letting callers treat one item and a tree of items through the same interface|

**And: which of the two did your codebase genuinely need this week, and which was mostly setup
for the other? Defend it with a number from Part 3.**

```
Composite solved the player-facing problem because the chest became an item that could be carried, nested, and weighed. 
Files that knew about the room's two separate containers dropped from 6 to 0; Iterator alone had only reduced that count
 to 4.
```

---

## Turning it in

Commit `docs/iterator-composite-clinic.md` with D1–D4 complete, the failure list in D3 Step 2
pasted verbatim, and **both experiments put back** — `CompositeIterator.remove()` working and
`loot` walking the top level.
