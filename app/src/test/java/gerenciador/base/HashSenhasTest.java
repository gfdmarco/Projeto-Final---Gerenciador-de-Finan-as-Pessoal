package gerenciador.base;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HashSenhasTest {

    @Test
    void gerarHashDeveSerEstavel() {
        String hash1 = HashSenhas.gerarHash("senha123");
        String hash2 = HashSenhas.gerarHash("senha123");
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
    }
}
