public class Main_UnitTests {
    public static final String TEXT = "aaababaabaaaabaabaabaabaaababaabaaababaabaaaabaabaabaabbabaabaaababaababaabaabaabaaabbaab";
    public static final String PATTERN = "aab";

    public static int countOccurrences(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < pattern.length(); j++) {
                if (i + j >= text.length() || text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                } else if (j == pattern.length() - 1) {
                    count++;
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int count = countOccurrences(TEXT, PATTERN);
        System.out.println("Строка " + PATTERN + " встретилась в тексте " + count + " раз");
    }
}
