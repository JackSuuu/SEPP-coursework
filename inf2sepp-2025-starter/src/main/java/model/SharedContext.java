package model;

import java.util.*;

public class SharedContext {
    public static final String ADMIN_STAFF_EMAIL = "inquiries@hindeburg.ac.nz";
    public User currentUser;

    public final List<Inquiry> inquiries;
    private final CourseManager course_manager;
    private final FAQManager faq_manager;
    
    private final Map<String, Set<String>> faqTopicsUpdateSubscribers;

    public SharedContext() {
        this.currentUser = new Guest();
        this.inquiries = new ArrayList<>();
        course_manager = new CourseManager();
        faq_manager = new FAQManager();
        faqTopicsUpdateSubscribers = new HashMap<>();
    }

    public CourseManager getCourseManager() {
        return course_manager;
    }

    public FAQManager getFaqManager() {
        return faq_manager;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean registerForFAQUpdates(String email, String topic) {
        if (faqTopicsUpdateSubscribers.containsKey(topic)) {
            return faqTopicsUpdateSubscribers.get(topic).add(email);
        } else {
            Set<String> subscribers = new HashSet<>();
            subscribers.add(email);
            faqTopicsUpdateSubscribers.put(topic, subscribers);
            return true;
        }
    }

    public boolean unregisterForFAQUpdates(String email, String topic) {
        return faqTopicsUpdateSubscribers.getOrDefault(topic, new HashSet<>()).remove(email);
    }

    public Set<String> usersSubscribedToFAQTopic(String topic) {
        return faqTopicsUpdateSubscribers.getOrDefault(topic, new HashSet<>());
    }
}
