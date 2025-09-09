INSERT INTO appointment (description, name)
SELECT 'DESCRICAO 1', 'NOME 1'
    WHERE NOT EXISTS (
    SELECT 1 FROM appointment WHERE description = 'DESCRICAO 1' and name = 'NOME 1'
);