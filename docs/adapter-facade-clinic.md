# The Adapter / Facade Clinic — Week 12, Part D

Fifteen points. Graded on **measurement and precision**, not prose. The rule from Week 9 still
holds: **a number without the command that produced it earns half marks.**

---

## D1 — Three classes with the same shape · 5 pts 🔎

**This is the highest-value step in the clinic, and it is the most common patterns-interview
question in existence.** You have written all three answers.

**Step 1.** Put these three declarations in a table, from your own repo, with file and line:

```java
class WeaponDecorator        implements __ Weapon__  { ___ Weapon___ wrapped; }   // Week 9
class LegacyBestiaryAdapter  implements __MonsterSource____  { __LegacyBestiary____ adaptee; }   // this week
class GameSession                            // implements nothing        // this week
```

**Step 2.** For the first two, answer one question: **is the held type the same as the
implemented type?**

| | implements | holds | same type? |
|---|------------|---|------------|
| `WeaponDecorator` | weapon     | weapon | yes        |
| `LegacyBestiaryAdapter` |    MonsterSource        | LegacyBestiary|     No        |

**Step 3.** That answer is the whole distinction. Write it as one sentence beginning *"A
decorator implements the same interface as the thing it holds, which is why…"*

> ____A decorator implements the same interface as the thing it holds, which is why decorators can stack, while an 
> adapter holds an object with a different interface and translates it into the interface the client expects.__

**Step 4 — the consequence, not the definition.** `Flaming(Frost(sword))` is a legal weapon.
Write down what `new LegacyBestiaryAdapter(new LegacyBestiaryAdapter(b))` does.

**Try it. Paste the compiler error.**

```
Error: incompatible types: dungeonforge.legacy.LegacyBestiaryAdapter cannot be converted to 
dungeonforge.legacy.LegacyBestiary
```

**Now say why that error is a property of the pattern and not a limitation of your code.**


**Step 5.** `GameSession` holds eight things and implements nothing. In one sentence, what makes
it a facade rather than "a class with a lot of fields"?

GameSession is a facade because it provides simple methods that coordinate the game’s setup and turn sequence,
so callers do not have to manage those collaborating objects themselves.

---

## D2 — The missing column · 3 pts

**Step 1.** Name the three honest options for a target field with no source. __Refuse the record it, derive a value,
default it

**Step 2.** You took one. Paste the method.

```java
private int experienceFor(int hp, int attack, String legacyId) {
        notes.add(legacyId + ": xp derived (no xp column in the export)");
        return Math.max(1, hp / 2 + attack);
    }
```

**Step 3.** Now read Head First p. 249 — `EnumerationIterator.remove()`. **Which of your three
options did Freeman & Robson take, and why was theirs available when yours was not?**


**Step 4.** Try `Arrays.asList("a","b").add("c")`. **Paste the exception.** Which option is that?

[ERROR]   AdapterFacadeTest.temporarilyTryUnsupportedAdd:341 » UnsupportedOperation

**Step 5 — the one that is worth the points.** A colleague changes your derivation from
`hp / 2 + attack` to `hp / 3 + attack`. Every test still passes.

**Is that a problem? Answer yes or no and defend it in two sentences.**
Yes, because changing the formula changes monster rewards even though the tests still pass. The tests need an 
assertion for the agreed XP formula so an accidental change is caught.

---

## D3 — The listener nobody subscribed · 4 pts 🔨

**Step 1.** Before US-9.2, how many times did `DamageMeter` appear anywhere outside its own
file? ______ **Command:** ______

**Step 2.** It compiled, it was correct, and it had never run. **Name every mechanism in this
project that could have caught it, and say why each one did not:**

| Mechanism | Why it did not catch this                                         |
|---|-------------------------------------------------------------------|
| the Java compiler | An unused class is valid, does not require it to be instantiated. |
| the 137-test suite |    every test asserts a class's behavior; none asks "is every listener wired?" |
| code review | the diff that added `DamageMeter` was correct — the omission was in a different file |
| the acceptance-criteria harness | it checks source content, and there was no criterion about the listener list   |

**Step 3 — the experiment.** In your finished build, comment out `bus.subscribe(meter)` in
`GameSession.start()` and run `mvn test`.

**How many tests fail?** ___2___ **Name them.** ____theDamageMeterActuallyFiresNow, 
theSessionSubscribesTheListenerThatNobodyEverWiredUp__

**Put the line back.**

**Step 4.** You just demonstrated the difference between the old wiring and the new wiring, and
it is **not** that the new one cannot be wrong. State what it actually is, in one sentence.

The new wiring can still be wrong, but it is assembled in one known place where tests can detect a missing subscription.


**Step 5.** A count of `bus.subscribe` calls is not evidence that a listener works. Which of your
acceptance criteria *is* that evidence, and what makes it different in kind?

AC3. It publishes a DAMAGE_TAKEN event and looks for the warning in the transcript. Different in kind because it checks 
a behaviour (the listener reacted), not a count (something was in a list).

---

## D4 — What each pattern cost · 3 pts

**Step 1.** Paste `git diff --stat` for your US-9.1 commit and your US-9.2 commit.

```
US-9.1   MonsterSource.java         +23
         LegacyBestiaryAdapter.java +142
         Main.java                  +1

US-9.2   GameSession.java           +219
         Main.java                 191 -> 123   (-68)
         GameWorld.java            128 -> 145   (+17)

Adapter's diff is almost all additions. Facade's has a big deletion column.
```

**Step 2.** `GameSession.start()` builds eight objects and a dungeon, every time it is called.
**Write a test that needs only a `Player` and an `EventBus`.** Can you still do it?

**What does that tell you about the cost of a front door?**

```
Yes, new Player(...) and new EventBus() still work; the facade hid nothing. What you cannot get is a half-built session.
The cost of a front door is that it only opens all the way.
```

**Step 3.** The Principle of Least Knowledge says `Main` should not have to know eight classes.
It now knows one. **Name one diagnostic or debugging task that got harder**, specifically.

```
Debugging the assembly itself. A failure now surfaces inside start(), after all eight objects exist, so you can no
 longer bisect the wiring by commenting out lines in main().
```

**Step 4 — the sentence to take into the final.** Adapter and Facade are both "a class standing
in front of other classes." In one sentence each, from having written both:

| Pattern | What it is actually for |
|---|---|
| Adapter |Converts an existing interface into the interface the client expects. |
| Facade | Provides a simpler entry point that coordinates several collaborating classes.|

**And: which of the two did your codebase genuinely need, and which was mostly tidiness?**
Defend the answer with a number from Part 3.

```
My codebase needed the Facade most because the turn sequence existed in three places before and now exists in one, 
preventing the script and interactive paths from behaving differently. Adapter provided a clean boundary for the legacy
 format, keeping CSV knowledge out of MonsterFactory, which still had zero legacy-format references.
```

---

## Turning it in

Commit `docs/adapter-facade-clinic.md` with D1–D4 complete, the compiler error in D1 Step 4
pasted verbatim, the exception in D2 Step 4 pasted verbatim, and `bus.subscribe(meter)` **put
back** in `GameSession`.
