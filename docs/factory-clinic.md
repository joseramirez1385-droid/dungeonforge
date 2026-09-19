# The "Which Factory?" Clinic — Lab 4, Part D

> Week 3's hard part was refusing a pattern. **This week's hard part is telling three very
> similar patterns apart.** Students who leave Week 4 unable to distinguish them will misuse
> all three for the rest of the semester — and Exam 1 will ask.

## D1 — The experiment: what does a fourth theme cost? · 8 pts

The Abstract Factory's whole claim is *"adding a new family is cheap and touches nothing
else."* Claims like that should be measured, not believed.

**Add a fourth theme.** Anything you like — Fungal, Drowned, Clockwork. It needs a kit class,
a couple of monster blueprints in `monsters.json`, and loot.

Before you start, **commit your current work** so `git diff --stat` is meaningful.

| Question | Your answer                                    |
|---|------------------------------------------------|
| How many **new** files did you create? | 1                                              |
| How many **existing** files did you modify? | 3                                              |
| Which existing files? | ThemeRegistry.java, config.json, monsters.json |
| Did `GameWorld.java` change? | No                                             |
| Did any `RoomPopulator` subclass change? | No                                             |
| Did `Monster`, `Room`, or `DungeonLevel` change? | No                                             |

**Paste the output of `git diff --stat`:**

```Bash
$ git diff --stat
 docs/factory-clinic.md                             | 16 ++++++-------
 .../java/dungeonforge/factory/ThemeRegistry.java   |  1 +
 .../dungeonforge/factory/WinterfellThemeKit.java   | 26 +++++++++++-----------
 src/main/resources/data/config.json                |  4 ++--
 src/main/resources/data/monsters.json              |  6 ++++-
 5 files changed, 29 insertions(+), 24 deletions(-)
```

**In two or three sentences: what does that number tell you about the Open/Closed
Principle — "open for extension, closed for modification"? Was it satisfied, and how do you
know from evidence rather than from a definition?**

I think the Open/Closed Principle was mostly satisfied because I only had to modify 3 existing files while 
adding the new functionality. I also did not have to change important existing classes like GameWorld, 
Monster, Room, DungeonLevel, or any RoomPopulator subclasses, which shows that the new functionality could be 
added without changing the main parts of the existing design.



> Set `dungeonDepth` to 4 in `config.json` and run it, so you can see your fourth theme.
> Then set it back to 3 before you open the PR.

## D2 — Classification · 12 pts

For each scenario: which of the three applies? Answer **Simple Factory**, **Factory Method**,
**Abstract Factory**, or **none of them** — and give a one-sentence reason.

| # | Scenario | Which?      | Why |
|---|---|-------------|---|
| 1 | One place in the code turns a monster id string into a `Monster`, so `new Monster` appears once | Simple Factory |the creation of Monster objects is handled in one place based on the monster ID.|
| 2 | A boss room, a treasure room and an ordinary room each fill themselves differently, but always in the same order: prose, then monsters, then a chest | Factory Method | each room type can create its contents differently while still following the same overall process|
| 3 | An ice level must contain ice monsters AND ice loot AND ice prose, never a mix | Abstract Factory | it creates a family of related ice-themed objects that are meant to work together.|
| 4 | Week 9: a weapon can be made flaming, then vampiric, then blessed, in any combination | None        |the weapon is combining different effects rather than using a factory to create different objects. |
| 5 | Week 12: save files must be written as JSON now and possibly as XML later, with matched reader and writer | Abstract Facotry |it creates a matching family of reader and writer objects for each file format. |
| 6 | A method returns a `Player` object, built from the name typed at startup | None |because having one shared GameConfig instance describes the Singleton Pattern, not a factory pattern. |

> Scenarios 4 and 6 are traps. One is a different pattern entirely; the other is not a pattern
> at all. Say so if you think so — "none of them" is a correct answer to at least one row.

## D3 — The distinction, in your own words · 5 pts

**Simple Factory is not one of the Gang of Four patterns.** Your textbook says so explicitly
before it teaches Factory Method.

**In three or four sentences: what can Factory Method do that Simple Factory cannot?** Do not
define either one. Describe a change someone might ask you to make, and explain why it would
be easy with one and awkward with the other.
If we were asked to add a California-style Pizza Store, Factory Method would make it easier 
because we could create a new California store that decides which pizzas it makes. With a Simple Factory, 
we would have to go back into the existing factory and change it to recognize and create the new California pizzas. 
Factory Method makes this change easier because a new store can be added 
without having to keep modifying one central factory.

## D4 — One honest question

What is still blurry about these three patterns? A specific confusion is worth more to me
than a confident summary.

What is still a little blurry to me is knowing when to use Factory Method instead of Abstract Factory. 
I understand the examples when they are explained, but when I first read a scenario, I sometimes have trouble 
telling whether it is creating one type of object differently or creating a family of related objects.

