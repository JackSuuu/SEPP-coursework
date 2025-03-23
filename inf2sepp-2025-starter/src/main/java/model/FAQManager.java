package model;

import java.util.LinkedList;
import java.util.List;

public class FAQManager {
    // new private variable
    private int nextItemId;

    // store root-level sections (topics)
    private final List<FAQSection> rootSections = new LinkedList<>();

    // Returns the list of root (top-level) FAQ sections.
    public List<FAQSection> getRootSections() {
        return rootSections;
    }

    // Finds and returns a FAQSection by topic (searches recursively in all sections).
    public FAQSection getSectionByTopic(String topic) {
        return findSectionByTopic(rootSections, topic);
    }

    // Recursively search for a section with the given topic.
    private FAQSection findSectionByTopic(List<FAQSection> sections, String topic) {
        for (FAQSection section : sections) {
            if (section.getTopic().equals(topic)) {
                return section;
            }
            FAQSection found = findSectionByTopic(section.getSubsections(), topic);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    // Private method: Removes an FAQItem from a section identified by its index.
    private boolean removeItem(FAQSection section, int itemNumber) {
        List<FAQItem> items = section.getItems();
        if (itemNumber >= 0 && itemNumber < items.size()) {
            items.remove(itemNumber);
            return true;
        }
        return false;
    }

    // Returns a list of FAQItems in the section that have the given courseCode as tag.
    public List<FAQItem> getItemByTag(FAQSection section, String courseCode) {
        List<FAQItem> results = new LinkedList<>();
        for (FAQItem item : section.getItems()) {
            if (courseCode.equals(item.getCourseTag())) {
                results.add(item);
            }
        }
        return results;
    }

    // Adds a course tag to an FAQItem in section at the given index.
    public boolean addTagToItem(FAQSection section, int itemNumber, String courseCode) {
        List<FAQItem> items = section.getItems();
        if (itemNumber >= 0 && itemNumber < items.size()) {
            FAQItem item = items.get(itemNumber);
            // If the tag is already set to the given courseCode, do nothing.
            if (courseCode.equals(item.getCourseTag())) {
                return false;
            }
            item.setCourseTag(courseCode);
            return true;
        }
        return false;
    }

    // Removes the course tag from an FAQItem if it matches the given courseCode.
    public boolean removeTagFromItem(FAQSection section, int itemNumber, String courseCode) {
        List<FAQItem> items = section.getItems();
        if (itemNumber >= 0 && itemNumber < items.size()) {
            FAQItem item = items.get(itemNumber);
            if (courseCode.equals(item.getCourseTag())) {
                item.setCourseTag(null);
                return true;
            }
        }
        return false;
    }

    // Creates a new FAQSection with the given topic, adds it as a root section, and returns it.
    public FAQSection addTopic(String topic) {
        FAQSection newSection = new FAQSection(topic);
        rootSections.add(newSection);
        return newSection;
    }

    // Adds a subtopic (child section) under the given parent section.
    public FAQSection addSubtopic(FAQSection parentSection, String subtopic) {
        FAQSection newSubsection = new FAQSection(subtopic);
        parentSection.addSubsection(newSubsection);
        return newSubsection;
    }

    // Checks if a subtopic with the given name exists directly under the parentSection.
    public boolean topicExistsAtLevel(FAQSection parentSection, String topicName) {
        for (FAQSection subsection : parentSection.getSubsections()) {
            if (subsection.getTopic().equals(topicName)) {
                return true;
            }
        }
        return false;
    }

    // Private: Promotes the given section's subsections by attaching them directly to its parent.
    private void promoteSubsections(FAQSection section) {
        FAQSection parent = section.getParent();
        if (parent != null) {
            List<FAQSection> subsections = section.getSubsections();
            for (FAQSection sub : subsections) {
                sub.setParent(parent);
                parent.getSubsections().add(sub);
            }
            subsections.clear();
        }
    }

    // Creates an FAQItem (with an optional course tag), adds it to the section, and returns its index.
    public int addFAQItem(FAQSection section, String question, String answer, String courseTag) {
        FAQItem newItem = new FAQItem(question, answer, courseTag);
        section.getItems().add(newItem);
        return section.getItems().size() - 1;
    }
}