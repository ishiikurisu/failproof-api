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

Customization can be done by setting the following
environment variables:

| variable name | default value | action |
|----|----|----|
| `SALT` | `SALT` | string used as seed for cryptographic procedures |
| `JDBC_DATABASE_URL` | `jdbc:postgresql://localhost:5433/fpcl?user=fpcl&password=password` | the database the application connects to. The default string connects to the `docker compose` database. |

Check the [routes doc](./docs/routes.md) for usage.
