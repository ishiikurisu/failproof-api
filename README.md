# Failproof Checklists API

Let's get these lists for you!

## Installation

To build this project, run the following command:

```
bundle install
make
```

To unit tests, run the following command:

```
make test
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
- `/sync`
  - Sync the notes from user
  - Method: `POST`
  - Parameters:
    - `auth_key`: `string`
    - `notes`: Markdown or `null`
    - `last_updated`: timestamp
  - Returns:
    - `notes`: the most up to date version of the notes
    - `timestamp`: the date of the last update
- `/export`
  - Exports database
  - Method: `POST`
  - Parameters:
    - `auth_key`: `string`
  - Returns:
    - `database`: `string` or `null`
- `/import`
  - Imports database
  - Method: `POST`
  - Parameters:
    - `auth_key`: `string`
    - `database`: `string`
  - Returns:
    - `error`: `string` or `null`

# Acknoledgements

This project is possible due to:

- [Ruby PG](https://rubydoc.info/gems/pg/1.2.3)
- [Sinatra](http://sinatrarb.com/)
- [cyu/rack-cors](https://github.com/cyu/rack-cors)
