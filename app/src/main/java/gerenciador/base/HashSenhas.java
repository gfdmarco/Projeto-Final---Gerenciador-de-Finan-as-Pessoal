package gerenciador.base;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashSenhas {

    private static String conversorHexa(byte[] bytes){
        //string para ser construída
        StringBuilder hexaConcatenado = new StringBuilder();
        for (byte b : bytes){
            String hexaAtual = String.format("%02x", b);
            hexaConcatenado.append(hexaAtual);
        }
        return hexaConcatenado.toString();
    }
    
    public static String gerarHash(String senha){
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            byte[] bytes_senha = senha.getBytes(StandardCharsets.UTF_8);

            //cálculo do hash
            byte[] hashBytes = hasher.digest(bytes_senha);

            //conversão para string do hash
            String hash = conversorHexa(hashBytes);

            return hash;
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 não disponível", e);
        }
    }
}
