package com.example.qrpay.services

import com.soywiz.klock.seconds
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

@Service
class SSEService(
    private val connectionFactory: ConnectionFactory,
    private val amqpAdmin: AmqpAdmin,
    private val simpleRabbitListenerContainerFactory: SimpleRabbitListenerContainerFactory,
) {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()


    fun createEmitter(hash: String): SseEmitter? {

        ensureQueueExists(hash)

        val emitter = SseEmitter(5.seconds.millisecondsLong)

        configureConsumer(hash, emitter)

        return emitter
    }

    private fun configureConsumer(hash: String, emitter: SseEmitter) {

        val container: SimpleMessageListenerContainer = simpleRabbitListenerContainerFactory.createListenerContainer()

        container.connectionFactory = connectionFactory
        container.setQueueNames(hash)

        val consumer = Consumer<TransferResult> { message ->

            emitter.send(message, MediaType.APPLICATION_JSON)
            emitter.complete()
        }

        val adapter = MessageListenerAdapter(consumer)
        adapter.setDefaultListenerMethod("accept")
        adapter.setMessageConverter(Jackson2JsonMessageConverter())

        container.setMessageListener(adapter)

        emitter.onError {
            container.stop()
        }

        emitter.onCompletion {
            container.stop()
        }

        emitter.onTimeout {
            container.stop()
        }

        executor.submit { container.start() }

    }

    private fun ensureQueueExists(hash: String) {
        val queueInfo = amqpAdmin.getQueueInfo(hash)
        if (queueInfo == null) {
            val queue = Queue(hash, false, true, true)
            val binding = Binding(hash, Binding.DestinationType.QUEUE, "transfer", hash, null)

            amqpAdmin.declareQueue(queue)
            amqpAdmin.declareBinding(binding)
        }
    }

    @RabbitListener(queues = ["xpto"])
    fun handle(transferResult: TransferResult) {

    }

}