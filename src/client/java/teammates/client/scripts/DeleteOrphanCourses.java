package teammates.client.scripts;

import java.util.Arrays;
import java.util.List;

import com.googlecode.objectify.cmd.Query;

import teammates.logic.api.Logic;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;

/**
 * Deletes orphan courses, i.e. courses with no instructors in it.
 */
public class DeleteOrphanCourses extends DataMigrationEntitiesBaseScript<Course> {

    // As the number of orphan courses is small, we opt for the approach of hard-coding the names instead
    private static final List<String> ORPHAN_COURSES = Arrays.asList(
            // List to be added manually
            ""
    );

    private final Logic logic = Logic.inst();

    public static void main(String[] args) {
        new DeleteOrphanCourses().doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        return ofy().load().type(Course.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    private boolean isOrphanCourse(Course course) {
        int allInstructorCount = ofy().load().type(Instructor.class)
                .filter("courseId =", course.getUniqueId())
                .count();
        if (allInstructorCount == 0) {
            return true;
        }

        int nullGoogleIdInstructorCount = ofy().load().type(Instructor.class)
                .filter("courseId =", course.getUniqueId())
                .filter("googleId =", null)
                .count();
        return allInstructorCount == nullGoogleIdInstructorCount;
    }

    @Override
    protected boolean isMigrationNeeded(Course course) {
        if (ORPHAN_COURSES.contains(course.getUniqueId())) {
            if (isPreview() && !isOrphanCourse(course)) {
                System.out.println("WARNING: Not orphan course: " + course.getUniqueId());
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void migrateEntity(Course course) {
        logic.deleteCourseCascade(course.getUniqueId());
    }

}
