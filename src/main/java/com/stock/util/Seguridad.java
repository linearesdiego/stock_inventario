package com.stock.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de contraseñas con PBKDF2 (viene incluido en el JDK, no hace falta ninguna librería).
 * Se guarda en la base como "iteraciones:salt:hash", nunca la contraseña en texto plano.
 */
public final class Seguridad {

    private static final int ITERACIONES = 65536;
    private static final int LARGO_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private Seguridad() {
    }

    public static String hashear(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERACIONES);
        Base64.Encoder b64 = Base64.getEncoder();
        return ITERACIONES + ":" + b64.encodeToString(salt) + ":" + b64.encodeToString(hash);
    }

    public static boolean verificar(String password, String guardado) {
        if (password == null || guardado == null) {
            return false;
        }
        String[] partes = guardado.split(":");
        if (partes.length != 3) {
            return false;
        }
        int iteraciones = Integer.parseInt(partes[0]);
        byte[] salt = Base64.getDecoder().decode(partes[1]);
        byte[] esperado = Base64.getDecoder().decode(partes[2]);
        byte[] calculado = pbkdf2(password.toCharArray(), salt, iteraciones);
        return java.security.MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iteraciones) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, iteraciones, LARGO_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular el hash", e);
        }
    }
}
