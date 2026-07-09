package gerenciador.sistema;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;

public class App extends Application{

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        stage.setTitle("Ice Delthios: Gerenciador de Financas Pessoais");
        App.trocarTela("login");
        stage.show();
    }

    public static void trocarTela(String nomeTela) throws Exception{
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + nomeTela + ".fxml"));
        stage.setScene(new Scene(loader.load()));
    }

    public static void trocarTela(String nomeTela, Usuario usuario) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + nomeTela + ".fxml"));
        Scene cena = new Scene(loader.load());
        Object controller = loader.getController();
        if (controller instanceof UsuarioNecessario){
            ((UsuarioNecessario) controller).setUsuario(usuario);
        }
        stage.setScene(cena);
    }

    public static void main(String[] args){
        launch(args);
    }
}
