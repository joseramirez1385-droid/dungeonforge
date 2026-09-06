# Artifact Review Clinic — Lab 2, Part C

> **This is the only document you write this week.** Everything else — the epics, the
> stories, the acceptance criteria, the Definition of Done, the sprint plans — was written
> for you.
>
> Reading critically is a harder and more useful skill than writing from a blank page, and it
> is the one that will make your own stories good when you start writing them in Week 6.

Read all three before answering:
- `docs/backlog.md`
- `docs/definition-of-done.md`
- `docs/sprint-01-plan.md`

---

## C1 — Find the three planted flaws · 12 pts

There is **exactly one deliberate defect in each of the three documents**: one bad user
story, one unverifiable Definition-of-Done criterion, and one sprint-plan item that isn't
what it claims to be.

> **Hint for the story:** re-read INVEST first. The bad one fails more than one letter.
>
> **Hint for the DoD:** ask of every checkbox — *could two reasonable people disagree about
> whether this is true?* If yes, it isn't a criterion. It's an opinion.

### Flaw 1 — in `docs/backlog.md`

**Which item:** 
US-1.4

**What's wrong with it:**
**Which INVEST letter(s) it violates, and how:**

N: Violates negotiable. Tells developer to use a HashMap with double-locking
V: Subjective professional code!! subjective to the developer
T:No "More Professional code" is subjective

**My repaired version:**

Pick a real beneficiary and define in measurable terms what better code is.
The performance of this code increases by 5%.

```
As a ...,
I want ...,
so that ...

Acceptance Criteria
- Given ..., when ..., then ...
- Given ..., when ..., then ...
```

---

### Flaw 2 — in `docs/definition-of-done.md`

**Which checkbox:** 
"The Code is well written... checkbox..."

**Why it can't actually be checked:**
It is an opinion not an unambiguous check that can be verified by a machine.

**My replacement, phrased so that it can be:**
Every public class has a comment stating why it exists.
All code standards are verified, naming conventions. code blocks.


---

### Flaw 3 — in `docs/sprint-01-plan.md`

**Which item:** 
I will get busy this week.

**Why it isn't really what the document calls it:**
This risk can not be mitigated and will be a forever risk.

**My repaired version, including a mitigation someone could actually act on:**
Tuesday and Wednesday are unavailable, so 3 out of 8 points must be done by Monday

---

## C2 — Say what's good, and why · 9 pts

Pick the **three strongest user stories** in `docs/backlog.md`. For each, two or three
sentences.

> Praise is harder than criticism, and it's where most of the learning is. "It's clear" earns
> nothing. "Its third criterion names an observable output — the same object reference — so
> two people would always agree whether it passed" earns full marks.

### Strong story 1: ______ US-1.2

**INVEST letters it satisfies especially well:**
Testable: All 4 acceptance criteria can be checked by a machine
Valuable: The So that bug can be reproduced is very valuable.


**What specifically makes its acceptance criteria checkable:**
There are no opinion in the acceptance criteria, one is a boolean yes/no check
another is a existence or absence check. This is easily machine verifiable.

### Strong story 2: ______ US-1.1

**INVEST letters it satisfies especially well:**
Negotiable: States its needs, but leaves the implementation up to the developer.

**What specifically makes its acceptance criteria checkable:**
Its sets the player HP to 80 at the start of a new game. this is checkable by a machine

### Strong story 3: ______ SO.2

**INVEST letters it satisfies especially well:**
Small: 2 quick points and has 1 workflow file
Independent: the repo is the only thing that's needs to exist 

**What specifically makes its acceptance criteria checkable:**
It can be tested by a machine easily.

---

## C3 — Trace a story to code · 4 pts

Take **US-1.1** (settings live in one place). **Write no Java.** In plain English, describe
what you'd expect to see in the pull-request diff when this story is done, and which
acceptance criterion each piece satisfies.

| What I'd expect in the diff                             | Which acceptance criterion it satisfies |
|---------------------------------------------------------|-----------------------------------------|
| Having config.json already made.                        | It satisfies A1                         |
| having a config class                                   | It satisfies A2                         |
| having a class on the Gameconfig.getInstance()          | It Satisfies A3                         |
| having a Main class created for the start of a new game | It satusfies A3                         |

**One sentence: how did the acceptance criteria help you predict the shape of the work?**
acceptance criteria helped me predict that I would need to create one central configuration class 
that could load the settings and provide default values

---

## C4 — The bonus catch · up to +3 bonus

Once you have dealt with the bad story, something in `docs/sprint-01-plan.md` no longer adds
up the way it did.

**What is it:**

**What a real team would do about it in sprint planning:**

**What this suggests about the relationship between vague work and over-committed sprints:**

---

## C5 — One honest question

What is one thing about the Scrum process you still don't understand after this week? A good
question here is worth more to me than a confident wrong answer.
 
this is all new to me and a lot to take in, so I would say everything about the Scrum process is still 
an issue of not understanding it fully but the more I read it over the week I should catch on to it, so I don't get 
left behind.
