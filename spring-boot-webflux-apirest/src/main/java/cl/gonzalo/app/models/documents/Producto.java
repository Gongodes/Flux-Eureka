package cl.gonzalo.app.models.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "productos")
public class Producto {


    @Id
    private String id;

    private String nombre;

    private Integer precio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    private Categoria categoria;





    public Producto(String nombre, Integer precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Producto(String nombre, Integer precio, Categoria categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }
}
