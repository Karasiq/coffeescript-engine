package com.karasiq.coffeescript

import java.io.InputStreamReader
import javax.script._

import com.karasiq.coffeescript.ScriptUtils._

import scala.util.control.Exception

trait CoffeeScriptCompiler {
  def compile(script: String): String
}

object CoffeeScriptCompiler {
  def apply(javaScriptEngine: ScriptEngine): CoffeeScriptCompiler = {
    val classLoader = Thread.currentThread().getContextClassLoader
    val reader = new InputStreamReader(classLoader.getResourceAsStream("coffee-script.js"))
    Exception.allCatch.andFinally(reader.close()) {
      javaScriptEngine.withContext(new SimpleScriptContext) { engine ⇒
        engine.eval(reader)
        engine match {
          case i: Invocable ⇒
            i.getInterface(javaScriptEngine.eval("CoffeeScript"), classOf[CoffeeScriptCompiler])

          case _ ⇒ throw new IllegalArgumentException("Cannot create compiler")
        }
      }
    }
  }
}
