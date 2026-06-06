package gerenciador.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;

public class PersisitenciaJSON {

    public static void salvar(Usuario u){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writeValue(new File("dados/" + u.getLogin() + ".json"), u);
        }
        catch (IOException e){
            throw new RuntimeException("Não foi possível salvar o usuário " + u.getLogin() + ".", e);
        }
    }

    public static Usuario carregar(String login){
        Object mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Usuario u = mapper.readValue(new File("dados/" + login + ".json"), Usuario.class);
        return u;
    }

    public static boolean usuarioExistente(String login){
        File arquivo = new File("dados/" + login + ".json");
        return arquivo.exists();
    }
}
