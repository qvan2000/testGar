import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;

public class Main {
    public static String allPunctuation = ".,?!;:()=-/\\\"\'\f\t\b\n\r—";

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String textFrom = bufferedReader.readLine();
        bufferedReader.close();
        System.out.println(generateTextOut2(textFrom,allPunctuation));
    }


public static String generateTextOut2(String textFrom, String allPunctuation) {
        String textOut2 = textFrom;
    String textOut3 = "";
    String curr = "";
    int countPunctuation = 0;
    int midLength = 7;
    for (int i = 0; i < textFrom.length(); i++) {
        curr = textFrom.substring(i,i+1);
        if (curr.equals(" ")||allPunctuation.contains(curr)) {
            countPunctuation = 0;
            textOut3 = textOut3+curr;
            continue;
        } else {
            if (countPunctuation>midLength) {
                countPunctuation = 0;
                textOut3 = textOut3+" ";
            }
            textOut3 = textOut3+curr;
            countPunctuation++;
        }
    }

        return textOut3;


}
}