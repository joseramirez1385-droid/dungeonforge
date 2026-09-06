# Git Dojo — my recovery notes

> Part D of Lab 2. For each drill: the command(s) you ran, **one sentence in your own
> words** on what it did, and one on when you would reach for it again.
>
> Graded on the sentences, not the commands. Commands can be copied; understanding cannot.

## The three trees — in my own words

| Tree | What lives here |
|---|---|
| Working Directory |  |
| Staging Area (Index) |  |
| HEAD |  |

---

## Drill 1 — Committed to `main` by accident

**Commands I ran:**
## echo "oops" > accident.txt
## git add accident.txt
## git commit -m "feat: work that should have been on a branch"

fixing it

## git switch -c fix/rescued-work
## git switch main
## git reset --hard origin/main

```
**What it did:**
We comminted to the main branch by accident. Then we fixed the issue by switching it to our local branch.

**When I would use it again:**
When there is an accidental commit to the main branch is when I will use it to fix it.

---

## Drill 2 — Wrong commit message / forgot a file

**Commands I ran:**

## echo "x" > note.txt && git add note.txt && git commit -m "asdf"
git commit --amend -m "docs: add note file"

```
**What it did:**
We wrote the wrong message and forgot to add a file.

**Why you must not do this to a commit you already pushed:**
Amending rewrites the history.

---

## Drill 3 — Committed a file that should be ignored

**Commands I ran:**
mkdir -p target && echo "junk" > target/Main.class
git add -f target/Main.class && git commit -m "chore: oops, committed build output"

Fixing it

git rm -r --cached target # stop tracking, keep the local files
echo "target/" >> .gitignore
git add .gitignore && git commit -m "chore: untrack build output and ignore target/"

```
**What it did:**
It commited a file that should have been ingored but wasn't

**Why adding it to `../.gitignore` alone was not enough:**
Since .gitignore only igores files git isn't already tracking you have to untrack it first then you can
add it to .gitignore.
---

## Drill 4 — Merge conflict

**Commands I ran:**
git switch main
git switch -c feature/a
printf '# DungeonForge - branch A title\n' > README.md
git commit -am "docs: title from branch A"
git switch main
git switch -c feature/b
printf '# DungeonForge - branch B title\n' > README.md
git commit -am "docs: title from branch B"
git switch main
git merge feature/a # clean
git merge feature/b # CONFLICT




```
**In the conflict markers, which side was "mine"?**

**What it did:**
we merge a conflict on our main branch with feature A and feature B that cause a conflict
that needed to fix.

**How I would back out of a merge I regretted starting:**
well for this one we went to the main branch README.md and correct the issue by merging the title to 
# DungeonForge - branch A title -Branch B title



---

## Drill 5 — "I destroyed everything"

**Commands I ran:**
git log --oneline # note the current hash
git reset --hard HEAD~3 # nuke the last three commits
git log --oneline # gone
git reflog # every position HEAD has held
git reset --hard <hash-from-before>

```
**What `git reflog` showed me:**
All previous logs from 90 days ago.

**One sentence on why this changes how nervous I should be about Git:**
Well like you said almost nothing is truely lost, everyone whos working on the main branch with you has
a copy of all the work you did so if you some how find a way to destory literaly git your buddy 
will have a back up and you cna continue from the last save.

---

## Stretch — Drill 6 (detached HEAD, interactive rebase)

**Notes:**

---

## The one command I want to remember from today
I would want to remember git reflog since it can pull up last 90 days that is very helpful to remember.

