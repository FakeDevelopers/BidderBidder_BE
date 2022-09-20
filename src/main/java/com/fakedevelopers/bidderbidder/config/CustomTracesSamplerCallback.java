package com.fakedevelopers.bidderbidder.config;

import io.sentry.SamplingContext;
import io.sentry.SentryOptions.TracesSamplerCallback;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
class CustomTracesSamplerCallback implements TracesSamplerCallback {

  @Override
  public Double sample(@NonNull SamplingContext context) {
    return 1.0;
  }
}
