package teammates.client.scripts;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.logic.api.Logic;
import teammates.storage.entity.CourseStudent;

/**
 * Populates student's createdAt timestamp.
 *
 * <p>This is necessary as the CourseStudent entity (a new version of Student entity) was created in early 2016,
 * and all migrated Student entities before then are created with the default timestamp, which is inaccurate.
 *
 * <p>As there is no way to accurately find out past students' creation time,
 * we make use of the corresponding course's creation time, which should be a good enough alternative.
 */
public class PopulateStudentCreatedAt extends DataMigrationEntitiesBaseScript<CourseStudent> {

    private static final Instant PLACEHOLDER_CREATE_TIME = Instant.parse("2011-01-01T00:00:00Z");

    private final Logic logic = Logic.inst();
    private final Map<String, CourseAttributes> courseCache = new HashMap<>();

    public static void main(String[] args) {
        new PopulateStudentCreatedAt().doOperationRemotely();
    }

    @Override
    protected Query<CourseStudent> getFilterQuery() {
        return ofy().load().type(CourseStudent.class)
                .filter("createdAt =", PLACEHOLDER_CREATE_TIME);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(CourseStudent student) {
        // Accuracy is already guaranteed by the DB query
        return true;
    }

    @Override
    protected void migrateEntity(CourseStudent student) {
        String courseId = student.getCourseId();
        CourseAttributes course = courseCache.computeIfAbsent(courseId, k -> logic.getCourse(student.getCourseId()));
        if (course == null) {
            logic.deleteStudentCascade(student.getCourseId(), student.getEmail());
            return;
        }
        if (student.getCreatedAt().toEpochMilli() > course.getCreatedAt().toEpochMilli()) {
            return;
        }
        student.setCreatedAt(course.getCreatedAt());
        saveEntityDeferred(student);
    }

}
