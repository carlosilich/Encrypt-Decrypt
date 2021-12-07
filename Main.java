package encryptdecrypt;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String mode = "enc";
        int key = 0;
        String data = "";
        String in = "";
        String out = "";
        String alg = "shift";

        for (int i = 0; i < args.length; i += 2) {
            String field = args[i];
            switch (field) {
                case "-mode":
                     mode = args[i + 1];
                    break;
                case "-key":
                    key = Integer.parseInt(args[i + 1]);
                    break;
                case "-data":
                    data = args[i + 1];
                    break;
                case "-in":
                    in = args[i + 1];
                    break;
                case "-out":
                    out = args[i + 1];
                    break;
                case "-alg":
                    alg = args[i + 1];
                    break;
            }
        }

        AlgorithmEr algorithmEr = new AlgorithmEr();

        if (alg.equals("shift")) {
            algorithmEr.setAlgorithm(new Shifting());
        } else {
            algorithmEr.setAlgorithm(new Unicode());
        }

        if (mode.equals("enc")) {
            algorithmEr.encode(key, data, in, out);
        } else {
            algorithmEr.decode(key, data, in, out);
        }
    }
}

class AlgorithmEr {
    private Algorithm algorithm;

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void encode(int key, String data, String in, String out) {
        this.algorithm.encode(key, data, in, out);
    }

    public void decode(int key, String data, String in, String out) {
        this.algorithm.decode(key, data, in, out);
    }
}

abstract class Algorithm {

    public abstract void encode (int key, String data, String in, String out);
    public abstract void decode (int key, String data, String in, String out);

    public static String readingFromFile (String fileName) {
        try {
            return new String (Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "";
    }

    public static void writeToFile (String fileName, String text) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(text);
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}

class Shifting extends Algorithm {

    @Override
    public void encode (int key, String data, String in, String out) {
        String textToEncode = "".equals(in) ? data : readingFromFile(in);
        if ("".equals(out)) {
            System.out.println(encodeShiftedText(textToEncode, key));
        } else {
            writeToFile(out, encodeShiftedText(textToEncode, key));
        }
    }

    @Override
    public void decode (int key, String data, String in, String out) {
        String textToDecode = "".equals(in) ? data : readingFromFile(in);
        if ("".equals(out)) {
            System.out.println(decodeShiftedText(textToDecode, key));
        } else {
            writeToFile(out, decodeShiftedText(textToDecode, key));
        }
    }

    private String encodeShiftedText (String text, int key) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                chars[i] = (char)(((chars[i] - 'A' + key) % 26) + 'A');
            } else if (Character.isLowerCase(chars[i])) {
                chars[i] = (char)(((chars[i] - 'a' + key) % 26) + 'a');
            }
        }
        return String.valueOf(chars);
    }

    private String decodeShiftedText (String text, int key) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                chars[i] = (char)(((chars[i] - 'A' - key + 26) % 26) + 'A');
            } else if (Character.isLowerCase(chars[i])) {
                chars[i] = (char)(((chars[i] - 'a' - key + 26) % 26) + 'a');
            }
        }
        return String.valueOf(chars);
    }
}

class Unicode extends Algorithm {

    @Override
    public void encode (int key, String data, String in, String out) {
        String textToEncode = "".equals(in) ? data : readingFromFile(in);
        if ("".equals(out)) {
            System.out.println(encodeUnicodeText(textToEncode, key));
        } else {
            writeToFile(out, encodeUnicodeText(textToEncode, key));
        }
    }

    @Override
    public void decode (int key, String data, String in, String out) {
        String textToDecode = "".equals(in) ? data : readingFromFile(in);
        if ("".equals(out)) {
            System.out.println(decodeUnicodeText(textToDecode, key));
        } else {
            writeToFile(out, decodeUnicodeText(textToDecode, key));
        }
    }

    private String encodeUnicodeText(String text, int key) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] += key;
        }
        return String.valueOf(chars);
    }

    private String decodeUnicodeText(String text, int key) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] -= key;
        }
        return String.valueOf(chars);
    }
}