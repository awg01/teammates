package teammates.client.scripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.api.AccountRequestsDb;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;

/**
 * This script serves two concurrent purposes, namely
 * (1) to populate the recently introduced {@link AccountRequest} entities based on the information of sample courses
 * from the entire DB, and
 * (2) to delete sample courses belonging to account requests that are never used.
 */
public class PopulateAccountRequestEntities extends DataMigrationEntitiesBaseScript<Course> {

    // The current demo course naming strategy is introduced in late 2013. Prior to that, a different
    // demo course naming strategy is used, which unfortunately is hard to trace.
    // As the number of such courses is not large, we opt to simply hardcode the demo course
    // to the course requester's email.
    private static final Map<String, String> HARDCODED_EMAILS = new HashMap<>();

    static {
        // Add the hard-coded course-email mapping here
        HARDCODED_EMAILS.put("", "");
    }

    private AccountRequest currentAccountRequest;

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();
    private final Logic logic = Logic.inst();

    public static void main(String[] args) {
        new PopulateAccountRequestEntities().doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        return ofy().load().type(Course.class).filter("name =", "Sample Course 101");
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(Course course) {
        // Criteria for sample course belonging to an unused account request:
        // 1. Course name is "Sample Course 101" (already guaranteed by the DB query)
        // 2. Course ID should end with -demo, -demo0, -demo1, ... , or -demo7
        //    (max. duplicate samples existing in DB)
        // 3. There is only one instructor for the course, i.e. the sole demo instructor
        // 4. The instructor has null google ID, i.e. unregistered

        String courseId = course.getUniqueId();
        if (!courseId.matches(".*-demo[0-7]?$")) {
            return false;
        }

        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        if (instructors.isEmpty()) {
            return false;
        }

        InstructorAttributes instr;
        boolean usedCourse;
        if (instructors.size() > 1) {
            instr = instructors.stream()
                    .filter(i -> i.getEmail().startsWith(courseId.replaceFirst("\\.([a-z._-]{3})-demo[0-7]?$", "@$1"))
                            || HARDCODED_EMAILS.getOrDefault(courseId, "").equals(i.getEmail()))
                    .findFirst()
                    .orElse(null);
            usedCourse = true;
        } else {
            instr = instructors.get(0);
            usedCourse = instr.getGoogleId() != null;
        }

        currentAccountRequest = new AccountRequest(instr.getEmail(), instr.getName(), course.getInstitute());
        currentAccountRequest.setCreatedAt(course.getCreatedAt());
        if (usedCourse) {
            currentAccountRequest.setRegisteredAt(course.getCreatedAt());
        }

        return true;
    }

    @Override
    protected void migrateEntity(Course course) throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequestAttributes existing = accountRequestsDb.getAccountRequest(
                currentAccountRequest.getEmail(), currentAccountRequest.getInstitute());
        if (existing == null) {
            // Use ofy().save() directly as the createdAt timestamp needs to be pre-determined
            ofy().save().entity(currentAccountRequest).now();
        } else if (existing.getRegisteredAt() == null && currentAccountRequest.getRegisteredAt() != null) {
            logic.updateAccountRequest(AccountRequestAttributes
                    .updateOptionsBuilder(existing.getEmail(), existing.getInstitute())
                    .withRegisteredAt(currentAccountRequest.getRegisteredAt())
                    .build());
        }

        if (currentAccountRequest.getRegisteredAt() == null) {
            // Indicates users who never registered; delete the sample course
            logic.deleteCourseCascade(course.getUniqueId());
        }
    }

}
