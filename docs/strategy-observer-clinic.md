# The Coupling Clinic — Lab 5, Part D

> Week 3 was about refusing a pattern. Week 4 was about telling three similar patterns apart.
> **Week 5 is about coupling** — and both of this week's patterns exist to reduce it, in two
> completely different directions.

## D1 — The arithmetic, BEFORE you write any code · 8 pts

**Do this section first.** It takes ten minutes and it decides whether the rest of the week
makes sense to you.

DungeonForge has **15 monster species**. We want **4 combat behaviours**: aggressive, ranged,
skittish, healer.

### The subclassing approach

Suppose behaviour is expressed by subclassing `Monster` — `AggressiveSkeleton`,
`SkittishSkeleton`, `RangedImp`, and so on.

| Question | Your answer                                           |
|---|-------------------------------------------------------|
| How many classes for 15 species × 4 behaviours? | 60                                                    |
| Add a 5th behaviour (say, "berserk"). How many NEW classes? | 15                                                    |
| Add a 16th species. How many NEW classes? | 4                                                     |
| A skeleton is losing badly and should start running. **Can a `SkittishSkeleton` object become an `AggressiveSkeleton` object at runtime?** Answer yes or no and say why. | No, We can't change there type once there in the game |

### The composition approach

| Question | Your answer |
|---|------------|
| How many classes for 15 species + 4 strategies? | 19         |
| Add a 5th behaviour. How many NEW classes? | 1          |
| Add a 16th species. How many NEW **Java** files? | 1          |
| Can a monster change behaviour at runtime? How? | Yes        |

**Now write two or three sentences.** Head First calls this the SimUDuck problem. In your own
words: **what is the actual defect in the subclassing design?** Not "it's more classes" —
there's a deeper problem that the last row of each table points at.

I think the actual defect has to be that a SkittishSkeleton cant become a AggressiveSkeleton at run time, 
But a monster can change its behavior at runtime.

## D2 — The coupling experiment · 9 pts

The Observer pattern's claim is that **a publisher need never know its subscribers**. Measure
it.

**Commit your work first**, so `git diff --stat` means something.

**Add a fourth listener.** Something simple — a `StatisticsCollector` that counts events by
type, or a `DangerMeter` that notices when your HP drops below 25%. Subscribe it in `Main`.

| Question | Your answer        |
|---|--------------------|
| How many **new** files? | 1                  |
| Did `Combat.java` change? | No                 |
| Did `EventBus.java` change? | No                 |
| Did any existing listener change? | No                 |
| Which files changed at all? | Add 1,changed Main |

**Paste `git diff --stat`:**

```Bash
git diff --stat
 docs/strategy-observer-clinic.md                   | 14 +++++------
 src/main/java/dungeonforge/Main.java               |  3 +++
 src/main/java/dungeonforge/events/DangerMeter.java | 27 ++++++++++++++++++++--
 3 files changed, 35 insertions(+), 9 deletions(-)
```

### Then the question that matters

`Combat` could simply have called `questTracker.onMonsterKilled(monster)` directly. That's one
line, it's obvious, and it needs no `EventBus`, no `GameEvent`, and no `GameEventListener` —
**three fewer classes.**

**Write a paragraph.** What does the direct call cost you that the bus does not? Give a
*concrete* scenario — a change somebody might ask for — where the direct-call version forces
you to edit `Combat` and the bus version does not.

> The direct call would work, but it would make Combat responsible for knowing about QuestTracker and anything else 
> that needs to react to combat events. For example, if we wanted to add the DangerMeter to warn the player when their 
> health drops below a certain amount, the direct-call version would require us to go back into Combat and add another 
> call specifically for DangerMeter. With the EventBus, Combat can just publish that damage was taken, and DangerMeter
> can listen for that event without Combat needing to know that DangerMeter even exists. This makes it easier to add 
> more listeners later without having to keep changing the Combat class.
> 


## D3 — The swap, demonstrated · 5 pts

**Run the game and find a line in the combat log like:**

```
Forge Golem changes tactics: aggressive -> skittish.
```

**Paste yours:**

```Bash
wight changes tactics: aggressive -> skittish
wight flees into the dark.
```

**Now answer:** at the moment that line was printed, what changed about the `Forge Golem`
object? Be precise. Its class? Its fields? Its identity? What *specifically* is different
about it one instruction later?


**Then add a fifth strategy** of your own invention. How many existing files did you have to
modify, and which?


## D4 — One honest question · 3 pts

**A prompt, because this one is worth surfacing now:** `CombatStrategy` and Week 8's `State`
pattern have almost identical UML — an interface, several implementations, an object that
holds one and delegates to it.

**Without looking ahead, guess:** what could possibly distinguish them? You are not expected
to be right. You're expected to have a hypothesis on record before Week 8 tells you.


**And anything else that's still unclear:**

