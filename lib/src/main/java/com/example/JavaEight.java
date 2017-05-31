package com.example;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class JavaEight {

    public static void main(String[] args) {

        Arrays.asList("a", "b", "c").forEach(e -> System.out.println(e));

        Arrays.asList("a", "b", "c").sort((e1, e2) -> e1.compareTo(e2));
        Arrays.asList("a", "b", "c").sort((e1, e2) -> {
            int result = e1.compareTo(e2);
            System.out.println(result);
            return result;
        });

        Converter<String, Integer> converter = (from -> Integer.valueOf(from));
        Integer converted = converter.convert("123");
        System.out.println(converted);

        //传递静态方法
        Converter<String, Integer> converter1 = Integer::valueOf;
        Integer converted1 = converter1.convert("123");
        System.out.println(converted1);

        //传递对象方法
        Something something = new Something();
        Converter<String, String> converter2 = something::startWith;
        String converted2 = converter2.convert("java");
        System.out.println(converted2);

        //使用关键字:: 实现构造方法
        PersonFactory<Person> personFactory = Person::new;
        Person person = personFactory.create("Peter", "Parker");

        //在lambda中实现表达式
        final int num = 1;
        Converter<Integer, String> stringConverter = from -> String.valueOf(from) + num;
        stringConverter.convert(2);
        System.out.println(stringConverter);

        //断言
        Predicate<String> predicate = s -> s.length() > 0;
        predicate.test("foo");  // 执行参数
        predicate.negate().test("foo");  //逻辑否定 相当于非

        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNoEmpty = isEmpty.negate();


        //Functions 功能性接口 分为Compose，andThen两个方法
        Function<Integer, Integer> times = e -> e * 2;
        Function<Integer, Integer> times1 = e -> e * e;
        //compose方法自身先执行，再把执行结果传递给调用者执行。andThen先执行前一步的调用者，获取前一步执行结果后再执行
        times.compose(times1).apply(4);  // 32
        times.andThen(times1).apply(4);  // 64


        //Suppliers  能生成给定泛型类型的结果
        Supplier<Person> personSupplier = Person::new;
        personSupplier.get(); //new Person();


        //Consumer 表示在单个输入参数上要执行的操作
        Consumer<Person> greeter = (p -> System.out.println("Hello" + p.firstName));
        greeter.accept(new Person("Luke", "Skywalker"));


        //Comparators  比较器
        Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
        Person p1 = new Person("John", "Doe");
        Person p2 = new Person("Alice", "Wonderland");

        comparator.compare(p1, p2);
        comparator.reversed().compare(p1, p2);  //反序比较


        //Optionals
        Optional<String> optional = Optional.of("bam");
        optional.isPresent(); // 存在返回true 否则false
        optional.get();       // 返回false
        optional.orElse("fallback");
        optional.ifPresent(s -> System.out.println(s.charAt(0)));


        //Streams  流式操作
        List<String> stringCollection = new ArrayList<>();
        stringCollection.add("ddd2");
        stringCollection.add("aaa2");
        stringCollection.add("bbb1");
        stringCollection.add("aaa1");
        stringCollection.add("bbb3");
        stringCollection.add("ccc");
        stringCollection.add("bbb2");
        stringCollection.add("ddd1");

        //filter  过滤
        stringCollection.stream().filter(s -> s.startsWith("a")).forEach(System.out::print);
        //Sorted  排序
        stringCollection.stream().sorted().filter(s -> s.startsWith("a")).forEach(System.out::print);
        //Map     排序
        stringCollection.stream().map(String::toUpperCase).sorted((a, b) -> b.compareTo(a)).forEach(System.out::print);
        //Match   用来确定某种断言是否与stream匹配
        boolean anyStartWithA = stringCollection.stream().anyMatch(s -> s.startsWith("a")); //匹配  true
        System.out.println(anyStartWithA);

        boolean allStartWithA = stringCollection.stream().allMatch(s -> s.startsWith("a"));  //全部匹配  false
        System.out.println(allStartWithA);

        boolean noneStartsWithZ = stringCollection.stream().noneMatch((s) -> s.startsWith("z"));  //全部不匹配 true
        System.out.println(noneStartsWithZ);
        //count   用来确定stream数量
        long startWithB = stringCollection.stream().filter((s) -> s.startsWith("b")).count();
        System.out.println(startWithB);
        //reduce  返回值为使用Optional接口包装的reduce值 改造后stream的值
        Optional<String> reduced = stringCollection.stream().sorted().reduce((s1, s2) -> s1 + "#" + s2);
        reduced.ifPresent(System.out::println);


        //Parallel Streams   Parallel Streams能在多个线程上并发地执行
        int max = 1000000;
        List<String> strings = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            strings.add(uuid.toString());
        }

        // 正常模式
        long t0 = System.nanoTime();
        long count = strings.stream().sorted().count();
        System.out.println(count);
        long t1 = System.nanoTime();

        long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("sequential sort took: %d ms", millis));

        // Parallel  Sort
        long t01 = System.nanoTime();
        long count1 = strings.parallelStream().sorted().count();
        long t11 = System.nanoTime();

        long millis1 = TimeUnit.NANOSECONDS.toMillis(t11 - t01);
        System.out.println(String.format("parallel sort took: %d ms", millis1));


        // Map
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.putIfAbsent(i, "val " + i);
        }
        map.forEach((id, val) -> System.out.println(val));
        map.computeIfPresent(3, (num1, val) -> num1 + val);
        System.out.println(map.get(3));

        //添加操作
        map.computeIfPresent(9, (num1, val) -> null);
        map.containsKey(9);
        System.out.println(map.containsKey(9));

        map.computeIfAbsent(23, num2 -> "val = " + num2);
        map.containsKey(23);
        System.out.println(map.containsKey(23));

        map.computeIfAbsent(3, num4 -> "bam");
        map.get(3);
        System.out.println(map.get(3));

        // 删除和Merge操作
        map.remove(3, "val3");
        map.get(3);   // 不存在 val3  返回的还是val33
        System.out.println(map.get(3));

        map.remove(3, "val33");
        map.get(3);
        System.out.println(map.get(3));

        map.getOrDefault(42, "not found");
        System.out.println("getOrDefault = " + map.getOrDefault(42, "not found"));

        // merge方法在key不存在时，可以向map中添加key/value，当key存在时，可以对这个key对应的value执行merge操作
        map.merge(9, "val9", (value, newValue) -> value.concat(newValue));
        map.get(9);
        System.out.println("merge = " + map.get(9));

        map.merge(9, "concat", (value, newValue) -> value.concat(newValue));
        map.get(9);
        System.out.println("merge = " + map.get(9));

        

        //Date API

        // Clock
        Clock clock = Clock.systemDefaultZone();
        long mills = clock.millis();
        System.out.println("mills = " + mills);
        Instant instant = clock.instant();
        Date legacyDate = Date.from(instant);
        System.out.println("legacyDate = " + legacyDate);


        // Timezones
        System.out.println(ZoneId.getAvailableZoneIds());
        ZoneId zone1 = ZoneId.of("Europe/Berlin");
        ZoneId zone2 = ZoneId.of("Brazil/East");
        System.out.println("zone1 = " + zone1.getRules());
        System.out.println("zone2 = " + zone2.getRules());

        // LocalTime
        LocalTime now1 = LocalTime.now(zone1);
        LocalTime now2 = LocalTime.now(zone2);
        System.out.println("LocalTime = " + now1.isBefore(now2));

        long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
        long minuteBetween = ChronoUnit.MINUTES.between(now1, now2);
        System.out.println("hoursBetween = " + hoursBetween );
        System.out.println("minuteBetween = " + minuteBetween );

        LocalTime late = LocalTime.of(23, 59, 59);
        System.out.println(late);

        DateTimeFormatter germanFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.GERMAN);
        LocalTime leetTime = LocalTime.parse("13:37", germanFormatter);
        System.out.println(leetTime);


        //LocalDate
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
        LocalDate yesterday = today.minusDays(2);
        System.out.println("today = " + today + " tomorrow = " + tomorrow + " yesterday = " + yesterday);

        LocalDate independenceDay = LocalDate.of(2014, Month.JULY, 4);
        DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
        System.out.println(dayOfWeek);

        DateTimeFormatter germanFormatter1 = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN);
        LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter1);
        System.out.println(xmas);   // 2014-12-24


        // LocalDateTime
        LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);

        DayOfWeek dayOfWeek1 = sylvester.getDayOfWeek();
        System.out.println(dayOfWeek1);      // WEDNESDAY

        Month month = sylvester.getMonth();
        System.out.println(month);          // DECEMBER

        long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
        System.out.println(minuteOfDay);    // 1439

                Instant instant1 = sylvester
                .atZone(ZoneId.systemDefault())
                .toInstant();

        Date legacyDate1 = Date.from(instant1);
        System.out.println(legacyDate1);     // Wed Dec 31 23:59:59 CET 2014

        // 自定义pattern
        DateTimeFormatter formatter =
                DateTimeFormatter
                        .ofPattern("MMM dd, yyyy - HH:mm");

        LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
        String string = formatter.format(parsed);
        System.out.println(string);     // Nov 03, 2014 - 07:13

    }

    static class Something {

        public Something() {
        }

        String startWith(String s) {
            return String.valueOf(s.charAt(0));
        }
    }

    static class Person {
        String firstName;
        String lastName;

        public Person() {
        }

        Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    static class Lambda4 {
        static int outerStaticNum;
        int outerNum;

        void testScopes() {
            Converter<Integer, String> stringConverter = from -> {
                outerNum = 23;
                return String.valueOf(from);
            };

            Converter<Integer, String> stringConverter1 = from -> {
                outerStaticNum = 72;
                return String.valueOf(outerStaticNum);
            };
        }

    }


}
