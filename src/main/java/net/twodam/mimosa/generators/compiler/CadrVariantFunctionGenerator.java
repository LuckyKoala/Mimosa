package net.twodam.mimosa.generators.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

/**
 * Generate pair accessor shorthand functions: cadr caar ... cddddr
 */
public class CadrVariantFunctionGenerator {
    private static final String FUNCTION_TEMPLATE =
            "public static MimosaType c%sr(MimosaType expr) {\n" +
            "    return wrap(expr)%s.build();\n" +
            "}\n";
    private static final String CAR_FUNCTION = ".car()";
    private static final String CDR_FUNCTION = ".cdr()";
    private static final char[] VARIANTS = new char[]{'a', 'd'};
    private static final int DEPTH_START = 2; //must be 2, check implementation below

    public static void main(String[] args) {
        new CadrVariantFunctionGenerator().generateFunctions()
                .forEach(System.out::println);
    }

    public List<String> generateFunctions() {
        int depthEnd = 4; //cddddr
        List<String> placeholders = generatePlaceHolders(VARIANTS, DEPTH_START, depthEnd);
        List<String> invocations =  placeholders.stream()
                .map(this::parseNameToFunctionInvocation)
                .collect(Collectors.toList());
        return IntStream.range(0, placeholders.size())
                .mapToObj(index -> String.format(FUNCTION_TEMPLATE, placeholders.get(index), invocations.get(index)))
                .collect(Collectors.toList());
    }

    private String parseNameToFunctionInvocation(String name) {
        StringBuilder builder = new StringBuilder(CAR_FUNCTION.length() * name.length());
        for(int i=name.length()-1; i>=0; i--) {
            char ch = name.charAt(i);
            if(ch == 'a') {
                builder.append(CAR_FUNCTION);
            } else if(ch =='d') {
                builder.append(CDR_FUNCTION);
            } else {
                throw new RuntimeException("Unsupported placeholder char " + ch);
            }
        }
        return builder.toString();
    }

    private List<String> generatePlaceHolders(char[] variants, int depthStart, int depthEnd) {
        List<String> placeholders = new ArrayList<>();
        List<SpanningTree> roots = constructNodes(variants, null);
        for(int length=depthStart; length<=depthEnd; length++) {
            int finalLength = length;
            //reuse the trees, not the stream of trees
            roots.stream()
                    //flatMap to stream of leaves
                    .flatMap(SpanningTree::leaves)
                    .forEach(node -> {
                        List<SpanningTree> leaves = constructNodes(variants, node);
                        //add one depth down
                        node.next = leaves;
                        //search upon parent link to construct a string
                        //one way from leave to root [choose this way here]
                        //multiple ways from root to leave
                        leaves.forEach(leave -> {
                                StringBuilder builder = new StringBuilder(finalLength);
                                while(leave != null) {
                                    builder.append(leave.ch);
                                    leave = leave.parent;
                                }
                                placeholders.add(builder.reverse().toString());
                        });
                    });
        }

        return placeholders;
    }

    private List<SpanningTree> constructNodes(char[] variants, SpanningTree parent) {
        List<SpanningTree> nodes = new ArrayList<>(variants.length);
        for(char ch : variants) {
            nodes.add(new SpanningTree(ch, parent, emptyList()));
        }
        return nodes;
    }

    /**
     * Trie tree
     * Spanning tree
     * DFS
     */
    class SpanningTree {
        char ch;
        SpanningTree parent;
        List<SpanningTree> next;

        SpanningTree(char ch, SpanningTree parent, List<SpanningTree> next) {
            this.ch = ch;
            this.parent = parent;
            this.next = next;
        }

        Stream<SpanningTree> leaves() {
            if(next.size() == 0) {
                return Stream.of(this);
            } else {
                return next.stream()
                        .flatMap(SpanningTree::leaves);
            }
        }
    }
}
