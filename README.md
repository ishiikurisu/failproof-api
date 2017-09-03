# Failproof checklists API

Let's get these lists for you. Just give us some time.

## Installation

To build this project, run the following Leiningen command:

```
lein uberjar
```

## Usage

Prior version 0.2.3, this API comes with the following methods for Java:

- `getLists`
    - Downloads an array of strings containing the tuple "title:code". This is the so called "id list".
- `getList`
    - Downloads the given checklist on the API format.
    - Parameter: a String with the machine code
- `toTitles`
    - Turns the id list into a list of titles.
    - Parameter (optional): the id list.
- `toLinks`
    - Turns the id list into a list of codes.
    - Parameter (optional): the id list.
- `getTitle`
    - Extracts the title of a checklist.
    - Parameter: the checklist in API format.
- `getItems`
    - Extracts the items of a checklist.
    - Parameter: the checklist in API format.
- `getChecks`
    - Extracts the checks of a checklist.
    - Parameter: the checklist in API format.
    - Returns an array of booleans, meaning `true` for checked items, and `false` for unchecked ones.
- `setCheck`
    - Sets a check in a checklist.
    - Parameter `checklist`: the checklist to be updated in API format.
    - Parameter `where`: the index of the check to be changed.
    - Parameter `what`: the value to assign to the check. `true` means to check it, and false, to uncheck it.
    - Returns a new checklist with the new check.

The same methods are also available for Clojure. `getLists` becomes `get-list`, `setCheck` becomes `set-check`, so on, so forth.

### API checklist format

Checklists will come in the following format when you download them.

```
Title
-Unchecked item
*Checked item
*Another checked item
-Another unchecked item
```
