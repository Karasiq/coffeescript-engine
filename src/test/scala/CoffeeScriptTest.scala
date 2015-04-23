import javax.script._

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

  it should "call function" in {
    val context = new SimpleScriptContext
    context.setAttribute("test", engine.eval("return {}"), ScriptContext.ENGINE_SCOPE)

    val script = engine match { // Pre-compile script
      case c: Compilable ⇒
        c.compile("test.square = (x) -> x * x")
    }
    script.eval(context)
    val result = engine match {
      case i: Invocable ⇒
        i.invokeMethod(context.getAttribute("test"), "square", "9")
    }
    result shouldBe 81
  }
}
