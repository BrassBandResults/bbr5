# Extract
There is a need to extract the contest event data out to github, so there is an immutable list of changes recorded along with when they happened.  There are two ways this process happens, one to take a full extract, and one to just extract recent changes.

## Full Extract

## Recent Changes
The process for recent changes is to do the following:
- Checkout existing git repo to get current data
- look for any results with a last changed date in the last week
- export those contest events in full, overwriting the ones checked out from git earlier
- Add all files
- Commit to git
- Push to github