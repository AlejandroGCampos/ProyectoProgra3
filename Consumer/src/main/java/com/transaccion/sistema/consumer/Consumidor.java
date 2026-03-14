package com.transaccion.sistema.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.trasaccion.sistema.modelos.Transaccion;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Consumidor {

	private static final String[] BANK_QUEUES = {"BANRURAL", "GYT", "BAC", "BI"};
    private static final String REJECT_QUEUE = "colas_rechazados"; //cola para los rechazos
	private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("AleCampos");
        factory.setPassword("Alej4ndr0");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);
        channel.queueDeclare(REJECT_QUEUE, true, false, false, null);

        for (String queue : BANK_QUEUES) {
            channel.queueDeclare(queue, true, false, false, null);
            
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String payload = new String(delivery.getBody(), StandardCharsets.UTF_8);
                try {
                    Transaccion tx = mapper.readValue(payload, Transaccion.class);
                    String codigoUnico = UUID.randomUUID().toString().substring(0, 8);
                    tx.idTransaccion = "TX-" + tx.idTransaccion + "-" + codigoUnico + "-AlejandroCampos";
                    tx.carnet = "0905-24-5051"; 
                    tx.nombre = "Estalin Alejandro Godoy Campos"; 
                    tx.correo = "egodoyc4@miumg.edu.gt";
                    
                    String jsonModificado = mapper.writeValueAsString(tx);
                    
                    if (tx.monto > 4000.00) { //modificaciones por el monto
                    	
                    	if (postToApi(jsonModificado)) {
                            System.out.println("[OK] Transacción guardada para AlejandroCampos: " + tx.idTransaccion);
                    	
                    	}else {
                    		System.err.println("[FALLO] API otrogada tuvo error en respuesta 200. El mensaje sigue en RabbitMQ.");
                    	}
                            
                    }else {
                        channel.basicPublish(
                                "",
                                REJECT_QUEUE,
                                null,
                                jsonModificado.getBytes(StandardCharsets.UTF_8)
                        );

                        System.out.println("Transacción enviada a cola_rechazadas: " + tx.idTransaccion);

                    }
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (Exception ex) {
                    System.err.println("Error procesando mensaje: " + ex.getMessage());
                }
            };

            channel.basicConsume(queue, false, deliverCallback, consumerTag -> {});
        }
        System.out.println("Consumidor esperando mensajes...");
    }

    private static boolean postToApi(String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://7e0d9ogwzd.execute-api.us-east-1.amazonaws.com/default/guardarTransacciones"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Respuesta del servidor: " + response.statusCode() + " -> " + response.body());
            
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    } 
}