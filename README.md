# Ice Delthios - Gerenciador de Finanças Pessoais

O **Ice Delthios** é um gerenciador de finanças pessoais desenvolvido em Java, projetado para ajudar você a organizar o caos de ter múltiplas contas bancárias. Com ele, você centraliza suas transações, define limites e acompanha seus gastos de maneira clara, segura e eficiente.

## 🚀 Funcionalidades Principais

* **Dashboard Consolidado:** Visão panorâmica do seu saldo, resumo mensal e progresso do orçamento.
* **Gestão Completa de Transações:** Cadastro de receitas e despesas de forma avulsa ou recorrente.
* **Controle Múltiplo:** Gerencie diferentes contas bancárias em um único lugar.
* **Categorização Inteligente:** Organize transações por categorias e tags personalizadas para saber exatamente para onde vai o seu dinheiro.
* **Fundos e Reservas:** Crie e acompanhe Fundos de Emergência e Fundos de Investimento separados do seu fluxo de caixa diário.
* **Relatórios Analíticos:** Gere relatórios detalhados por período ou por categoria.
* **Gestão de Limites (Alertas):** O sistema alerta caso você tente registrar uma despesa que ultrapasse o orçamento estabelecido ou caso haja falta de saldo.

## 🛠 Tecnologias Utilizadas e Salvamento em JSON

* **Java:** Linguagem principal responsável por toda a lógica de negócio e regras financeiras.
* **JavaFX:** Framework utilizado para a construção de uma interface gráfica desktop rica e fluida.
* **JSON (Jackson):** Todo o salvamento de dados (usuários, transações, contas) é feito localmente através da persistência em arquivos `.json`. Isso garante leveza, rapidez e permite rodar o sistema sem a necessidade de configurar um banco de dados externo.
* **Gradle:** Ferramenta de automação de compilação (*build*) e gerenciamento de dependências.

## 🎨 Design da Interface e Inteligência Artificial

A interface gráfica foi cuidadosamente construída utilizando o **Scene Builder**, uma ferramenta visual essencial do ecossistema JavaFX que permite estruturar as telas (arquivos FXML) de forma ágil com o padrão *drag-and-drop*. 

Além disso, este projeto contou com o apoio de **Inteligência Artificial (IA)** para otimização do desenvolvimento em duas frentes principais:
1.  **Design da Interface Gráfica:** Auxiliando na estruturação moderna dos layouts, sugestão de paletas de cores, estilização via CSS (como visto nos arquivos `.css` do projeto) e na integração entre o FXML e os *Controllers* em Java.
2.  **Documentação:** A elaboração e estruturação clara deste próprio arquivo `README.md` foi idealizada e gerada para facilitar o entendimento do projeto.

## ⚙️ Como Inicializar o Programa via Gradle

Para rodar o Ice Delthios na sua máquina, você precisa ter o **Java JDK** instalado. Como o projeto utiliza o **Gradle**, você não precisa configurar dependências manualmente.

1. Navegue até a pasta raiz do projeto via terminal.
2. Para compilar e executar o sistema, utilize o *wrapper* do Gradle:
   * **No Windows:**
     ```bash
     gradlew run
     ```
   * **No Linux / macOS:**
     ```bash
     ./gradlew run
     ```
*(Na primeira execução, o Gradle baixará automaticamente o JavaFX e as bibliotecas do Jackson para lidar com o JSON).*

## 🧪 Testes

O Ice Delthios possui uma suíte de testes unitários para garantir a confiabilidade das regras de negócio financeiras.
Para rodar todos os testes automatizados, execute:
```bash
./gradlew test

## 📁 Estrutura do Projeto (MVC) e Pastas

O projeto segue a arquitetura Model-View-Controller (MVC) com pacotes bem definidos, organizados no padrão de projetos Gradle:

app/
├── build.gradle             # Configurações do Gradle e gerenciamento de dependências
└── src/
    ├── main/
    │   ├── java/gerenciador/
    │   │   ├── base/        # Classes base do sistema (Usuário, Persistência JSON, etc.)
    │   │   ├── enums/       # Enumerações (Frequência, TipoFundo, etc.)
    │   │   ├── exceptions/  # Tratamento de exceções personalizadas
    │   │   ├── interfaces/  # Contratos e interfaces do sistema
    │   │   ├── operacoes/   # Lógica de negócio (Transações, Movimentações, Reservas, Relatórios)
    │   │   ├── sistema/     # Controllers da interface JavaFX
    │   │   └── suporte/     # Classes de apoio (Contas, Categorias, Tags, Histórico)
    │   └── resources/
    │       └── fxml/        # Arquivos de design da interface (.fxml) e estilos (.css)
    └── test/
        └── java/gerenciador/# Suíte de testes unitários (JUnit)