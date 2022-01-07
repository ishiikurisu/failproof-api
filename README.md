# Failproof Checklists API

Let's get these lists for you!

## Installation

To install dependencies and execute this project, run the following command:

```
lein run
```

To unit tests, run the following command:

```
lein test
```

To build this project, run the following command:
```
lein uberjar
```

## Usage

The API comes with the following HTTP methods:

- `/users/create`
  - Creates new user
  - Method: `POST`
  - Parameters:
    - `username`: `string`
    - `password`: `string`
    - `notes`: Markdown or `null`
  - Returns:
    - `auth_key`: `string`
- `/users/auth`
  - Authorizes users to app
  - Method: `POST`
  - Parameters:
    - `username`: `string`
    - `password`: `string`
  - Returns:
    - `auth_key`: `string`
    - `notes`: Markdown or `null`
- `/users/password`
  - Allows users to change password
  - Method: `POST`
  - Parameters:
    - `auth_key`: `string`
    - `old_password`: `string`
    - `new_password`: `string`
  - Returns:
    - `error`: `string` or `null`
- `/notes`
  - Obtains the notes for a given user
  - Method: `GET`
    - Parameters:
      - `auth_key`: `string`
    - Returns:
      - `notes`: Markdown or `null`
- `/notes`
  - Updates the notes from user
  - Method: `POST`
  - Parameters:
    - `auth_key`: `string`
    - `notes`: Markdown or `null`
  - Returns:
    - `error`: `string` or `null`
- `/backup`
  - Exports database
  - Method: `GET`
  - Parameters:
    - `auth_key`: `string`
  - Returns:
    - `database`: `string` or `null`
- `/backup`
  - Imports database
  - Method: `POST`
  - Parameters:
    - `auth_key`: `string`
    - `database`: `string`
  - Returns:
    - `error`: `string` or `null`
