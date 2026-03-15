
# Patrón Proxy en este proyecto — Ejemplos reales

---

## 1. **Proxy de Protección** — `@PreAuthorize` en `EditionTeamController`

```java
// EditionTeamController.java
@RestController
public class EditionTeamController {

    private final EditionTeamRegistrationService registrationService;

    public EditionTeamController(EditionTeamRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/editions/{editionId}/teams/{teamId}")
    public ResponseEntity<Map<String, String>> registerTeam(
            @PathVariable Long editionId, @PathVariable String teamId) {
        registrationService.registerTeam(editionId, teamId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "editionId", editionId.toString(),
                "teamId", teamId,
                "status", "REGISTERED"));
    }
}
```

**Tipo de Proxy:** **Protection Proxy** (Proxy de Protección).

**Cómo funciona:** Spring Security crea un **proxy CGLIB** alrededor del bean `EditionTeamController`. Cuando se invoca `registerTeam()`, la llamada **no llega directamente** al método: primero pasa por un interceptor (`MethodSecurityInterceptor`) que evalúa la expresión SpEL `"isAuthenticated()"`. Si el usuario no está autenticado, el proxy lanza un `AccessDeniedException` **antes** de que el método real se ejecute. El controlador nunca sabe que está siendo protegido; el proxy actúa como guardián transparente.

---

## 2. **Proxy Transaccional (Remote/Smart Reference Proxy)** — `@Transactional` en `ProjectRoomAssignmentService`

```java
// ProjectRoomAssignmentService.java
@Service
public class ProjectRoomAssignmentService {

    private final ProjectRoomRepository projectRoomRepository;
    private final JudgeRepository judgeRepository;

    public ProjectRoomAssignmentService(ProjectRoomRepository projectRoomRepository,
                                        JudgeRepository judgeRepository) {
        this.projectRoomRepository = projectRoomRepository;
        this.judgeRepository = judgeRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AssignJudgeResponse assignJudge(AssignJudgeRequest request) {
        ProjectRoom room = projectRoomRepository.findById(request.roomId())
                .orElseThrow(() -> new RoomAssignmentException("ROOM_NOT_FOUND", "Room not found"));
        // ... lógica de asignación ...
        projectRoomRepository.save(room);
        judgeRepository.save(judge);
        return new AssignJudgeResponse(/* ... */);
    }
}
```

**Tipo de Proxy:** **Smart Reference Proxy** (a veces llamado Proxy Transaccional).

**Cómo funciona:** Spring genera un **proxy CGLIB** que envuelve `ProjectRoomAssignmentService`. Cuando otro bean invoca `assignJudge()`, la llamada es interceptada por `TransactionInterceptor`. El proxy:
1. **Antes** del método: abre una transacción con aislamiento `SERIALIZABLE`.
2. **Ejecuta** el método real (`assignJudge`).
3. **Después** del método: si no hubo excepción, hace `commit`; si hubo excepción, hace `rollback`.

El servicio no contiene ningún código de gestión transaccional — toda esa lógica "extra" vive en el proxy. Esto es un ejemplo clásico de **AOP (Aspect-Oriented Programming)** donde el aspecto transaccional se "teje" en tiempo de ejecución mediante el proxy.

---

## 3. **Proxy Virtual / Smart Reference** — `@RepositoryRestResource` + Spring Data JPA en `EditionRepository`

```java
// EditionRepository.java
@Tag(name = "Editions", description = "Repository for managing Edition entities")
@RepositoryRestResource
public interface EditionRepository extends CrudRepository<Edition, Long>,
                                           PagingAndSortingRepository<Edition, Long> {

    @RestResource(exported = false)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Edition e where e.id = :id")
    Optional<Edition> findByIdForUpdate(@Param("id") Long id);

    List<Edition> findByYear(@Param("year") Integer year);

    List<Edition> findByVenueName(@Param("venueName") String venueName);
}
```

**Tipo de Proxy:** **Virtual Proxy** (Proxy Virtual).

**Cómo funciona:** `EditionRepository` es una **interfaz sin implementación**. No existe ninguna clase que implemente sus métodos. En tiempo de ejecución, Spring Data JPA utiliza `java.lang.reflect.Proxy` (JDK Dynamic Proxy) para crear una **implementación generada dinámicamente**. Cuando se invoca `findByYear(2024)`, el proxy intercepta la llamada y:
1. Analiza el nombre del método (`findByYear`) para derivar la consulta SQL automáticamente (o usa la `@Query` explícita en `findByIdForUpdate`).
2. Abre una conexión, ejecuta la query y mapea el resultado a objetos `Edition`.

La interfaz actúa como un **marcador de posición** (placeholder) para un objeto real que no existe hasta que el proxy lo materializa. Además, `@RepositoryRestResource` añade otra capa de proxy que expone automáticamente los endpoints REST (`GET /editions`, `GET /editions/{id}`, etc.) sin que tú escribas un controlador.

---

## Resumen Visual

| Fragmento | Anotación clave | Tipo de Proxy | Mecanismo Spring |
|---|---|---|---|
| `EditionTeamController` | `@PreAuthorize` | **Protección** | CGLIB proxy + `MethodSecurityInterceptor` |
| `ProjectRoomAssignmentService` | `@Transactional(SERIALIZABLE)` | **Smart Reference / Transaccional** | CGLIB proxy + `TransactionInterceptor` |
| `EditionRepository` | `@RepositoryRestResource` | **Virtual** | JDK Dynamic Proxy (Spring Data) |

> **Nota clave:** Spring usa **CGLIB proxies** cuando la clase destino es una clase concreta (controladores, servicios) y **JDK Dynamic Proxies** cuando el destino es una interfaz (repositorios). En ambos casos, el patrón es el mismo: un objeto intermediario intercepta las llamadas y añade comportamiento antes/después sin modificar el código original.
