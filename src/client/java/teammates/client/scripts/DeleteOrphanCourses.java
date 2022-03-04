package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.logic.api.Logic;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;

/**
 * Deletes orphan courses, i.e. courses with no instructors in it.
 */
public class DeleteOrphanCourses extends DataMigrationEntitiesBaseScript<Course> {

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

    @Override
    protected boolean isMigrationNeeded(Course course) {
        return ofy().load().type(Instructor.class)
                .filter("courseId =", course.getUniqueId())
                .keys()
                .list()
                .isEmpty();
    }

    @Override
    protected void migrateEntity(Course course) {
        logic.deleteCourseCascade(course.getUniqueId());
    }

}
