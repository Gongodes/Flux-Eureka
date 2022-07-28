package cl.gonzalo.app.models.dao;

import cl.gonzalo.app.models.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria,String> {
}
