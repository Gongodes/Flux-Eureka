package cl.gonzalo.app.handdler;

import cl.gonzalo.app.models.Producto;
import cl.gonzalo.app.models.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Component
public class ProductoHandler {

    @Autowired
    private ProductoService service;

    public Mono<ServerResponse>listar(ServerRequest request){
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(service.findAll(), Producto.class);

    }

    public Mono<ServerResponse> ver (ServerRequest request){
        String id = request.pathVariable("id");
        return  service.findById(id).flatMap(p->ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> crear(ServerRequest request){
        Mono<Producto> producto = request.bodyToMono(Producto.class);

        return producto.flatMap(p-> {
            if (p.getCreateAt() == null) {
                p.setCreateAt(new Date());
            }
            return service.save(p);}).flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(p));
        }

    public Mono<ServerResponse> editar (ServerRequest request){
        Mono<Producto> producto = request.bodyToMono(Producto.class);

        String id = request.pathVariable("id");

        return producto.flatMap(p->ServerResponse.created(URI.create("/api/client/".concat(id)))
                .contentType(MediaType.APPLICATION_JSON).body(service.update(p,id),Producto.class));

    }

    public Mono<ServerResponse> eliminar (ServerRequest request){
        String id = request.pathVariable("id");
        return service.delete(id).then(ServerResponse.noContent().build());
    }
}