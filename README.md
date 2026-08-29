# CST-323 Cloud Test Application — Contact Manager

Spring Boot test application used across CST-323 Topics 1–7 to validate deployment
onto Microsoft Azure, Heroku, Amazon AWS, and Google Cloud.

## Stack

| Layer | Technology |
|---|---|
| Language / runtime | Java 17 |
| Framework | Spring Boot 3.2.5 (Spring MVC, Spring Data JPA) |
| View | Thymeleaf 3 |
| UI | Bootstrap 5.3 (CDN) |
| Database | MySQL 8 |
| Logging | SLF4J API over Log4j 2 |
| Build | Maven |
| SCM | Git |

## Pages

| URL | Method | Purpose | CRUD |
|---|---|---|---|
| `/` | GET | Home / dashboard | — |
| `/contacts` | GET | List all contacts | Read |
| `/contacts/new` | GET | Blank contact form | Create (form) |
| `/contacts/{id}` | GET | Contact detail | Read |
| `/contacts/{id}/edit` | GET | Populated contact form | Update (form) |
| `/contacts/save` | POST | Insert or update a contact | Create / Update |
| `/contacts/{id}/delete` | POST | Remove a contact | Delete |

## Local setup

1. Install MySQL 8 and start it.
2. Create the schema and a user:

   ```sql
   SOURCE src/main/resources/db/schema.sql;
   CREATE USER 'cst323user'@'localhost' IDENTIFIED BY 'changeme';
   GRANT ALL PRIVILEGES ON cst323.* TO 'cst323user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. Optionally load sample rows: `SOURCE src/main/resources/db/seed-data.sql;`
4. Build and run:

   ```bash
   mvn clean package
   java -jar target/cloud-test-app.jar
   ```

5. Open http://localhost:8080

## Configuration

Connection settings read from environment variables so no credentials are committed:

| Variable | Default |
|---|---|
| `PORT` | `8080` |
| `DB_URL` | `jdbc:mysql://localhost:3306/cst323?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USER` | `cst323user` |
| `DB_PASSWORD` | `changeme` |

`spring.jpa.hibernate.ddl-auto=validate` — the schema is owned by `db/schema.sql`
so the identical DDL runs against every cloud provider's MySQL instance.
