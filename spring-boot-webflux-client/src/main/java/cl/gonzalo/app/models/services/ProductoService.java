package cl.gonzalo.app.models.services;

import cl.gonzalo.app.models.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    public Flux<Producto>findAll();

    public Mono<Producto> findById(String id);

    public Mono<Producto> save(Producto producto);

    public Mono<Producto> update(Producto producto, String id);

    public Mono<Void> delete (String id);


}
