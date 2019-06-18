package io.teacheck.messenger;

import io.vertx.amqp.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import static io.teacheck.messenger.constants.Constants.*;

public class Messenger extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Messenger.class);
    private WebClient webClient;
    private AmqpClient client;
    @Override
    public void start() {
        AmqpClientOptions options = new AmqpClientOptions()
                .setHost(BROKER_HOST)
                .setUsername(BROKER_USERNAME)
                .setPassword(BROKER_PASSWORD)
                .setConnectTimeout(BROKER_TIMEOUT)
                .setPort(BROKER_PORT);
        client = AmqpClient.create(vertx, options);
        setupWebClient();
        listen();
    }

    private void setupWebClient() {
        WebClientOptions webClientOptions = new WebClientOptions()
                .setDefaultHost(DB_SERVICE_HOST)
                .setDefaultPort(DB_SERVICE_PORT);

        webClient = WebClient.create(vertx,webClientOptions);
    }

    private void listen() {
        client.connect(ar -> {
            if (ar.failed()) {
                logger.error("Unable to connect to the broker");
            } else {
                logger.info("Connection succeeded");
                AmqpConnection connection = ar.result();
                connection.createReceiver(BROKER_QUEUE_NAME, this::consumirMensaje);
            }
        });
    }

    private void consumirMensaje(AsyncResult<AmqpReceiver> resHandler) {
        if (resHandler.succeeded()) {
            logger.info("Receiver created.");
            AmqpReceiver receiver = resHandler.result();
            receiver.exceptionHandler(throwable -> logger.error(throwable.getMessage()))
                    .handler(this::consumirMensaje);
        } else {
            logger.error("Couldn't create receiver: " + resHandler.cause().getMessage());
        }
    }

    private void consumirMensaje(AmqpMessage amqpMessage) {
        JsonObject data = amqpMessage.bodyAsJsonObject();
        persistirDatos(data);
    }

    private void persistirDatos(JsonObject data) {
        JsonObject object = new JsonObject()
                .put("resultados-control", data);
        webClient.post("/messenger/post-data")
                .sendJsonObject(object, this::responseHandler);
    }

    private void responseHandler(AsyncResult<HttpResponse<Buffer>> asyncResponse) {
        if (asyncResponse.succeeded()) {
            logger.info("Request succeed with response status: " + asyncResponse.result().statusMessage());
            logger.info("Response body: " + asyncResponse.result().bodyAsString());
        } else {
            logger.error("Cause: " + asyncResponse.cause());
        }
    }
}
