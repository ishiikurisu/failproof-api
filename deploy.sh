#!/bin/sh
ruby tasks.rb e db.jsonl
ruby tasks.rb r
git push heroku main -f
ruby tasks.rb i db.jsonl
