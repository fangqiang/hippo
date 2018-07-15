package pers.fq.hippo.biz;

import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/17
 */
public class ExpressService {

    static Map<String, CompiledExpression> cache = new ConcurrentHashMap<>();

    public static CompiledExpression get(String logic){
        CompiledExpression compiledExpression = cache.get(logic);
        if(compiledExpression == null){
            ExpressionCompiler compiler = new ExpressionCompiler(logic);
            CompiledExpression compile = compiler.compile();
            cache.put(logic, compile);
            return compile;
        }else{
            return compiledExpression;
        }
    }
}
