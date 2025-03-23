package model;

public class FAQItem {
    private final String question;
    private final String answer;
    private String courseTag;

    public FAQItem(String question, String answer, String courseTag) {
        this.question = question;
        this.answer = answer;
        this.courseTag = courseTag;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
    
    public String getCourseTag() {
        return courseTag;
    }

    public void setCourseTag(String courseTag) {
        this.courseTag = courseTag;
    }
}
