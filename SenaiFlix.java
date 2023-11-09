package Filmes;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SenaiFlix {
    private static Connection connection;

    public static void main(String[] args) throws Throwable {
        conectarAoBanco();

        while (true) {
            String escolha = JOptionPane.showInputDialog("Escolha uma ação:\n" +
                    "1 - Inserir filme\n" +
                    "2 - Listar filmes\n" +
                    "3 - Atualizar filme\n" +
                    "4 - Excluir filme\n" +
                    "5 - Sair");

            if (escolha == null) {
                desconectarDoBanco();
                System.exit(0);
            }

            switch (escolha) {
                case "1":
                    inserirFilme();
                    break;
                case "2":
                    listarFilmes();
                    break;
                case "3":
                    atualizarFilme();
                    break;
                case "4":
                    excluirFilme();
                    break;
                case "5":
                    desconectarDoBanco();
                    System.exit(0);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida. Tente novamente.");
                    break;
            }
        }
    }

    public static void conectarAoBanco() {
        String jdbcURL = "jdbc:mysql://localhost:3306/catalogo";
        String username = "root";
        String password = "senai";

        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            JOptionPane.showMessageDialog(null, "Conexão com o banco de dados bem-sucedida!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao estabelecer a conexão com o banco de dados: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void inserirFilme() {
        String nome = JOptionPane.showInputDialog("Digite o nome do filme:");
        String genero = JOptionPane.showInputDialog("Digite o gênero do filme:");
        String diretor = JOptionPane.showInputDialog("Digite o nome do diretor:");
        String duracaoStr = JOptionPane.showInputDialog("Digite a duração do filme:");

        try {
            int duracao = Integer.parseInt(duracaoStr);

            String inserirSQL = "INSERT INTO filmes (nome, genero, diretor, duracao) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(inserirSQL)) {
                preparedStatement.setString(1, nome);
                preparedStatement.setString(2, genero);
                preparedStatement.setString(3, diretor);
                preparedStatement.setInt(4, duracao);

                int linhasAfetadas = preparedStatement.executeUpdate();

                if (linhasAfetadas > 0) {
                    JOptionPane.showMessageDialog(null, "Filme inserido com sucesso.");
                } else {
                    JOptionPane.showMessageDialog(null, "Falha ao inserir o filme.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Duração inválida. Certifique-se de inserir um número inteiro.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir o filme: " + e.getMessage());
        }
    }

    public static void listarFilmes() {
        String listarSQL = "SELECT * FROM filmes";

        try (PreparedStatement preparedStatement = connection.prepareStatement(listarSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            StringBuilder listaFilmes = new StringBuilder("Lista de filmes:\n");

            while (resultSet.next()) {
                int id = resultSet.getInt("idFilme");
                String nome = resultSet.getString("nome");
                String genero = resultSet.getString("genero");
                String diretor = resultSet.getString("diretor");
                int duracao = resultSet.getInt("duracao");

                listaFilmes.append("ID: ").append(id).append(", Nome: ").append(nome).append(", Gênero: ").append(genero)
                        .append(", Diretor: ").append(diretor).append(", Duração: ").append(duracao).append("\n");
            }

            JOptionPane.showMessageDialog(null, listaFilmes.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar os filmes: " + e.getMessage());
        }
    }

    public static void atualizarFilme() throws SQLException {
        String idFilmeStr = JOptionPane.showInputDialog("Digite o ID do filme a ser atualizado:");
        try {
            int idFilme = Integer.parseInt(idFilmeStr);

            String nome = JOptionPane.showInputDialog("Digite o novo nome do filme:");
            String genero = JOptionPane.showInputDialog("Digite o novo gênero do filme:");
            String diretor = JOptionPane.showInputDialog("Digite o novo nome do diretor:");
            String duracaoStr = JOptionPane.showInputDialog("Digite a nova duração do filme:");

            try {
                int duracao = Integer.parseInt(duracaoStr);

                String atualizarSQL = "UPDATE filmes SET nome = ?, genero = ?, diretor = ?, duracao = ? WHERE idFilme = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(atualizarSQL)) {
                    preparedStatement.setString(1, nome);
                    preparedStatement.setString(2, genero);
                    preparedStatement.setString(3, diretor);
                    preparedStatement.setInt(4, duracao);
                    preparedStatement.setInt(5, idFilme);

                    int linhasAfetadas = preparedStatement.executeUpdate();

                    if (linhasAfetadas > 0) {
                        JOptionPane.showMessageDialog(null, "Filme atualizado com sucesso.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Falha ao atualizar o filme. Verifique o ID do filme.");
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Duração inválida. Certifique-se de inserir um número inteiro.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de filme inválido. Certifique-se de inserir um número inteiro.");
        }
    }

    public static void excluirFilme() {
        String idFilmeStr = JOptionPane.showInputDialog("Digite o ID do filme a ser excluído:");
        try {
            int idFilme = Integer.parseInt(idFilmeStr);

            String excluirSQL = "DELETE FROM filmes WHERE idFilme = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(excluirSQL)) {
                preparedStatement.setInt(1, idFilme);

                int linhasAfetadas = preparedStatement.executeUpdate();

                if (linhasAfetadas > 0) {
                    JOptionPane.showMessageDialog(null, "Filme excluído com sucesso.");
                } else {
                    JOptionPane.showMessageDialog(null, "Falha ao excluir o filme. Verifique o ID do filme.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de filme inválido. Certifique-se de inserir um número inteiro.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir o filme: " + e.getMessage());
        }
    }

    public static void desconectarDoBanco() {
        try {
            if (connection != null) {
                connection.close();
                JOptionPane.showMessageDialog(null, "Conexão com o banco de dados encerrada.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao encerrar a conexão com o banco de dados: " + e.getMessage());
        }
    }
}
