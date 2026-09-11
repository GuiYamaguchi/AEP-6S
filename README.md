# CursoLIVRE

O CursoLIVRE busca expandir o acesso a educação para aqueles que não teriam a possibilidade de cursar uma faculdade ou curso profissionalizante, procurando facilitar a especialização para brasileiros. O projeto foi desenvolvido com base na ODS 4 – Educação de Qualidade, que visa a educação equitativa e inclusiva, tanto na educação primária, como na técnica.

O projeto utiliza a linguagem Java para a criação de classes junto da ferramenta Maven. O banco de dados utiliza a estrutura NoSQL (MongoDB) para armazenar cursos cadastrados. No projeto Java, foram inclusas as dependências:
 - Spring Web para criação de API Rest;
 - Spring Data MongoDB para a integração do banco de dados NoSQL ao projeto;
 - Lombok para simplificação do codigo;
 - JUnit para realização de testes automatizados;
 - Swagger para documentação do projeto.

 Pré-Requisitos:
 - Java 21 ou maior;
 - MongoDB.

## Como executar a API

### 1. Configurar e iniciar o MongoDB

A aplicação se conecta ao MongoDB usando a URI configurada em `cursoLivre/src/main/resources/application.properties`:

```properties
spring.mongodb.uri=mongodb://localhost:27017/cursoLivreDB
server.port=8080
```

Garanta que uma instância do MongoDB esteja rodando localmente na porta padrão `27017` antes de subir a aplicação. Algumas formas de fazer isso:

- **Instalação local**: inicie o serviço do MongoDB (`mongod`) normalmente na sua máquina.
- **Docker**:
  ```bash
  docker run -d --name mongo-cursolivre -p 27017:27017 mongo:latest
  ```

Caso queira usar outro host, porta, banco ou uma instância remota (ex: MongoDB Atlas), basta alterar o valor de `spring.mongodb.uri` no `application.properties` antes de rodar a aplicação.

### 2. Rodar a aplicação

Dentro da pasta `cursoLivre` (onde está o `pom.xml`), utilize o Maven Wrapper incluso no projeto:

```bash
cd cursoLivre

# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Ou, se preferir usar um Maven instalado globalmente:

```bash
mvn spring-boot:run
```

Por padrão, a API sobe em:

```
http://localhost:8080
```

A porta pode ser alterada modificando a propriedade `server.port` no `application.properties`.

### 3. Acessar a documentação da API (Swagger)

Com a aplicação rodando, a documentação interativa gerada pelo Swagger/OpenAPI (springdoc) fica disponível em:

- Swagger UI: `http://localhost:8080/swagger-ui.html` (ou `http://localhost:8080/swagger-ui/index.html`)
- Especificação OpenAPI (JSON): `http://localhost:8080/v3/api-docs`

## Como executar os testes

Os testes automatizados (JUnit) podem ser executados dentro da pasta `cursoLivre` com:

```bash
cd cursoLivre

# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

Ou com o Maven global:

```bash
mvn test
```

> Observação: alguns testes utilizam `spring-boot-starter-data-mongodb-test`, por isso é recomendável ter o MongoDB acessível (local ou via Docker, como descrito acima) antes de rodar a suíte de testes.

## Relatório de cobertura de testes (JaCoCo)

O projeto já está configurado com o plugin `jacoco-maven-plugin`, que gera automaticamente o relatório de cobertura ao final da fase `test` e valida se a cobertura mínima de 70% (linhas e instruções) foi atingida no projeto.

Para gerar o relatório, basta rodar os testes normalmente:

```bash
cd cursoLivre
./mvnw test
```

Ou, para também validar o limite mínimo de cobertura (70%):

```bash
./mvnw verify
```

Após a execução, o relatório HTML do JaCoCo é gerado em:

```
cursoLivre/target/site/jacoco/index.html
```

Basta abrir esse arquivo em um navegador para visualizar a cobertura de código detalhada por pacote e por classe.
