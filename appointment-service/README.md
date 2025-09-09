Para executar a aplicacao é necessário possuir o docker instalado. Clone o projeto, acesse o diretório onde está localizado o arquivo Dockerfile e docker-compose.yml e execute o seguinte comando:

docker compose up --build

Para executar somente o banco de dados no docker utilize o seguinte comando:

docker-compose -f docker-compose-local.yml up -d

( Em alguns Sistemas Operacionais pode ser necessário utilizar "docker compose" ao invés de "docker-compose" ): docker compose -f docker-compose-local.yml up -d


Para habilitar o debug remoto no intellij ir em edit configurations -> clicar no "+" -> Remote JVM Debug -> Host: localhost Porta: 5005 -> clicar em "Apply" -> Rodar normalmente