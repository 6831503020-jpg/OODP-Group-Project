/**
 * CONCEPT: ENUM MODULARITY
 * Why: Public enums must be in their own file so the Java compiler 
 * can resolve the type globally. This prevents circular dependencies 
 * and makes the 'AnnouncementType' reusable across different modules.
 */
public enum AnnouncementType {
    EVENT, 
    STAFF_CALLING
}