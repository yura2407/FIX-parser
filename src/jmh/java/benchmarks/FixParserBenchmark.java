package benchmarks;

import org.fixParser.Encoding;
import org.fixParser.FixEncoder;
import org.fixParser.FixParser;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class FixParserBenchmark {
    @State(Scope.Benchmark)
    public static class MyState {
        private static final String[] MESSAGES = new String[]{
                "8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TESTBUY1\u000152=20180920-18:14:19.508\u000156=TESTSELL1\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20180920-18:14:19.492\u000110=092\u0001",
                "8=FIX.4.49=28935=834=109049=TESTSELL152=20180920-18:23:53.67156=TESTBUY16=113.3511=63673064027889863414=3500.000000000015=USD17=2063673064633531000021=231=113.3532=350037=2063673064633531000038=700039=140=154=155=MSFT60=20180920-18:23:53.531150=F151=3500453=1448=BRK2447=D452=110=151",
                "8=FIX.4.4\u00019=75\u000135=A\u000134=1092\u000149=TESTBUY1\u000152=20180920-18:24:59.643\u000156=TESTSELL1\u000198=0\u0001108=60\u000110=178\u0001",
                "8=FIX.4.4\u00019=63\u000135=5\u000134=1091\u000149=TESTBUY1\u000152=20180920-18:24:58.675\u000156=TESTSELL1\u000110=138\u0001",
                "8=FIX.4.2\u00019=163\u000135=D\u000134=972\u000149=TESTBUY3\u000152=20190206-16:25:10.403\u000156=TESTSELL3\u000111=141636850670842269979\u000121=2\u000138=100\u000140=1\u000154=1\u000155=AAPL\u000160=20190206-16:25:08.968\u0001207=TO\u00016000=TEST1234\u000110=106\u0001",
                "8=FIX.4.2\u00019=271\u000135=8\u000134=974\u000149=TESTSELL3\u000152=20190206-16:26:09.059\u000156=TESTBUY3\u00016=174.51\u000111=141636850670842269979\u000114=100.0000000000\u000117=3636850671684357979\u000120=0\u000121=2\u000131=174.51\u000132=100.0000000000\u000137=1005448\u000138=100\u000139=2\u000140=1\u000154=1\u000155=AAPL\u000160=20190206-16:26:08.435\u0001150=2\u0001151=0.0000000000\u000110=194\u0001",
                "8=FIX.4.2\u00019=74\u000135=A\u000134=978\u000149=TESTSELL3\u000152=20190206-16:29:19.208\u000156=TESTBUY3\u000198=0\u0001108=60\u000110=137\u0001",
                "8=FIX.4.2\u00019=62\u000135=5\u000134=977\u000149=TESTSELL3\u000152=20190206-16:28:51.518\u000156=TESTBUY3\u000110=092\u0001"
        };
        private final Random random = new Random();
        private final byte[][] customBinaryMessagePool = new byte[MESSAGES.length][];
        private final byte[][] defaultBinaryMessagePool = new byte[MESSAGES.length][];
        @Param({"CUSTOM", "ASCII"})
        private Encoding encoding;

        private final HashMap<Integer, String> inputMapNonRepetitive = new HashMap<>(
                Map.of(8, "random_val",
                        35, "",
                        52, "random_val",
                        5243, ""
                )
        );
        private final HashMap<Integer, List<String>> inputMapRepetitive = new HashMap<>(
                Map.of(8, new ArrayList<>(List.of("random_val", "random_val2")),
                        35, new ArrayList<>(),
                        52, new ArrayList<>(List.of("random_val", "random_val2")),
                        5243, new ArrayList<>(List.of("random_val"))
                )
        );

        @Setup
        public void setup() {
            for (int i = 0; i < MESSAGES.length; i++) {
                customBinaryMessagePool[i] = FixEncoder.encodeBinary(MESSAGES[i]);
                defaultBinaryMessagePool[i] = MESSAGES[i].getBytes(StandardCharsets.US_ASCII);
            }
        }
        public byte[] getBinaryMessage(Encoding encoding) {
            return switch (encoding) {
                case CUSTOM -> customBinaryMessagePool[random.nextInt(MESSAGES.length)];
                case ASCII -> defaultBinaryMessagePool[random.nextInt(MESSAGES.length)];
            };
        }
    }

    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void decodeOneMessageWithAllFieldsNonRepetitive(MyState state) {
        FixParser.parseBinaryNonRepetitive(state.getBinaryMessage(state.encoding), state.encoding);
    }

    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void decodeOneMessageWithAllFieldsRepetitive(MyState state) {
        FixParser.parseBinaryRepetitive(state.getBinaryMessage(state.encoding), state.encoding);
    }

    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void decodeOneMessageWithInterestFieldsNonRepetitive(MyState state) {
        FixParser.parseBinaryNonRepetitive(state.getBinaryMessage(state.encoding), state.encoding, 8,35,52,5243);
    }

    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void decodeOneMessageWithInterestFieldsRepetitive(MyState state) {
        FixParser.parseBinaryRepetitive(state.getBinaryMessage(state.encoding), state.encoding, 8,35,52,5243);
    }

    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void decodeOneMessageWithInterestFieldsNonRepetitiveWithReusableHashMap(MyState state) {
        FixParser.parseBinaryNonRepetitive(state.getBinaryMessage(state.encoding), state.encoding, state.inputMapNonRepetitive);
    }

    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void decodeOneMessageWithInterestFieldsRepetitiveWithReusableHashMap(MyState state) {
        FixParser.parseBinaryRepetitive(state.getBinaryMessage(state.encoding), state.encoding, state.inputMapRepetitive);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FixParserBenchmark.class.getSimpleName())
                .forks(1)
                .addProfiler(GCProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
