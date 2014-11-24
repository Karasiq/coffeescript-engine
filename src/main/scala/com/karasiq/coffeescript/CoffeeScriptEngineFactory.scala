package com.karasiq.coffeescript

import java.io.{BufferedReader, Reader}
import java.util
import javax.script._

import scala.language.postfixOps

private sealed abstract class CoffeeScriptEngine extends AbstractScriptEngine with CoffeeScriptCompiler {
  private val engineManager = new ScriptEngineManager()

  override val javaScriptEngine: ScriptEngine = engineManager.getEngineByName("javascript")

  override def eval(script: String, context: ScriptContext): AnyRef = {
    javaScriptEngine.eval(compile(script), context)
  }

  override def eval(reader: Reader, context: ScriptContext): AnyRef = {
    val bufferedReader = new BufferedReader(reader)
    val script = Iterator.continually(bufferedReader.readLine()).takeWhile(null ne)
      .foldLeft(StringBuilder.newBuilder)((builder, string) ⇒ builder.append(string))
      .result()
    eval(script, context)
  }

  override def getFactory: ScriptEngineFactory

  override def createBindings(): Bindings = new SimpleBindings()
}

final class CoffeeScriptEngineFactory extends ScriptEngineFactory {
  import scala.collection.JavaConversions._

  override def getEngineName: String = "CoffeeScript Engine"

  override def getParameter(key: String): AnyRef = key match {
    case ScriptEngine.ENGINE ⇒ getEngineName
    case ScriptEngine.ENGINE_VERSION ⇒ getEngineVersion
    case ScriptEngine.NAME ⇒ getNames.head
    case ScriptEngine.LANGUAGE ⇒ getLanguageName
    case ScriptEngine.LANGUAGE_VERSION ⇒ getLanguageVersion
  }

  override def getProgram(statements: String*): String = statements.mkString("\n")

  override def getMethodCallSyntax(obj: String, m: String, args: String*): String = {
    s"$obj.$m(${args.mkString(",")})"
  }

  override def getMimeTypes: util.List[String] = Vector("application/coffeescript", "text/coffeescript")

  override def getNames: util.List[String] = Vector("coffeescript")

  override def getLanguageName: String = "CoffeeScript"

  override def getEngineVersion: String = "1.0"

  override def getLanguageVersion: String = "1.8.0"

  override def getExtensions: util.List[String] = Vector("coffee")

  override def getScriptEngine: ScriptEngine = new CoffeeScriptEngine {
    override def getFactory: ScriptEngineFactory = CoffeeScriptEngineFactory.this
  }

  override def getOutputStatement(toDisplay: String): String = s"print($toDisplay)"
}
