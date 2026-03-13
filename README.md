# ProyectoProgra3
# Procesamiento de Transacciones Bancarias con RabbitMQ
---
## Descripción
Este proyecto implementa un sistema distribuido para el procesamiento de transacciones bancarias usando **RabbitMQ**, **Java** y **Maven**, aplicando el patrón **Producer–Consumer**.
El sistema se le da u obtiene un lote de transacciones desde una API externa, distribuye cada transacción a una cola específica según el banco y posteriormente procesa cada transacción enviándola nuevamente a la API para su almacenamiento.

De esta manera se puede descoplar el envio y procesamiento de transacciones, con mejor escalabilidad y confiabilidad del sistema.

---

## Arquitectura del Sistema
El sistema está compuesto por tres componentes principales:
1. **Producer**
2. **RabbitMQ**
3. **Consumer**

Y su flujo de procesamiento es:
API (GET /transacciones)
↓
Producer
↓
RabbitMQ (colas por banco)
↓
Consumer
↓
API (POST /transacciones)

---

## Componentes del Sistema
### Producer
El Producer es el encargado de obtener las transacciones desde la API externa.
Y sus funciones principales:
* Consumir el endpoint **GET /transacciones**
* Recibir un lote de transacciones en formato JSON
* Analizar cada transacción y leer el campo **bancoDestino**
* Crear dinámicamente una cola en RabbitMQ para cada banco
* Publicar cada transacción como mensaje JSON en la cola correspondiente

Esto permite que cada banco tenga su propio canal de procesamiento.


### RabbitMQ
RabbitMQ funciona como intermediario entre el Producer y el Consumer.
Y sus funciones son:
* Recibir mensajes enviados por el Producer
* Almacenar temporalmente las transacciones en colas
* Distribuir los mensajes a los Consumers
Cada cola representa un banco específico.

Ejemplo de colas creadas dinámicamente (los 4 bancos):
* BANRURAL
* BAC
* BI
* GYT
Esto permite que cada entidad procese únicamente sus propias transacciones.


### Consumer
El Consumer escucha las colas creadas en RabbitMQ y procesa cada transacción.
Y sus funciones principales:
* Escuchar múltiples colas (una por banco)
* Recibir mensajes en formato JSON
* Convertir los mensajes a objetos Java
* Enviar cada transacción al endpoint **POST /transacciones**
* Confirmar el procesamiento mediante **ACK manual**
El uso de ACK manual nos garantiza que las transacciones no se pierdan si hay un error durante el procesamiento.

---

## Flujo del Sistema
1. El Producer realiza una solicitud al endpoint: GET /transacciones
2. La API devuelve un lote con múltiples transacciones.
3. El Producer analiza cada transacción y obtiene el campo: bancoDestino
4. Se crea una cola en RabbitMQ con el nombre del banco si no existe.
5. Cada transacción se envía a la cola correspondiente.
6. El Consumer escucha las colas activas.
7. Cuando recibe una transacción:
* La convierte a objeto Java
* La envía al endpoint POST /transacciones
8. Si el POST responde correctamente, el Consumer confirma el mensaje mediante ACK.

---

## Tecnologías utilizadas
* Java
* Maven
* RabbitMQ
* Docker
* Postman
* Jackson (JSON)

---

## Ejecución del proyecto
1. Iniciar RabbitMQ usando Docker.
2. Ejecutar el proyecto Producer para obtener y enviar transacciones.
3. Verificar en el panel de RabbitMQ que las colas se crean automáticamente.
4. Ejecutar el proyecto **Consumer** para procesar los mensajes.
5. Verificar que las transacciones sean enviadas al endpoint **POST /transacciones**.

---

## Demostración
El video de demostración:

1. Consumo del endpoint GET /transacciones
2. Publicación de transacciones en RabbitMQ
3. Creación de colas dinámicas
4. Consumo de mensajes
5. Envío de transacciones al endpoint POST
