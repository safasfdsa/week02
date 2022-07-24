import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  (1)实现能够带括号的加减乘除法，例如（1 + 2 * （3 - 5）） * （2 - 1）
 *
 * （2）能够判断输入的表达式是否正确，自己实现异常类抛出并捕获相应的错误
 *
 * （3）能够对计算结果的溢出做出判断，自己实现异常类抛出并捕获相应的错误
 *
 * （4）能够对浮点数进行运算
 *
 * （5）自己写出测试类进行测试
 */
public class Calculator01 {
    // 正则表达式检验合法性
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("[0-9\\.+-/*()= ]+");
    // 哈希表存储运算符优先级map
    private static final Map<String, Integer> OPT_PRIORITY_MAP = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 6968472606692771458L;

        {
            put("(", 0);
            put("+", 2);
            put("-", 2);
            put("*", 3);
            put("/", 3);
            put(")", 7);
            put("=", 20);
        }
    };

    /**
     *
     * @param str 表达式存储为字符串类型
     */
    public static double calculate(String str){
        // 非空校验
        if (null == str || "".equals(str.trim())) {
            throw new IllegalArgumentException("表达式不能为空！");
        }

        // 表达式字符合法性校验
        Matcher matcher = EXPRESSION_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("表达式含有非法字符！");
        }

        Stack<String> optStack = new Stack<>(); // 运算符栈
        Stack<BigDecimal> numStack = new Stack<>(); // 数值栈，数值以BigDecimal存储计算，避免精度计算问题
        StringBuilder curNumBuilder = new StringBuilder(16); // 当前正在读取中的数值字符追加器

        //利用逆波兰表达式结构计算
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ') {       //默认抛出“”
                if (c >= '0' && c <= '9' || c == '.'){    //存储数字
                    curNumBuilder.append(c);
                }else {
                    if (curNumBuilder.length() > 0){
                        //如果追加器有值，说明为数字，压入数值栈，清空追加器
                        numStack.push(new BigDecimal(curNumBuilder.toString()));
                        curNumBuilder.delete(0, curNumBuilder.length());
                    }

                    String curOpt = String.valueOf(c);
                    if (optStack.empty()) {
                        // 运算符栈栈顶为空则直接入栈
                        optStack.push(curOpt);
                    }else {         //当运算符栈不为空时，分四种情况
                        if (curOpt.equals("(")){
                            //左括号直接入运算符栈
                            optStack.push(curOpt);
                        } else if (curOpt.equals(")")) {
                                //右括号时，先进行括号内的运算
                            directCalc(optStack, numStack, true);
                        } else if (curOpt.equals("=")) {
                            directCalc(optStack,numStack,false);
                            return numStack.pop().doubleValue();
                        }else {
                            //运算符为+-*/时，与栈顶比较
                            comparAndCal(optStack,numStack,curOpt);
                        }
                    }
                }
            }
        }
        // 表达式不是以等号结尾的场景
        if (curNumBuilder.length() > 0) {
            // 如果追加器有值，说明之前读取的字符是数值，而且此时已经完整读取完一个数值
            numStack.push(new BigDecimal(curNumBuilder.toString()));
        }
        directCalc(optStack, numStack, false);
        return numStack.pop().doubleValue();
    }

    /**
     * 拿当前运算符和栈顶运算符对比，如果栈顶运算符优先级高于或同级于当前运算符，
     * 则执行一次二元运算（递归比较并计算），否则当前运算符入栈
     * @param optStack
     * @param numStack
     * @param curOpt
     */
    private static void comparAndCal(Stack<String> optStack, Stack<BigDecimal> numStack, String curOpt) {
        //比较当前运算符和栈顶运算符优先级
        String peekOpt = optStack.peek();
        int priority = getPriority(peekOpt, curOpt);
        if (priority == -1 || priority == 0){
            // 栈顶运算符优先级大或同级，触发一次二元运算
            String opt = optStack.pop(); // 当前参与计算运算符
            BigDecimal num2 = numStack.pop(); // 当前参与计算数值2
            BigDecimal num1 = numStack.pop(); // 当前参与计算数值1
            BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);

            numStack.push(bigDecimal);
            // 运算完栈顶还有运算符，则还需要再次触发一次比较判断是否需要再次二元计算
            if (optStack.empty()) {
                optStack.push(curOpt);
            } else {
                comparAndCal(optStack, numStack, curOpt);
            }
        } else {
            // 当前运算符优先级高，则直接入栈
            optStack.push(curOpt);
        }
    }

    /**
     *
     * @param peekOpt
     * @param curOpt
     * @return  栈顶优先级大返回-1，0
     */
    private static int getPriority(String peekOpt, String curOpt) {
        return OPT_PRIORITY_MAP.get(curOpt) - OPT_PRIORITY_MAP.get(peekOpt);
    }

    /**
     *
     * @param optStack
     * @param numStack
     * @param b   true表示进行括号的运算
     */
    private static void directCalc(Stack<String> optStack, Stack<BigDecimal> numStack, boolean b) {
        String opt = optStack.pop(); // 当前参与计算运算符
        BigDecimal num2 = numStack.pop(); // 当前参与计算数值2
        BigDecimal num1 = numStack.pop(); // 当前参与计算数值1
        BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);

        numStack.push(bigDecimal);

        if (b){
            if ("(".equals(optStack.peek())){
                //遇到左括号停止，并且去除左括号
                optStack.pop();
            }else {
                directCalc(optStack,numStack,b);
            }
        }else {
            if (!optStack.empty()) {
                // 等号类型只要栈中还有运算符就继续计算
                directCalc(optStack, numStack, b);
            }
        }
    }

    private static BigDecimal floatingPointCalc(String opt, BigDecimal num1, BigDecimal num2) {    //统一用早入栈的数字对后入栈的数字进行二元运算
        BigDecimal b = new BigDecimal(0);
        switch (opt){
            case "+":
                b = num1.add(num2);
                break;
            case "-":
                b = num1.subtract(num2);
                break;
            case "*":
                b = num1.multiply(num2);
                break;
            case "/":
                b = num1.divide(num2,10,BigDecimal.ROUND_HALF_DOWN);
                break;
            default:
                break;
        }
        return b;
    }

    private static boolean isDoubleEquals(double value1, double value2) {
        System.out.println("正确结果=" + value1 + ", 实际计算结果=" + value2);
        return Math.abs(value1 - value2) <= 0.0001;
    }

    public static void main(String[] args) {
        // 几个测试数据
        System.out.println(isDoubleEquals(-39.5, calculate(" 2 + 3/2 * 3 - 4 *(2 + 5 -2*4/2+9) + 3 + (2*1)-3= ")));
        System.out.println(isDoubleEquals(60.3666,calculate("9*2+1/3*2-4+(3*2/5+3+3*6)+3*5-1+3+(4+5*1/2)=")));
        System.out.println(isDoubleEquals(372.8,calculate(" 9.2 *(20-1)-1+199 = ")));
        System.out.println(isDoubleEquals(372.8, calculate(" 9.2 *(20-1)-1+199" )));
        System.out.println(isDoubleEquals(372.8, calculate(" 9.2 *(20-1)-1+199")));
        System.out.println(isDoubleEquals(-29, calculate(" 9 *(20-1)-(1+199) ")));
        System.out.println(isDoubleEquals(1.0E24, calculate("1000000000000*1000000000000 = ")));
        System.out.println(isDoubleEquals(-3.0, calculate("(1 + 2 *(3 - 5)) * (2 - 1)")));
    }
}
