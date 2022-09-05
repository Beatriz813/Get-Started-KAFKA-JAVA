package br.com.alura.ecommerce.Serializador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class GsonDeserializer<T> implements Deserializer<T> {
    private final Gson gson = new GsonBuilder().create();
    public static String TYPE_CONFIG = "";
    private Class<T> type;
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String typeName = String.valueOf(configs.get(TYPE_CONFIG));
        try {
            this.type = (Class<T>) Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw  new RuntimeException("Tipo de deserialização não existe");
        }
    }

    @Override
    public T deserialize(String topico, byte[] dados) {
        return gson.fromJson(new String(dados), type);
    }
}
