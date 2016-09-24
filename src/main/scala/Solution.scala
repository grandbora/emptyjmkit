package emptyjvmkit

import com.twitter.conversions.storage._
import com.twitter.conversions.time._
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.service.HttpResponseClassifier
import com.twitter.finagle.tracing.NullTracer
import com.twitter.finagle.util.DefaultTimer
import com.twitter.finagle.{Http, Stack, param}
import com.twitter.util.Await

object Solution extends App {

  println("hello emptyjvmkit")

  //  val statsReceiver = SoundCloudStatsReceiver("client", telemetry, config)
  val timer = DefaultTimer.twitter


  val builder = Http.client
    .withLabel("ADYEN")
    //    .withStatsReceiver(statsReceiver)
    .withStreaming(false)
    //    .withTlsWithoutValidation
    //    .withTracer(tracer)
    .withMaxResponseSize(5.megabytes)
    .configured(param.Timer(timer))
    //    .configured(HttpClientParams.connectionParams(serviceConfig))
    //    .configured(HttpClientParams.expirationParams(serviceConfig))
    //    .configured(HttpClientParams.keepaliveParams(serviceConfig))
    //    .configured(Transporter.ConnectTimeout(serviceConfig.getTcpConnectionTimeout))
    .withRequestTimeout(1.minute)
    //    .withRetryBudget(retryBudget)
    .withResponseClassifier(HttpResponseClassifier.ServerErrorsAsFailures)

  val newStack = builder.stack.remove(Stack.Role("TraceInitializerFilter"))
  val newBuilder = builder.copy(stack = newStack)

  //  val service = Await.result(builder.newClient("127.0.0.1:1338").apply())
  val service = newBuilder.newService("127.0.0.1:1338")

  val req = Request("http://127.0.0.1:1338")
  Await.result(service.apply(req))

  println("end")

}
