# Get github issues / PR for a release
# Exec with "python github_release_notes.py YOUR_GITHUB_API_ACCESS_TOKEN 4.7.1"

from github import Github
from collections import Counter
import sys

TARGETS = ['csharp', 'cpp', 'go', 'java', 'javascript', 'python2', 'python3', 'swift', 'php', 'dart']

TOKEN=sys.argv[1]
MILESTONE=sys.argv[2]
g = Github(login_or_token=TOKEN)

# Then play with your Github objects:
org = g.get_organization("antlr")
repo = org.get_repo("antlr4")
milestone = [x for x in repo.get_milestones() if x.title==MILESTONE]
milestone = milestone[0]

issues = repo.get_issues(state="closed", milestone=milestone, sort="created", direction="desc")

# dump bugs fixed
print()
print("## Issues fixed")
for x in issues:
    labels = [l.name for l in x.labels]
    if x.pull_request is None and not ("type:improvement" in labels or "type:feature" in labels):
        print("* [%s](%s) (%s)" % (x.title, x.html_url, ", ".join([l.name for l in x.labels])))

# dump improvements closed for this release (issues or pulls)
print()
print("## Improvements, features")
for x in issues:
    labels = [l.name for l in x.labels]
    if ("type:improvement" in labels or "type:feature" in labels):
        print("* [%s](%s) (%s)" % (x.title, x.html_url, ", ".join(labels)))

# dump PRs closed for this release by target
print()
print("## Pull requests grouped by target")
for target in TARGETS:
    print()
    print(f"### {target} target")
    for x in issues:
        labels = [l.name for l in x.labels]
        if x.pull_request is not None and f"target:{target}" in labels:
            print("* [%s](%s) (%s)" % (x.title, x.html_url, ", ".join(labels)))

# dump contributors
print()
print("## Contributors")
user_counts = Counter([x.user.login for x in issues])
users = {x.user.login:x.user for x in issues}
for login,count in user_counts.most_common(10000):
    name = users[login].name
    logins = f" ({users[login].login})"
    if name is None:
        name = users[login].login
        logins = ""
    print(f"* {count:3d} items: [{name}]({users[login].html_url}){logins}")
