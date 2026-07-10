package com.jaba.ms_idp.util; // Ajusta el paquete si es necesario

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta para firmar el token (debe ser larga y segura). 
    // Usamos un valor por defecto seguro en caso de que no esté en el application.properties
    @Value("${jwt.secret:SmartLogixSuperSecretKeyParaFirmarTokens2026+}")
    private String secret;

    // Tiempo de expiración en milisegundos (24 horas = 86400000 ms)
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    // Método interno para generar la llave criptográfica a partir del texto secreto
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // El método principal que llamaremos desde el controlador
    public String generateToken(String email, String rol) {
        return Jwts.builder()
                .subject(email) // El "dueño" del token (en este caso, el correo)
                .claim("rol", rol) // Para que a futuro el backend también pueda validar el rol
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Fecha de caducidad
                .signWith(getSigningKey()) // La firma digital irrefutable
                .compact(); // Construye el string final
    }
}

