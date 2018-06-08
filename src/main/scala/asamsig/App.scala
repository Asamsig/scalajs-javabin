package asamsig

import jsonmodels.yql.{Forecast, Yql}
import org.scalajs.dom.Event
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLInputElement
import play.api.libs.json.Json
import slinky.core._
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSImport("resources/App.css", JSImport.Default)
@js.native
object AppCSS extends js.Object

@JSImport("resources/logo.svg", JSImport.Default)
@js.native
object ReactLogo extends js.Object

@react class App extends Component {
  type Props = Unit
  case class State(city: String, result: Option[Yql] = None)

  override def initialState = State("Trondheim")

  private val css = AppCSS

  def createForecastItem(forecast: Forecast): ReactElement =
    dt(
      div(
        h4(b(s"${forecast.day} ${forecast.date.toString.dropRight(5)}")),
        s"${forecast.low.toInt}° - ${forecast.high.toInt}° ${forecast.text}"
      )
    )

  def handleChange(e: Event): Unit = {
    val eventValue =
      e.target.asInstanceOf[HTMLInputElement].value
    setState(_.copy(city = eventValue))
  }

  def handleSubmit(e: Event): Unit = {
    e.preventDefault()

    if (state.city.nonEmpty) {
      Ajax
        .get(
          s"https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text=%27${state.city}%27)%20and%20u=%27c%27&format=json"
        )
        .map { response =>
          setState(prevState => {
            Option(Json.parse(response.responseText).as[Yql]).filter(_.query.count > 0) match {
              case Some(data) =>
                State(prevState.city, Some(data))
              case None =>
                State("City not found", None)
            }
          })
        }
    }
  }

  def render() = {
    div(className := "App")(
      header(className := "App-header")(
        img(src := ReactLogo.asInstanceOf[String], className := "App-logo", alt := "logo"),
        h1(className := "App-title")("Welcome to React (with Scala.js!)")
      ),
      p(className := "App-intro")(
        "To get started, edit ",
        code("App.scala"),
        " and save to reload."
      ),
      div(
        form(onSubmit := handleSubmit _)(
          input(
            onChange := handleChange _,
            value := state.city
          )
        ),
        state.result
          .map(yql => {
            val elements: Seq[ReactElement] = yql.query.results.channel.item.forecast.map(createForecastItem)
            List(h1(yql.query.results.channel.location.city.toString), dl(elements))
          })
      )
    )
  }
}
