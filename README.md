# Film Finder

## Descripción

Film Finder es un servicio backend RESTful desarrollado como parte de la práctica 2 de Desarrollo de Aplicaciones Móviles N en ESCOM. Esta API proporciona endpoints para la gestión de usuarios, búsqueda de películas, almacenamiento de favoritos y seguimiento del historial de búsqueda. Sirve como backend para la aplicación móvil [Film Finder Android](https://github.com/sammmcv/FilmFinderAndroid).

## Características

* **Autenticación de Usuarios:** Registro e inicio de sesión con contraseñas encriptadas.
* **Gestión de Usuarios:** Creación, actualización de roles y eliminación de cuentas.
* **Búsqueda de Películas:** Integración con API externa de películas.
* **Gestión de Favoritos:** Guardar y eliminar películas favoritas por usuario.
* **Historial de Búsqueda:** Seguimiento de términos de búsqueda por usuario.
* **Panel de Administración:** Interfaz web para gestionar usuarios y ver estadísticas.

## Tecnologías Utilizadas

* **Lenguaje:** Java
* **Framework:** Spring Boot
* **Base de Datos:** JPA/Hibernate
* **Seguridad:** Spring Security con BCrypt
* **Frontend:** Thymeleaf para las vistas web
* **API Externa:** Integración con API de películas

## Configuración y Ejecución

1. **Clonar el Repositorio:**
   ```bash
   git clone https://github.com/sammmcv/FilmFinder
   ```
2. **Configurar la Base de Datos:**
   - Asegúrate de tener MySQL instalado y ejecutándose.
   - Crea una base de datos llamada `crud` (o la que definas en `application.properties`).
   - Actualiza el usuario y contraseña en `src/main/resources/application.properties` si es necesario.

3. **Configurar Propiedades de la Aplicación:**
   - Edita `src/main/resources/application.properties` para ajustar la conexión a la base de datos y credenciales OAuth2 si vas a usar Google Login.

4. **Construir y Ejecutar el Proyecto:**
   - Desde la raíz del proyecto, ejecuta:
     ```bash
     mvn spring-boot:run
     ```

5. **Acceso a la Aplicación:**
   - El backend estará disponible en [http://localhost:8080](http://localhost:8080).
   - El panel de administración web estará en [http://localhost:8080/admin](http://localhost:8080/admin) (según configuración de rutas).


## Estructura de Carpetas

```
practica2_crudRest_compu/
├── src/
│   ├── main/
│   │   ├── java/           # Código fuente Java (controladores, servicios, modelos)
│   │   ├── resources/
│   │   │   ├── templates/  # Vistas Thymeleaf
│   │   │   └── application.properties
│   └── test/               # Pruebas unitarias y de integración
├── pom.xml                 # Configuración de Maven
├── mvnw, mvnw.cmd          # Maven Wrapper
└── README.md
```

## Endpoints Principales

- **Autenticación:** `/api/login`, `/api/register`
- **Usuarios:** `/api/users`, `/api/users/{id}/role`, `/api/users/{id}`
- **Películas:** `/api/movies/search`, `/api/movies/{imdbId}`
- **Favoritos:** `/api/movies/favorites`, `/api/movies/favorites/{userId}`, `/api/movies/favorites/{id}`
- **Historial:** `/api/history/{userId}`, `/api/history/all`

## Notas

- El backend está preparado para integrarse con la app [Film Finder Android](https://github.com/sammmcv/FilmFinderAndroid).
- Puedes modificar los endpoints o la configuración según tus necesidades.
- Para la integración con Google OAuth2, consulta la documentación de Spring Security y registra tu app en Google Developers Console.

## Autores

- Barros Martinez Luis Enrique
- Cortés Velazquez Samuel