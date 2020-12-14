package studio.rockpile.devtools.lambda;

import org.junit.Test;

import java.text.DecimalFormat;
import java.util.function.Function;
import java.util.stream.IntStream;

// jdk8中新增的函数式接口
@FunctionalInterface
interface InterfaceFunc {
    // 函数式接口中只能有一个方法
    int compute(int n);
}

class MyMoney {
    private Integer money;

    public MyMoney(int money) {
        this.money = money;
    }

    // 使用JDK8自带的函数接口Function<T,R>
    // <T> the type of the input to the function
    // <R> the type of the result of the function
    public void printMoney(Function<Integer, String> formatter) {
        System.out.println("费用: " + formatter.apply(this.money));
    }
}

public class LambdaDemo {

    @Test
    public void test() {
        try {
            int nums[] = {33, 50, -50, 99, 666};
            // java中的回调函数
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("rockpile 1");
                }
            }).start();
            // lambda表达式的等价写法（回调函数）
            new Thread(() -> System.out.println("rockpile 2")).start();

            // jdk8中新特性
            int min = IntStream.of(nums).parallel().min().getAsInt();
            System.out.println("min : " + min);

            // 函数式接口
            // InterfaceFunc square = (n) -> n*n;
            InterfaceFunc square = n -> n * n;
            System.out.println("square.compute(5) : " + String.valueOf(square.compute(5)));
            // 使用函数式接口，可以支持链式操作
            MyMoney me = new MyMoney(10000);
            Function<Integer, String> fun = m -> new DecimalFormat("#,###").format(m);
            me.printMoney(fun.andThen(s -> "人民币" + s));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
