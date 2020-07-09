# Failproof Checklists API

Let's get these lists for you. Just give us some time.

## Installation

To build this project, run the following command:

```
bundle install
make
```

## Usage

The API comes with the following HTTP methods:

- `/users/create`
  - Method: `POST`
  - Parameters:
    - `username`: `string`
    - `password`: `string`
    - `notes`: Markdown or `null`
  - Returns:
    - `auth_key`: `string`
- `/users/auth`
  - Method: `POST`
  - Parameters:
    - `username`: `string`
    - `password`: `string`
  - Returns:
    - `auth_key`: `string`
    - `notes`: Markdown or `null`
- `/notes/create`
- `/notes/read`
- `/notes/update`
- `/notes/update`

