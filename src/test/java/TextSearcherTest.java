import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextSearcherTest {

    @Test
    void testCountOccurrences_simpleMatch() {
        // given:
        String text = "abcabc";
        String pattern = "abc";

        // when:
        int result = TextSearcher.countOccurrences(text, pattern);

        // then:
        assertEquals(2, result);
    }

    @Test
    void testCountOccurrences_noMatch() {
        // given:
        String text = "hello";
        String pattern = "xyz";

        // when:
        int result = TextSearcher.countOccurrences(text, pattern);

        // then:
        assertEquals(0, result);
    }

    @Test
    void testCountOccurrences_overlappingMatches() {
        // given:
        String text = "aaaa";
        String pattern = "aa";

        // when:
        int result = TextSearcher.countOccurrences(text, pattern);

        // then:
        assertEquals(3, result);  // "aa" встречается на позициях 0, 1 и 2
    }

    @Test
    void testCountOccurrences_emptyPattern() {
        // given:
        String text = "hello";
        String pattern = "";

        // when:
        int result = TextSearcher.countOccurrences(text, pattern);

        // then:
        assertEquals(0, result);
    }

    @Test
    void testCountOccurrences_nullText() {
        // given:
        String text = null;
        String pattern = "abc";

        // when:
        int result = TextSearcher.countOccurrences(text, pattern);

        // then:
        assertEquals(0, result);
    }
}
