# Failproof checklists API

Let's get these lists for you. Just give us some time.

## Installation

To build this project, run the following Leiningen command:

```
lein uberjar
```

## Usage

Prior version 0.2.0, this API comes with the following methods:

- `getLists`
    - Downloads an array of strings containing the tuple "title:code". This is the so called "id list".
- `getList`
    - Downloads the given checklist on the API format.
    - Parameter: a String with the machine code
- `toLists`
    - Turns the id list into a list of titles.
    - Parameter (optional): the id list.
- `toLinks`
    - Turns the id list into a list of codes.
    - Parameter (optional): the id list.


### API checklist format

Checklists will come in the following format when you download them.

```
Title
-Unchecked item
*Checked item
*Another checked item
-Another unchecked item
```
