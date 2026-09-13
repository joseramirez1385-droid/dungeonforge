# The Singleton Audit — Lab 3, Part C

> **The hard part of Singleton week is not writing one. It is 12 lines of code.**
> The hard part is knowing when *not* to.
>
> Singleton is the most over-applied pattern in the book. A student who leaves this week able
> to write one has learned the easy half. A student who leaves able to *refuse* to write one
> has learned the half that matters.

Below are **eight** candidate classes from DungeonForge's future. Three you have already met;
five arrive in Weeks 4 to 15. For each, decide: **Singleton, or not?**

Answer with the test we will use all semester:

> **Would a second instance be a BUG, or merely unusual?**
>
> If two instances would produce *incorrect behaviour* — not just wasted memory, not just
> inconvenience — the class may deserve to be a Singleton.
> If two instances would merely be *odd*, it is a dependency, and you should pass it in.

Fill in every row. Two of the eight are genuine singletons; you already know which, because
you built them this week. Your job is to defend the other six answers.

| # | Class | What it does | Singleton? | Would a 2nd instance be a bug, or just unusual? Why? |
|---|---|---|------------|------------------------------------------------------|
| 1 | `GameConfig` | Holds every tunable setting | x          |                                                      |
| 2 | `RandomSource` | The one seeded RNG | x          |                                                      |
| 3 | `Player` | The player character |            | x                                                    |
| 4 | `MonsterFactory` (Wk 4) | Turns blueprints into monsters |            | x                                                    |
| 5 | `EventBus` (Wk 5) | Publishes game events to subscribers | x          |                                                      |
| 6 | `CommandHistory` (Wk 7) | The undo stack |            | x                                                    |
| 7 | `SaveSystemFacade` (Wk 12) | Reads and writes save files |            | x                                                    |
| 8 | `Logger` | Writes diagnostic output to a file | x          |                                                      |

## The three that will cause arguments

Rows 5, 7 and 8 are the interesting ones, and reasonable engineers disagree about all three.
Pick **one** of them and write a paragraph:

**Which one:** #7 'SaveSystemFacade'

**The case FOR making it a Singleton:**
Make it easier to control the save system and help prevent different instances 
from trying to access or change the same save file at the same time.

**The case AGAINST:**
The case I would make against it is What if later in the game the player needs 
multiple save slots, an autosave system, or different save locations?

**What you would actually do in this project, and why:**
I would not make SaveSystemFacade a Singleton. I would rather have the option to use 
multiple save system objects so I could separate things like the main save and 
backup saves that way if something went wrong with the main save file, I would want the game 
to have a backup of the player's progress that it could fall back on.

> There is no answer key for this paragraph. You are graded on whether you engaged with the
> tension, not on which side you landed.

## One more question

Your `GameConfig` has a method called `resetForTests()`. It exists only so that tests can
undo the global state that the Singleton created.

**In one or two sentences: what is that method telling you about the pattern?**
I think the problem is that one test can change GameConfig and accidentally affect another test 
because they are all using the same instance.

