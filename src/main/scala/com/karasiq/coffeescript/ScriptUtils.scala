package com.karasiq.coffeescript

import javax.script.{ScriptContext, ScriptEngine}

private[coffeescript] object ScriptUtils {
  implicit class EngineOps(engine: ScriptEngine) {
    def withContext[T](context: ScriptContext)(f: ScriptEngine ⇒ T): T = {
      val oldContext = engine.getContext
      engine.setContext(context)
      try { f(engine) } finally { engine.setContext(oldContext) }
    }
  }
}
