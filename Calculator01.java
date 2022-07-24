import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  (1)ʵ���ܹ������ŵļӼ��˳��������磨1 + 2 * ��3 - 5���� * ��2 - 1��
 *
 * ��2���ܹ��ж�����ı��ʽ�Ƿ���ȷ���Լ�ʵ���쳣���׳���������Ӧ�Ĵ���
 *
 * ��3���ܹ��Լ���������������жϣ��Լ�ʵ���쳣���׳���������Ӧ�Ĵ���
 *
 * ��4���ܹ��Ը�������������
 *
 * ��5���Լ�д����������в���
 */
public class Calculator01 {
    // ������ʽ����Ϸ���
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("[0-9\\.+-/*()= ]+");
    // ��ϣ��洢��������ȼ�map
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
     * @param str ���ʽ�洢Ϊ�ַ�������
     */
    public static double calculate(String str){
        // �ǿ�У��
        if (null == str || "".equals(str.trim())) {
            throw new IllegalArgumentException("���ʽ����Ϊ�գ�");
        }

        // ���ʽ�ַ��Ϸ���У��
        Matcher matcher = EXPRESSION_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("���ʽ���зǷ��ַ���");
        }

        Stack<String> optStack = new Stack<>(); // �����ջ
        Stack<BigDecimal> numStack = new Stack<>(); // ��ֵջ����ֵ��BigDecimal�洢���㣬���⾫�ȼ�������
        StringBuilder curNumBuilder = new StringBuilder(16); // ��ǰ���ڶ�ȡ�е���ֵ�ַ�׷����

        //�����沨�����ʽ�ṹ����
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ') {       //Ĭ���׳�����
                if (c >= '0' && c <= '9' || c == '.'){    //�洢����
                    curNumBuilder.append(c);
                }else {
                    if (curNumBuilder.length() > 0){
                        //���׷������ֵ��˵��Ϊ���֣�ѹ����ֵջ�����׷����
                        numStack.push(new BigDecimal(curNumBuilder.toString()));
                        curNumBuilder.delete(0, curNumBuilder.length());
                    }

                    String curOpt = String.valueOf(c);
                    if (optStack.empty()) {
                        // �����ջջ��Ϊ����ֱ����ջ
                        optStack.push(curOpt);
                    }else {         //�������ջ��Ϊ��ʱ�����������
                        if (curOpt.equals("(")){
                            //������ֱ���������ջ
                            optStack.push(curOpt);
                        } else if (curOpt.equals(")")) {
                                //������ʱ���Ƚ��������ڵ�����
                            directCalc(optStack, numStack, true);
                        } else if (curOpt.equals("=")) {
                            directCalc(optStack,numStack,false);
                            return numStack.pop().doubleValue();
                        }else {
                            //�����Ϊ+-*/ʱ����ջ���Ƚ�
                            comparAndCal(optStack,numStack,curOpt);
                        }
                    }
                }
            }
        }
        // ���ʽ�����ԵȺŽ�β�ĳ���
        if (curNumBuilder.length() > 0) {
            // ���׷������ֵ��˵��֮ǰ��ȡ���ַ�����ֵ�����Ҵ�ʱ�Ѿ�������ȡ��һ����ֵ
            numStack.push(new BigDecimal(curNumBuilder.toString()));
        }
        directCalc(optStack, numStack, false);
        return numStack.pop().doubleValue();
    }

    /**
     * �õ�ǰ�������ջ��������Աȣ����ջ����������ȼ����ڻ�ͬ���ڵ�ǰ�������
     * ��ִ��һ�ζ�Ԫ���㣨�ݹ�Ƚϲ����㣩������ǰ�������ջ
     * @param optStack
     * @param numStack
     * @param curOpt
     */
    private static void comparAndCal(Stack<String> optStack, Stack<BigDecimal> numStack, String curOpt) {
        //�Ƚϵ�ǰ�������ջ����������ȼ�
        String peekOpt = optStack.peek();
        int priority = getPriority(peekOpt, curOpt);
        if (priority == -1 || priority == 0){
            // ջ����������ȼ����ͬ��������һ�ζ�Ԫ����
            String opt = optStack.pop(); // ��ǰ������������
            BigDecimal num2 = numStack.pop(); // ��ǰ���������ֵ2
            BigDecimal num1 = numStack.pop(); // ��ǰ���������ֵ1
            BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);

            numStack.push(bigDecimal);
            // ������ջ�����������������Ҫ�ٴδ���һ�αȽ��ж��Ƿ���Ҫ�ٴζ�Ԫ����
            if (optStack.empty()) {
                optStack.push(curOpt);
            } else {
                comparAndCal(optStack, numStack, curOpt);
            }
        } else {
            // ��ǰ��������ȼ��ߣ���ֱ����ջ
            optStack.push(curOpt);
        }
    }

    /**
     *
     * @param peekOpt
     * @param curOpt
     * @return  ջ�����ȼ��󷵻�-1��0
     */
    private static int getPriority(String peekOpt, String curOpt) {
        return OPT_PRIORITY_MAP.get(curOpt) - OPT_PRIORITY_MAP.get(peekOpt);
    }

    /**
     *
     * @param optStack
     * @param numStack
     * @param b   true��ʾ�������ŵ�����
     */
    private static void directCalc(Stack<String> optStack, Stack<BigDecimal> numStack, boolean b) {
        String opt = optStack.pop(); // ��ǰ������������
        BigDecimal num2 = numStack.pop(); // ��ǰ���������ֵ2
        BigDecimal num1 = numStack.pop(); // ��ǰ���������ֵ1
        BigDecimal bigDecimal = floatingPointCalc(opt, num1, num2);

        numStack.push(bigDecimal);

        if (b){
            if ("(".equals(optStack.peek())){
                //����������ֹͣ������ȥ��������
                optStack.pop();
            }else {
                directCalc(optStack,numStack,b);
            }
        }else {
            if (!optStack.empty()) {
                // �Ⱥ�����ֻҪջ�л���������ͼ�������
                directCalc(optStack, numStack, b);
            }
        }
    }

    private static BigDecimal floatingPointCalc(String opt, BigDecimal num1, BigDecimal num2) {    //ͳһ������ջ�����ֶԺ���ջ�����ֽ��ж�Ԫ����
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
        System.out.println("��ȷ���=" + value1 + ", ʵ�ʼ�����=" + value2);
        return Math.abs(value1 - value2) <= 0.0001;
    }

    public static void main(String[] args) {
        // ������������
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
