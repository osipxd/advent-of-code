#!/usr/bin/env bash

# Bash strict mode (Google it)
set -euo pipefail

# Yes, I've not implemented input parsing because that's not the point.
# The point is to use Git and little of bash to solve the puzzle.
# Convert vertical stacks to horizontal (see Day05_input_reformat.gif to learn how) and
# PUT CONVERTED INPUT HERE 👇
stacks="
ZN
MCD
P
"
# END OF INPUT

stacks=$(echo "$stacks" | sed '1d ; $d') # Remove first and last line break from the input
n=$(echo "$stacks" | grep -c '^')        # Remember number of stacks

# Make this variable empty if you want to see git commands output
quiet='-q'

function main() {
  comment "Hello!"
  comment "I am the CrateMover 9001"
  comment "Powered by Git and Bash"
  comment "Let's start!"
  init-repo

  comment "Crates loaded."
  comment "Let's move them!"
  # PUT COMMANDS FROM INPUT HERE 👇
  move 1 from 2 to 1
  move 3 from 1 to 3
  move 2 from 2 to 1
  move 1 from 1 to 2
  # END OF COMMANDS

  comment "Done!"
  print-answer
}

function init-repo() {
  # Prepare directory for repo
  local dir='aoc-day5-repo-tmp'
  rm -rf "$dir"
  mkdir "$dir"
  cd "$dir" # And change working directory

  # Init empty repo with branch named `root`
  git -c init.defaultBranch=root init $quiet

  # Yes I can define functions, but I want to use maximum of git and minimum of bash, so
  # let's add some aliases to imitate stack API
  git config alias.offer "commit $quiet --allow-empty --message" # git offer A      - adds empty commit with message "A"
  git config alias.take "cherry-pick --allow-empty"              # git take 1~N..1  - takes last N commits from branch "1"
  git config alias.pop-up "branch $quiet --force"                # git pop-up 1 1~N - removes last N commits from branch "1"
  git config alias.peek "log --pretty=format:'%s' -1"            # git peek 1       - prints the last commit from branch "1"
  git config alias.peek-all "log --pretty=format:'%s' --reverse" # git print-all 1  - prints all commits from branch "1"

  # Add initial commit, this commit will be the root commit for all other branches
  tgit offer root

  # And then build all branches using input stacks
  local i=0
  for stack in $stacks; do
    i=$((i + 1))
    build-branch "$i" "$stack"
  done
}

function build-branch() {
  local name=$1
  local stack=$2
  local i

  # Create new branch
  tgit checkout $quiet -b "$name" root

  # Add commits from the given stack
  for ((i = 0; i < ${#stack}; i++)); do
    tgit offer "${stack:$i:1}"
  done
}

# This is the main feature of this script! Define function in form of commands from input
# Usage: move 1 from 2 to 1
function move() {
  local count=$1
  local from=$3
  local to=$5

  tgit checkout $quiet "$to"
  tgit take "$from~$count..$from"
  tgit pop-up "$from" "$from~$count"
}

function print-answer() {
  local answer=''
  local i
  for ((i = 1; i <= n; i++)); do
    answer+=$(git peek "$i")
  done

  answer=$(echo "$answer" | tr -d '\n')
  echo
  echo -e "${C_GRAY}Answer: ${RESET}$answer"
  echo -e "${C_GRAY}This star for you - ${F_BOLD}${C_YELLOW}*${RESET}"
}

### Functions for visualisation

RESET="\033[0m"
F_BOLD="\033[1m"
F_BOLD_RESET="\033[22m"
C_GREEN="\033[32m"
C_GRAY="\033[37m"
C_YELLOW='\033[1;33m'

# If you don't need visualisation, replace usage of tgit with git
# t - for tracked
function tgit() {
  git "$@" >/dev/null
  alias=$(git config --local alias."$1" | sed -E "s/(.+)/($1 -> \1)/" | tr -d '\n' || true)
  track "${C_GREEN}${F_BOLD}git $F_BOLD_RESET$* $C_GRAY$alias$RESET"
}

function comment() {
  track "${C_GRAY}# $1${RESET}"
}

# Prints current branches state and latest command
function track() {
  local message=$1

  dump=$(dump-branches)
  clear
  echo -e "> $message"$'\n'"$dump"
  sleep 0.1
}

function dump-branches() {
  local output=''
  local branches=$(git branch --format='%(refname:short)' | sed '$d')

  for branch in $branches; do
    output+=$'\n'$(git log --pretty='format:%s' --reverse "$branch" |
      sed "s/root/$branch/" |
      tr -d '\n' |
      sed "s/\(.\)/[\1] /g ; s/]/] |/ ; s/$/\n/")
  done

  echo "$output"
}

main
