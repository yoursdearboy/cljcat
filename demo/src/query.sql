-- :name insert-event! :! :n
INSERT INTO log (event)
VALUES (:event)

-- :name select-events :n
SELECT * FROM log
