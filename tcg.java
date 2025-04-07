import java.util.Scanner;

public class TeamCodeGenerator {

    public static void main(String[] args) {
        LongToCodeConverter teamConverter = new LongToCodeConverter(
            LongToCodeConverter.TEAM_TAG,
            LongToCodeConverter.TEAM_CONVERSION_CHARS
        );

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter team code (starts with " + LongToCodeConverter.TEAM_TAG + "): ");
        String inputCode = scanner.nextLine();
        scanner.close();

        generateNextCodes(teamConverter, inputCode, 10);
    }

    public static void generateNextCodes(LongToCodeConverter converter, String baseCode, int count) {
        long baseId = converter.toId(baseCode);
        
        if (baseId == -1) {
            System.out.println("Error: Invalid command code");
            return;
        }

        System.out.println("Original ID: " + baseId);
        System.out.println("Next " + count + " team codes:");

        for (int i = 1; i <= count; i++) {
            long nextId = baseId + i;
            
            int highInt = (int)(nextId >>> 32);
            int lowInt = (int)(nextId & 0xFFFFFFFFL);
            
            String nextCode = converter.toCode(highInt, lowInt);
            System.out.println(i + ": " + nextCode + " (ID: " + nextId + ")");
        }
    }
}




public class LongToCodeConverter {

    public static final String CONVERSION_CHARS = "0289PYLQGRJCUV";
    public static final String HASH_TAG = "#";
    public static final String TEAM_CONVERSION_CHARS = "QWERTYUPASDFGHJKLZCVBNM23456789";
    public static final String TEAM_TAG = "X";

    private String codeSuffix;
    private String conversionChars;

    public LongToCodeConverter() {
        this.conversionChars = CONVERSION_CHARS;
        this.codeSuffix = HASH_TAG;
    }
    public LongToCodeConverter(String codeSuffix, String conversionChars) {
        this.codeSuffix = codeSuffix;
        this.conversionChars = conversionChars;
    }
    public String toCode(int highInt, int lowInt) {
        if (highInt < 256) {
            long l = toLong(lowInt >> 24, highInt | (lowInt << 8));
            String res = convert(l);
            return codeSuffix + res;
        } else {
            System.err.println("Cannot convert the code to string. Higher int value too large");
            return null;
        }
    }

    private String convert(long id) {
        StringBuilder tag = new StringBuilder();
        int len = conversionChars.length();
        while (id > 0) {
            int charIndex = (int)Math.floor(id % len);
            tag.insert(0, conversionChars.charAt(charIndex));
            id -= charIndex;
            id /= len;
        }
        return tag.toString();
    }

    public long toId(String code) {
        String codeSubstring, subStr;
        int i, counter, subStrIdx;
        int unk6, unk12;
        long unk7, v13; 
        int hiInt, loInt;

        if (code.length() < 14) {
            codeSubstring = code.substring(1);

            if (codeSubstring.length() < 1) {
                v13 = 0;
                loInt = (int) (v13 & 0x7FFFFFFF);
                hiInt = 0;

            } else {
                i = 0;
                unk6 = 0;
                unk7 = 0;

                do {
                    counter = i + 1;
                    subStr = codeSubstring.substring(i, counter);
                    subStrIdx = conversionChars.indexOf(subStr);
                    if (subStrIdx <= -1) {
                        System.err.println("Cannot convert the string to code. String contains invalid character(s).");
                        return toLong(-1, -1);
                    }

                    unk12 = unk6 * conversionChars.length() + subStrIdx;
                    unk7 = (toLong((int) unk7, unk6) * conversionChars.length() + subStrIdx) >> 32;
                    unk6 = unk12;
                    i = counter;
                } while (counter < codeSubstring.length());

                if ((unk12 & unk7) != -1) {
                    v13 = toLongS((int) unk7, unk12) >> 8;
                    loInt = (int) (v13 & 0x7FFFFFFF);
                    hiInt = unk6 & 0xFF;
                    return toLong(hiInt, loInt);
                }

                hiInt = -1;
                loInt = -1;
            }

            return toLong(hiInt, loInt);
        } else {
            System.err.println("Cannot convert the string to code. String is too long.");
            return toLong(-1, -1);
        }
    }

    private long toLong(int hiInt, int loInt) {
        return ((long)hiInt << 32) | (loInt & 0xFFFFFFFFL);
    }
    
    private long toLongS(int hiInt, int loInt) {
        return ((long)hiInt << 32) | ((long)loInt);
    }
}
