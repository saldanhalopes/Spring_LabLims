# Lablims

Atualize sua conexão de banco de dados local em `application.yml` ou crie seu próprio arquivo `application-local.yml` para substituir
configurações para o desenvolvimento.

Durante o desenvolvimento é recomendado utilizar o perfil `local`. No IntelliJ `-spring.profiles.active=Local` pode ser
adicionado nas opções de VM da Configuração de Execução após habilitar esta propriedade em "Modificar opções".

Lombok deve ser suportado pelo seu IDE. Para IntelliJ, instale o plugin Lombok e habilite o processamento de anotações.

Usando um proxy, todo o aplicativo agora está acessível em `localhost:8080`. Todas as alterações nos modelos e JS/CSS
os arquivos ficam imediatamente visíveis no navegador.

## Build

O aplicativo pode ser construído usando o seguinte comando:

```
mvnw clean package
```

Inicie sua aplicação com o seguinte comando - aqui com o perfil `Prod`:

```
java -Dspring.profiles.active=Prod -jar ./target/Spring_LabLims.jar
```

Se necessário, uma imagem Docker pode ser criada com o plugin Spring Boot. Adicione `SPRING_PROFILES_ACTIVE=Prod` como
variável de ambiente ao executar o contêiner.

```
mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=br.com/lablims
```

## Further readings

* [Maven docs](https://maven.apache.org/guides/index.html)  
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)  
* [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)  
* [Thymeleaf docs](https://www.thymeleaf.org/documentation.html)  
* [Webpack concepts](https://webpack.js.org/concepts/)  
* [npm docs](https://docs.npmjs.com/)  
* [Bootstrap docs](https://getbootstrap.com/docs/5.3/getting-started/introduction/)  
* [Htmx in a nutshell](https://htmx.org/docs/)  
