package checkipaddr;/*
    ### about JMH
    https://javapapers.com/java/java-micro-benchmark-with-jmh/
    https://blog.goyello.com/2017/06/19/testing-code-performance-jmh-tool/

    ### random ip address create
    https://www.browserling.com/tools/random-ip

 */

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 자료구조별로 임의의 (10만건)ip 목록을 저장하고, 조회시 성능을 측정한다.
 * 자료구조는 HashMap vs Set 로 비교
 * 조회대상은 String vs byte[] 로 비교
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.NANOSECONDS)
public class CheckIpAddr {

    static Map<byte[], String> ipAddrMapTableKeyIsBytes = new HashMap<byte[], String>();

    static Map<String, String> ipAddrMapTableKeyIsString = new HashMap<String, String>();

    static ArrayList<byte[]> ipAddrListTableForByteCompare = new ArrayList<>();
    static ArrayList<String> ipAddrListTable = new ArrayList<>();

    static Set<byte[]> ipAddrSetTableKeyIsBytes = new HashSet<byte[]>();
    static Set<String> ipAddrSetTableKeyIsString = new HashSet<String>();

    final static String TARGET_IP_ADDR = "34.229.151.181";

    final static byte[] TARGET_IP_ADDR_B = "34.229.151.181".getBytes();


    @Setup
    public void initNAutoCloseable(){

        File file = new File("resources/ip_addr.txt");

        try(BufferedReader reader =new BufferedReader(new FileReader(file))){

            String text = null;
            // repeat until all lines is read
            while ((text = reader.readLine()) != null){

                //System.out.println(text);
                ipAddrMapTableKeyIsBytes.put(text.getBytes(), text);

                ipAddrMapTableKeyIsString.put(text, text);

                ipAddrSetTableKeyIsBytes.add(text.getBytes());

                ipAddrSetTableKeyIsString.add(text);

                /*ipAddrListTableForByteCompare.add(text.getBytes());
                ipAddrListTable.add(text);

                ipAddrSetTable.add(text);*/
            }

        }catch (IOException ioe){
            ioe.printStackTrace();
        }


        System.out.println("ipAddrList size : "+ipAddrMapTableKeyIsBytes.size());

    }
    //HashMap
    @Benchmark
    public void benchmarkHashMapKeyIsString(){

        ipAddrMapTableKeyIsString.containsKey(TARGET_IP_ADDR);
    }

    //HashMap
    @Benchmark
    public void benchmarkHashMapKeyIsBytes(){

        ipAddrMapTableKeyIsBytes.containsKey(TARGET_IP_ADDR_B);
    }

    //Set
    @Benchmark
    public void benchmarkSetKeyIsString(){

        ipAddrSetTableKeyIsString.contains(TARGET_IP_ADDR);
    }

    //Set
    @Benchmark
    public void benchmarkSetKeyIsByte(){

        ipAddrSetTableKeyIsBytes.contains(TARGET_IP_ADDR_B);
    }

    /*
    //List
    @Benchmark
    public void benchmarkArrayListForByteCompare(){

        ipAddrListTable.contains(TARGET_IP_ADDR.getBytes());
    }

    //List
    @Benchmark
    public void benchmarkArrayList(){

        ipAddrListTable.contains(TARGET_IP_ADDR);
    }

    */

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(CheckIpAddr.class.getSimpleName()).warmupIterations(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
