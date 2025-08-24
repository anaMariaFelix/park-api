--usados para preencher a tabela e ficar mais facil de trabalhar nos testes
--isso é um arquivo, que foi criado dentro de dois diretorios a parti do diretorio resouces.  resouces -> sql -> usuarios -> file(usuarios-insert.sql)

--sem segurança
INSERT INTO usuarios(id,userName,password,role) values (100,'ana@email.com','$2a$12$LzFUJnNJXXOuMZbvVbDv1u5RWGT5uICk.vjx2aYV1bVk67tQvuikS','ROLE_ADMIN');
INSERT INTO usuarios(id,userName,password,role) values (101,'maria@email.com','$2a$12$LzFUJnNJXXOuMZbvVbDv1u5RWGT5uICk.vjx2aYV1bVk67tQvuikS','ROLE_CLIENTE');
INSERT INTO usuarios(id,userName,password,role) values (102,'sid@email.com','$2a$12$LzFUJnNJXXOuMZbvVbDv1u5RWGT5uICk.vjx2aYV1bVk67tQvuikS','ROLE_CLIENTE');
INSERT INTO usuarios(id,userName,password,role) values (103,'wilker@email.com','$2a$12$LzFUJnNJXXOuMZbvVbDv1u5RWGT5uICk.vjx2aYV1bVk67tQvuikS','ROLE_CLIENTE');


--com segurança
INSERT INTO clientes(id,nome,cpf,id_usuario) values (10,'maria alves','61056769050','101');
INSERT INTO clientes(id,nome,cpf,id_usuario) values (11,'sidcley','22401480048','102');
