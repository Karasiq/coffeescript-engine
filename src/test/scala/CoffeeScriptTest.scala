import javax.script.ScriptEngineManager

import org.scalatest._

class CoffeeScriptTest extends FlatSpec with Matchers {
  val engine = new ScriptEngineManager().getEngineByName("coffeescript")

  "Engine" should "be registered" in {
    engine should not be null
  }

  it should "execute scripts" in {
    val result = engine.eval("number = 42; opposite = true; number = -42 if opposite; print(number); return number")
    result should be (-42)
  }
}
