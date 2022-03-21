package teammates.client.scripts;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.logic.api.Logic;
import teammates.storage.entity.Instructor;

/**
 * Populates instructor's createdAt timestamp.
 *
 * <p>This is necessary as the instructor's createdAt timestamp was introduced in late 2021, thus all instructors
 * created before that will not have createdAt timestamp.
 *
 * <p>As there is no way to accurately find out past instructors' (added before the introduction of createdAt field)
 * creation time, we make use of the corresponding course's creation time, which should be a good enough alternative.
 */
public class PopulateInstructorCreatedAt extends DataMigrationEntitiesBaseScript<Instructor> {

    private final Logic logic = Logic.inst();
    private final Map<String, CourseAttributes> courseCache = new HashMap<>();

    public static void main(String[] args) {
        new PopulateInstructorCreatedAt().doOperationRemotely();
    }

    @Override
    protected Query<Instructor> getFilterQuery() {
        return ofy().load().type(Instructor.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(Instructor instructor) {
        try {
            Field createdAtField = instructor.getClass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            return createdAtField.get(instructor) == null;
        } catch (ReflectiveOperationException e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(Instructor instructor) {
        String courseId = instructor.getCourseId();
        CourseAttributes course = courseCache.computeIfAbsent(courseId, k -> logic.getCourse(instructor.getCourseId()));
        if (course == null) {
            logic.deleteInstructorCascade(instructor.getCourseId(), instructor.getEmail());
            return;
        }
        if (instructor.getCreatedAt() != null
                && instructor.getCreatedAt().toEpochMilli() > course.getCreatedAt().toEpochMilli()) {
            return;
        }
        instructor.setCreatedAt(course.getCreatedAt());
        saveEntityDeferred(instructor);
    }

}
