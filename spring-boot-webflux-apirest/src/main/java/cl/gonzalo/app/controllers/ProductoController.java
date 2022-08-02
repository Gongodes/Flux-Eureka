package cl.gonzalo.app.controllers;

import cl.gonzalo.app.models.service.ProductoService;
import cl.gonzalo.app.models.documents.Producto;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;

import static com.mongodb.assertions.Assertions.assertTrue;


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


            RSAKey senderJWK = null;
            try {
                senderJWK = new RSAKeyGenerator(2048)
                        .keyID("123")
                        .keyUse(KeyUse.SIGNATURE)
                        .generate();
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
            RSAKey senderPublicJWK = senderJWK.toPublicJWK();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(senderJWK.getKeyID()).build(),
                    new JWTClaimsSet.Builder()
                            .subject(producto.getNombre())
                            .issueTime(new Date())

                            .build());



            RSAKey recipientJWK = null;
            try {
                recipientJWK = new RSAKeyGenerator(2048)
                        .keyID("456")
                        .keyUse(KeyUse.ENCRYPTION)
                        .generate();
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
            RSAKey recipientPublicJWK = recipientJWK.toPublicJWK();

            try {
                signedJWT.sign(new RSASSASigner(senderJWK));
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }


            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
                            .contentType("JWT")
                            .build(),
                    new Payload(signedJWT));


            try {
                jweObject.encrypt(new RSAEncrypter(recipientPublicJWK));
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }


            String jweString = jweObject.serialize();
                System.out.println(jweString);

            try {
                jweObject = JWEObject.parse(jweString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


            try {
                jweObject.decrypt(new RSADecrypter(recipientJWK));
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }


            signedJWT = jweObject.getPayload().toSignedJWT();




            try {
                assertTrue(signedJWT.verify(new RSASSAVerifier(senderPublicJWK)));
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }


            try {
                p.setNombre(signedJWT.getJWTClaimsSet().getSubject());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
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