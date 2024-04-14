package ru.pmlite.api.config

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender
import io.opentelemetry.sdk.logs.LogRecordProcessor
import io.opentelemetry.sdk.logs.SdkLoggerProvider
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.semconv.ResourceAttributes
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment

@Profile("metrics")
@Configuration
class OpenTelemetryConfig  {

  @Bean
  fun installOpenTelemetryAppender(openTelemetry: OpenTelemetry): ApplicationRunner {
    return ApplicationRunner { OpenTelemetryAppender.install(openTelemetry) }
  }



  @Bean
  fun otelSdkLoggerProvider(
    environment: Environment,
    logRecordProcessors: ObjectProvider<LogRecordProcessor>
  ): SdkLoggerProvider {
    val applicationName = environment.getProperty("spring.application.name", "application")
    val springResource = Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, applicationName))
    val builder = SdkLoggerProvider.builder().setResource(Resource.getDefault().merge(springResource))
    logRecordProcessors.orderedStream().forEach(builder::addLogRecordProcessor)
    return builder.build()
  }

  @Bean
  fun otelLogRecordProcessor(): LogRecordProcessor {
    return BatchLogRecordProcessor
      .builder(OtlpGrpcLogRecordExporter.builder().setEndpoint("http://otel-collector:4317").build())
      .build()
  }


}
