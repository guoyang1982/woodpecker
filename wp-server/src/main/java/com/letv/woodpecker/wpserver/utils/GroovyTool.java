package com.letv.woodpecker.wpserver.utils;

import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.lang.Binding;

import javax.script.*;
import java.util.List;
import java.util.Map;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/7/12 下午3:25
 */
public class GroovyTool {

    private volatile static GroovyTool groovyTool = new GroovyTool();

    private GroovyTool() {
    }

    public static GroovyTool getInstance() {
        return groovyTool;
    }

    /**
     * @param filepath groovy脚本文件的路径(当参数为null默认指定工程路径下的groovy文件夹)
     * @param filename groovy脚本文件名字
     * @param params   执行脚本的参数
     * @return 返回执行结果
     * @desc 描述
     */
    public Object runGroovyScriptByFile(String[] filepath, String filename, Map<String, Object> params) {

        if (filepath == null || filepath.length == 0)
            filepath = new String[]{"grovvy\\"};// 定义Groovy脚本引擎的根路径

        try {
            GroovyScriptEngine engine = new GroovyScriptEngine(filepath);
            return engine.run(filename, new Binding(params));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param filepath groovy脚本文件的路径(当参数为null默认指定工程路径下的groovy文件夹)
     * @param filename groovy脚本文件名字
     * @param funname  执行指定的方法名称
     * @param params   执行脚本的参数
     * @return 返回执行结果
     * @desc 描述
     */
    public Object runGroovyScriptByFile(String[] filepath, String filename, String funname, Object[] params) {

        if (filepath == null || filepath.length == 0)
            filepath = new String[]{"grovvy\\"};// 定义Groovy脚本引擎的根路径
        try {
            GroovyScriptEngine engine = new GroovyScriptEngine(filepath);
            Script script = engine.createScript(filename, new Binding());
            return script.invokeMethod(funname, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param script 要执行的脚本 通过字符串传入，应用场景 如从数据库中读取出来的脚本等
     * @param params 执行grovvy需要传入的参数
     * @return 脚本执行结果
     * @desc 执行groovy脚本(不指定方法)
     */
    public Object runGroovyScript(String script, Map<String, Object> params) {
        if (script == null || "".equals(script))
            throw new RuntimeException("方法runGroovyScript无法执行，传入的脚本为空");

        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("groovy");
            Bindings bindings = engine.createBindings();
            bindings.putAll(params);
            return engine.eval(script, bindings);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param script  要执行的脚本 通过字符串传入，应用场景 如从数据库中读取出来的脚本等
     * @param funName 要执行的方法名
     * @param params  执行grovvy需要传入的参数
     * @return
     * @desc 执行groovy脚本(需要指定方法名)
     */
    public Object runGroovyScript(String script, String funName, Object[] params) {
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("groovy");
            // String HelloLanguage = "def hello(language) {return \"Hello
            // $language\"}";
            engine.eval(script);
            Invocable inv = (Invocable) engine;
            return inv.invokeFunction(funName, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getScriptEngineFactoryList() {
        ScriptEngineManager manager = new ScriptEngineManager();

        List<ScriptEngineFactory> factories = manager.getEngineFactories();

        for (ScriptEngineFactory factory : factories) {

            System.out.printf(
                    "Name: %s%n" + "Version: %s%n" + "Language name: %s%n" + "Language version: %s%n"
                            + "Extensions: %s%n" + "Mime types: %s%n" + "Names: %s%n",
                    factory.getEngineName(), factory.getEngineVersion(), factory.getLanguageName(),
                    factory.getLanguageVersion(), factory.getExtensions(), factory.getMimeTypes(), factory.getNames());
            // 得到当前的脚本引擎

            ScriptEngine engine = factory.getScriptEngine();
            System.out.println(engine);
        }
    }

}

