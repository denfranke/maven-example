
import com.fasterxml.jackson.annotation.JsonProperty;

public class CatFact {
    private String id;
    private String text;
    private String type;
    private String user;
    private Integer upvotes; // Используем Integer, чтобы можно было хранить null

    // Конструкторы
    public CatFact() {
    }

    public CatFact(String id, String text, String type, String user, Integer upvotes) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.user = user;
        this.upvotes = upvotes;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    @Override
    public String toString() {
        return String.format("CatFact{id='%s', upvotes=%d, text='%s'}",
                id, upvotes, text.length() > 50 ? text.substring(0, 50) + "..." : text);
    }
}