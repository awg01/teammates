package teammates.client.scripts;

import java.util.Arrays;
import java.util.List;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.logic.api.Logic;
import teammates.storage.entity.Course;

/**
 * Deletes the courses that are added by legacy browser tests.
 *
 * <p>A course is determined to be an internal testing-only course if any of the following criteria apply:
 * (1) The institute belongs to a pre-defined list of test institutes (queried over the entire DB), or
 * (2) There is a non-zero number of instructors with email looking like *@*.tmt (internal test data format).
 */
public class DeleteTestCourses extends DataMigrationEntitiesBaseScript<Course> {

    private static final List<String> TEST_INSTITUTES = Arrays.asList(
            "TEAMMATES Test Institute 1",
            "TEAMMATES Test Institute 5",
            "TEAMMATES Test Institute 7",
            "TEAMMATES Test Institute 9",
            "TEAMMATES Test Institute with Long Long Long Name",
            "inst&quot;&lt;&#x2f;td&gt;&lt;&#x2f;div&gt;",
            "inst&lt;script&gt; alert(&#39;hi!&#39;); &lt;&#x2f;script&gt;",
            "Test institute",
            "Test University"
    );

    private static final List<String> TEST_COURSE_IDS = Arrays.asList(
            "AAMgtUiT.CS1101",
            "AAMgtUiT.CS2103",
            "AAMgtUiT.CS2104",
            "AHPUiT.instr1.gma-demo",
            "AHPUiT.instr1.gma-demo0",
            "AHPUiT.instr1.gma-demo1",
            "AHPUiT.instr1.gma-demo10",
            "AHPUiT.instr1.gma-demo11",
            "AHPUiT.instr1.gma-demo12",
            "AHPUiT.instr1.gma-demo13",
            "AHPUiT.instr1.gma-demo14",
            "AHPUiT.instr1.gma-demo15",
            "AHPUiT.instr1.gma-demo16",
            "AHPUiT.instr1.gma-demo17",
            "AHPUiT.instr1.gma-demo18",
            "AHPUiT.instr1.gma-demo19",
            "AHPUiT.instr1.gma-demo2",
            "AHPUiT.instr1.gma-demo20",
            "AHPUiT.instr1.gma-demo21",
            "AHPUiT.instr1.gma-demo22",
            "AHPUiT.instr1.gma-demo23",
            "AHPUiT.instr1.gma-demo3",
            "AHPUiT.instr1.gma-demo4",
            "AHPUiT.instr1.gma-demo5",
            "AHPUiT.instr1.gma-demo6",
            "AHPUiT.instr1.gma-demo7",
            "AHPUiT.instr1.gma-demo8",
            "AHPUiT.instr1.gma-demo9",
            "AHPUiT.instr1_.gma-demo",
            "AHPUiT____.instr1_.gma-demo0",
            "AHPUiT____.instr1_.gma-demo1",
            "AHPUiT____.instr1_.gma-demo2",
            "AST.TGCBCI.course1",
            "AST.TGCBCI.course2",
            "AutEvalRem.course",
            "AutSessRem.course",
            "CCAddUiTest.course1",
            "CCAddUiTest.CS2105",
            "CCEnrollUiT.CS2104",
            "CCSDEditUiT.CS2104",
            "CCSDetailsUiT.CS2104",
            "CCSDetailsUiT.sanitizationCourse",
            "CESubEditUiT.CS1101",
            "CESubViewUiT.CS1101",
            "CEvalRUiT.CS1101",
            "CEvalUiT.coursewithoutevals",
            "CEvalUiT.CS1101",
            "CEvalUiT.CS2104",
            "CEvEditUiTest.CS1101",
            "CFeedbackEditUiT.CS1101",
            "CFeedbackEditUiT.CS2104",
            "CFeedbackEditUiT.idOfDstCourse",
            "CFeedbackEditUiT.idOfTSCourse",
            "CFeedbackUiT.coursewithoutsessions",
            "CFeedbackUiT.CS1101",
            "CFeedbackUiT.CS2104",
            "CFeedbackUiT.CS2105",
            "CFeedbackUiT.CS2106",
            "CFResultsUiT.CS2104",
            "CFResultsUiT.NoSections",
            "CFResultsUiT.SanitizedTeam",
            "charlie.tmms.gma-demo0",
            "CHomeUiT.idOfTestingSanitizationCourse",
            "comments.idOfArchivedCourse",
            "comments.idOfSampleCourse-demo",
            "comments.idOfTypicalCourse1",
            "comments.idOfUriCharsCourse",
            "course1",
            "course2",
            "course3",
            "course4",
            "CS1101",
            "CS2104",
            "CS4215",
            "CSListUiT.idOfTestingSanitizationCourse",
            "dummy.gma-demo",
            "dummy2.gma-demo",
            "est.sample.data-demo",
            "FConstSumOptionQnUiT.CS2104",
            "FConstSumRecipientQnUiT.CS2104",
            "FContribQnUiT.CS2104",
            "FeedbackEditCopy.CS1101",
            "FeedbackEditCopy.CS2102",
            "FeedbackEditCopy.CS2103",
            "FeedbackEditCopy.CS2103R",
            "FeedbackEditCopy.CS2104",
            "FeedbackEditCopy.CS2105",
            "FeedbackEditCopy.CS2107",
            "FMcqQnUiT.CS2104",
            "FMsqQnUiT.CS2104",
            "FNumScaleQnUiT.CS2104",
            "FRankUiT.CS4221",
            "FRubricQnUiT.CS2104",
            "FSQTT.idOfCourseWithSections",
            "FSQTT.idOfTypicalCourse1",
            "FSQTT.idOfTypicalCourse2",
            "ICJConfirmationUiT.CS1101",
            "idOfArchivedCourse",
            "idOfCourse1OfCoord2",
            "idOfCourse1OfInstructor2",
            "idOfCourse2OfCoord2",
            "idOfCourse2OfInstructor2",
            "idOfCourseNoEvals",
            "idOfSampleCourse-demo",
            "idOfTestingSanitizationCourse",
            "idOfTypicalCourse1",
            "idOfTypicalCourse2",
            "idOfTypicalCourse3",
            "idOfTypicalCourse4",
            "idOfUnregisteredCourse",
            "IESFPTCourse",
            "IFQSubmitUiT.CS2104",
            "IFRResponseCommentUiT.CS2104",
            "IFRResponseCommentUiT.NoSections",
            "IFRResponseCommentUiT.SanitizedTeam",
            "IFSubmitUiT.CS2104",
            "ins.wit-demo1",
            "ins.wit-demo2",
            "ins.wit-demo3",
            "ins.wit-Unloaded",
            "InsCrsEdit.CS2104",
            "InsCrsEdit.idOfTestingSanitizationCourse",
            "ISR.CS1101",
            "ISR.CS2104",
            "ISR.NoEval",
            "ISR.sanitizationCourse",
            "jd1.exa-demo",
            "my-at-gmail.com-demo",
            "newIns.wit-demo",
            "random-demo",
            "SCDetailsUiT.CS2104",
            "SCDetailsUiT.idOfTSCourse",
            "searchUI.idOfTestingSanitizationCourse",
            "searchUI.idOfTypicalCourse1",
            "session-scalability-test2",
            "SEvalEditUiT.CS2104",
            "SEvalRUiT.CS1101",
            "SFQSubmitUiT.CS2104",
            "SFResultsUiT.CS2104",
            "SFSubmitUiT.CS2104",
            "SHomeUiT.sanitizationCourse",
            "test.tes-demo",

            // these are also orphaned courses

            "AHPUiT.instr1-demo",
            "anothersamplecourse",
            "AST.TDC.course1",
            "AST.TDC.course2",
            "AST.TGCBCI.course3",
            "charlie.tmms.gma-demo",
            "course-with-space",
            "demo.instructor-demo",
            "ICJConfirmationUiT.CS2104",
            "idOfCourse1OfInstructor1",
            "idOfCourse2OfInstructor1",
            "OmitInstructor",
            "SessionInGracePeriod"
    );

    private final Logic logic = Logic.inst();

    public static void main(String[] args) {
        new DeleteTestCourses().doOperationRemotely();
    }

    private boolean isTestCourse(Course course) {
        if (TEST_INSTITUTES.contains(course.getInstitute())) {
            return true;
        }
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(course.getUniqueId());
        return instructors.stream()
                .anyMatch(instr -> instr.getEmail().endsWith(".tmt"));
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
        if (TEST_COURSE_IDS.contains(course.getUniqueId())) {
            if (isPreview() && !isTestCourse(course)) {
                // There are other test courses determined via other manners.
                // This is an extra verification step done during preview mode.
                System.out.printf("Verify that this is test course: [%s] %s (%s)%n", course.getUniqueId(),
                        course.getName(), course.getInstitute());
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
