# SpringExample
curl -v localhost:8080/users                                                                                        -- список всех юзеров

curl -v localhost:8080/users/1                                                                                      -- конкретный юзер с id 1

curl -X POST localhost:8080/users -H 'Content-type:application/json' -d '{"name": "Samwise Gamgee", "points": 93}'  -- добавить юзера

curl -X PUT localhost:8080/users/3 -H 'Content-type:application/json' -d '{"name": "Samwise Gamgee", "points": 95}' -- поменять юзера с id 3

curl -X DELETE localhost:8080/users/3                                                                               -- удалить юзера с id 3

curl -v localhost:8080/random                                                                                       -- список всех юзеров
