package cl.gonzalo.app.controllers;

import cl.gonzalo.app.models.service.ProductoService;
import cl.gonzalo.app.models.documents.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;


    @GetMapping
    public Flux<Producto> lista() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Producto> ver(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public Mono<Producto> crear(@RequestBody Producto producto) {

        if (producto.getCreateAt() == null) {
            producto.setCreateAt(new Date());
        }

        return service.save(producto);
    }


    @PutMapping("/{id}")
    public Mono<Producto> editar(@RequestBody Producto producto, @PathVariable String id) {


        return service.findById(id).flatMap(p -> {

            p.setNombre(producto.getNombre());
            p.setCategoria(producto.getCategoria());
            p.setPrecio(producto.getPrecio());


            return service.save(p);
        });
    }

    @DeleteMapping("/{id}")
    public Mono<Producto> borrar(@PathVariable String id) {



        return  service.findById(id).flatMap(p -> {
            return service.delete(p).then(Mono.just(p));
        });

    }

}