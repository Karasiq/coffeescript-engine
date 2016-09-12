package com.karasiq.coffeescript

import java.io.{BufferedReader, Reader}
import java.util
import javax.script._

import scala.language.postfixOps

private sealed abstract class CoffeeScriptEngine extends AbstractScriptEngine with Invocable with Compilable {
  private val engineManager = new ScriptEngineManager()

  private val javaScriptEngine: ScriptEngine = engineManager.getEngineByName("javascript")

  private lazy val compiler = CoffeeScriptCompiler(javaScriptEngine)

  override def setBindings(bindings: Bindings, scope: Int) = {
    super.setBindings(bindings, scope)
    javaScriptEngine.setBindings(bindings, scope)
  }

  override def getContext = {
    javaScriptEngine.getContext
  }

  override def setContext(ctxt: ScriptContext) = {
    super.setContext(ctxt)
    javaScriptEngine.setContext(ctxt)
  }

  override def getBindings(scope: Int) = {
    javaScriptEngine.getBindings(scope)
  }

  override def eval(script: String, context: ScriptContext): AnyRef = {
    javaScriptEngine.eval(compiler.compile(script), context)
  }

  private def readerToString(reader: Reader): String = {
    val bufferedReader = new BufferedReader(reader)
    Iterator.continually(bufferedReader.readLine()).takeWhile(null ne).mkString("\r\n")
  }

  override def eval(reader: Reader, context: ScriptContext): AnyRef = {
    this.eval(readerToString(reader), context)
  }

  override def createBindings(): Bindings = new SimpleBindings()

  private def asInvocable[T](f: Invocable ⇒ T): T = {
    javaScriptEngine match {
      case i: Invocable ⇒
        f(i)

      case _ ⇒
        throw new IllegalArgumentException("Not invocable")
    }
  }

  override def invokeMethod(thiz: scala.Any, name: String, args: AnyRef*): AnyRef = {
    asInvocable[AnyRef](_.invokeMethod(thiz, name, args:_*))
  }

  override def invokeFunction(name: String, args: AnyRef*): AnyRef = {
    asInvocable[AnyRef](_.invokeFunction(name, args:_*))
  }

  override def getInterface[T](clasz: Class[T]): T = {
    asInvocable[T](_.getInterface(clasz))
  }

  override def getInterface[T](thiz: scala.Any, clasz: Class[T]): T = {
    asInvocable[T](_.getInterface(thiz, clasz))
  }

  override def compile(script: Reader): CompiledScript = compile(readerToString(script))

  override def compile(script: String): CompiledScript = javaScriptEngine match {
    case c: Compilable ⇒
      c.compile(compiler.compile(script))

    case _ ⇒
      new CompiledScript {
        lazy val cached = compiler.compile(script)

        override def eval(context: ScriptContext): AnyRef = javaScriptEngine.eval(cached, context)

        override def getEngine: ScriptEngine = javaScriptEngine
      }
  }
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
    case _ ⇒ null
  }

  override def getProgram(statements: String*): String = statements.mkString("\n")

  override def getMethodCallSyntax(obj: String, m: String, args: String*): String = {
    s"$obj.$m(${args.mkString(",")})"
  }

  override def getMimeTypes: util.List[String] = Vector("application/coffeescript", "text/coffeescript")

  override def getNames: util.List[String] = Vector("coffeescript")

  override def getLanguageName: String = "CoffeeScript"

  override def getEngineVersion: String = "1.0"

  override def getLanguageVersion: String = "1.10.0"

  override def getExtensions: util.List[String] = Vector("coffee")

  override def getScriptEngine: ScriptEngine = new CoffeeScriptEngine {
    override def getFactory: ScriptEngineFactory = CoffeeScriptEngineFactory.this
  }

  override def getOutputStatement(toDisplay: String): String = s"print($toDisplay)"
}
