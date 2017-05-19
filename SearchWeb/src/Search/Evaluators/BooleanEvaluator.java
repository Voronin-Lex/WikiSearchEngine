package Search.Evaluators;

import Common.ContextHelper;
import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import Search.QuerryProcessors.BooleanQueryProcessor;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lex on 11.03.17.
 */
public class BooleanEvaluator extends AbstractEvaluator<Set<Integer>>{

    public final static Operator NOT = new Operator("!", 1, Operator.Associativity.RIGHT, 3);
    public final static Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 2);
    public final static Operator OR = new Operator("||", 2, Operator.Associativity.LEFT, 1);
    public final static Parameters PARAMETERS;
    private BooleanQueryProcessor booleanQueryProcessor;

    static
    {
        PARAMETERS = new Parameters();
        PARAMETERS.add(NOT);
        PARAMETERS.add(AND);
        PARAMETERS.add(OR);
        PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
    }

    public BooleanEvaluator(BooleanQueryProcessor booleanQueryProcessor)
    {
        super(PARAMETERS);
        this.booleanQueryProcessor = booleanQueryProcessor;
    }

    @Override
    protected Set<Integer> toValue(String literal, Object evaluatorContext)
    {
        try {
            return booleanQueryProcessor.GetDocuments(ContextHelper.getFirstNormalForm(literal));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Set<Integer> evaluate(Operator operator, Iterator<Set<Integer>> operands, Object evaluationContext)
    {
        if (operator==NOT)
            return antiIntersection(BooleanQueryProcessor.GetAllArticles(), operands.next());


        else if(operator==AND)
        {
            Set<Integer> firstSet = operands.next();
            Set<Integer> secondSet = operands.next();
            return intersection(firstSet,secondSet);
        }

        else if (operator==OR)
        {
            Set<Integer> firstSet = operands.next();
            Set<Integer> secondSet = operands.next();
            return union(firstSet,secondSet);
        }

        else
            return super.evaluate(operator, operands, evaluationContext);
    }


    private Set<Integer> intersection(Set<Integer> s1, Set<Integer> s2){
        return new TreeSet<>(s1.stream().filter(s2::contains).collect(Collectors.toSet()));
    }

    private Set<Integer> antiIntersection(Set<Integer> s1, Set<Integer> s2){
        return new TreeSet<>(s1.stream().filter(l -> !s2.contains(l)).collect(Collectors.toSet()));
    }

    private Set<Integer> union(Set<Integer> s1, Set<Integer> s2){
        return new TreeSet<>(Stream.concat(s1.stream(),s2.stream()).collect(Collectors.toSet()));
    }
}
