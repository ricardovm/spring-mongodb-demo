# Projeto spring-mongodb-demo

Bem-vindo o spring-mongodb-demo. Este é um projeto de estudo que demonstra o uso de 
MongoDB em uma aplicação Spring Boot.

## Execução

### Executando com Docker

Você vai precisar apenas de docker e docker-compose atualizados.

Para execução local, execute na raiz do projeto::

```shell script
docker-compose up
```

Isso vai gerar a imagem da aplicação à partir dos fonter e executar junto com o MongoDB

### Executando a aplicação localmente

Para execução local, você vai precisar do MongoDB em execução (pode ser via docker) e 
o Java 17 configurado no PATH:

```shell script
docker run -d --rm -p 27017:27017 mongo
./mvnw spring-boot:run
```

### Execução dos testes

Você vai precisar de docker e Java 17 para execução dos testes:

```shell script
 ./mvnw verify
```

## Features

Uma documentação básica dos endpoints está disponível em http://localhost:8080/swagger-ui/index.html

## Tecnologias

A linguagem utilizada neste projero é Java, em sua versão 17. Também se utiliza o MongoDB
como banco de dados.

E juntando essas duas tecnologias, além de prover o servidor com a API está o Spring Boot.

## Camadas

O código é dividido em 4 camadas principais. Esta estrutura permite uma fácil evolução da
aplicação sem aumentar desnecessariamente a complexidade.

1. `domain`: classes do domínio da aplicação. Pretende-se que o código (não as anotações)
   não tenha dependência com o framework, mas deve conter toda a estrutura básica da
   aplicação. `domain` não acessa diretamente nenhuma outra camada
2. `usecase`: a camada com os casos de uso disponíveis para aplicação, onde cada operação
   é um caso de uso específico
3. `app`: a camada externa acessada pelos clientes da aplicação, neste caso, apenas através 
   da sua API REST. Basicamente os endpoints e classes DTO que acessam o domínio e casos
   de uso para carregar e processar informações
4. `external`: camada com acessos externos à aplicação, onde está a implementação do 
   repositório com acesso à base de dados
