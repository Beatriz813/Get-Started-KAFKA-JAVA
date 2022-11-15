package br.com.alura.ecommerce.usermodule;

import br.com.alura.ecommerce.KafkaService;

import br.com.alura.ecommerce.usermodule.modelos.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            this.connection.createStatement().execute("create table Users (uuid varchar(200) primary key, email varchar(200))");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws SQLException {
        CreateUserService createUserService = new CreateUserService();
        try (KafkaService service = new KafkaService<Order>(CreateUserService.class.getName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::Callback, Order.class, new HashMap<>())) {
            service.run();
        }
    }

    private void Callback(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("==============================================================");
        System.out.println("Precossing new order, checking for new user");
        System.out.println(record.value());

        var order = record.value();

        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }

    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = this.connection.prepareStatement("select uuid from Users " +
                "where email = ? limit 1");
        exists.setString(1, email);
        var result = exists.executeQuery();
        return !result.next();
    }

    private void insertNewUser(String email) throws SQLException {
        var insert = this.connection.prepareStatement("insert into Users (uuid, email) values (?, ?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("Usuário uuid e " + email + " adicionado");
    }
}