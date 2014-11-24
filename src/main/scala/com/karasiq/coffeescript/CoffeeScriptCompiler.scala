package com.karasiq.coffeescript

import java.io.InputStreamReader
import javax.script.{ScriptContext, ScriptEngine, SimpleScriptContext}

trait CoffeeScriptCompiler {
  protected def javaScriptEngine: ScriptEngine

  private val context = new SimpleScriptContext // Compiler context

  private lazy val coffeeScript = {
    val classLoader = Thread.currentThread().getContextClassLoader
    val reader = new InputStreamReader(classLoader.getResourceAsStream("coffee-script.js"))
    try {
      javaScriptEngine.eval(reader, context)
    } finally {
      reader.close()
    }
  }

  final def compile(script: String): String = {
    coffeeScript // Init
    context.setAttribute("_cfscriptsrc", script, ScriptContext.ENGINE_SCOPE)
    try {
      val call = javaScriptEngine.getFactory.getMethodCallSyntax("CoffeeScript", "compile", "_cfscriptsrc")
      val result = javaScriptEngine.eval(call, context)
      result match { case s: String ⇒ s }
    } finally {
      context.removeAttribute("_cfscriptsrc", ScriptContext.ENGINE_SCOPE)
    }
  }
}
