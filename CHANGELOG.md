# Change Log

# 0.3.0:

- add organisations recursive support
  The only limit is rate-limiting

example: if user give foo, bar as organisations and foo contain 5 repo and bar 2,
 doghub will go through all the 5 repo of foo searching for stale issue/prs and to the same later for bar

(if rate-limiting doesn't prevent it. But every hour it will restored)

# 0.2.0:

## Features:

add posibility to check staled Pull-Request, togheter with Issues.

Right now the bot comment automatically PRs and Issue. 

- add logging and try/catch

- improve layout

# 0.1.0

Add initial structure. Implement old issue check
