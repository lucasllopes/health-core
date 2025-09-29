Para executar a aplicacao é necessário possuir o docker instalado. Clone o projeto, acesse o diretório onde está localizado o arquivo Dockerfile e docker-compose.yml e execute o seguinte comando:

docker compose up --build

( Em alguns Sistemas Operacionais pode ser necessário utilizar "docker compose" ao invés de "docker-compose" ): docker compose -f docker-compose-local.yml up -d

Para verificar as filas no rabbitmq acesse o link http://localhost:15672/ (usuario: user senha: 123456)