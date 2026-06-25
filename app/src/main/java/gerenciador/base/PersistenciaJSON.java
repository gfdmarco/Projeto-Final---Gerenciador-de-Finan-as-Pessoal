package gerenciador.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;

public class PersistenciaJSON {

    private static ObjectMapper criarMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        return mapper;
    }

    public static void salvar(Usuario u){
        try {
            File pastaDados = new File("dados");
            if (!pastaDados.exists()){
                pastaDados.mkdirs();
            }
            ObjectMapper mapper = criarMapper();
            mapper.writeValue(new File(pastaDados, u.getLogin() + ".json"), u);
        }
        catch (IOException e){
            throw new RuntimeException("Não foi possível salvar o usuário " + u.getLogin() + ".", e);
        }
    }

    public static Usuario carregar(String login){
        try {
            ObjectMapper mapper = criarMapper();
            Usuario u = mapper.readValue(new File("dados/" + login + ".json"), Usuario.class);
            return u;
        }
        catch(IOException e){
            throw new RuntimeException("Não foi possível carregar o usuário " + login + ".", e);
        }
    }

    public static boolean usuarioExistente(String login){
        File arquivo = new File("dados/" + login + ".json");
        return arquivo.exists();
    }
}
