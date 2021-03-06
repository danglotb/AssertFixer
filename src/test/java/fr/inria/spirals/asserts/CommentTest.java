package fr.inria.spirals.asserts;

import fr.inria.spirals.test.TestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.code.CtComment;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 30/05/17
 */
public class CommentTest {

    private Launcher spoon;

    @Before
    public void setUp() throws Exception {
        spoon = new Launcher();
        spoon.addInputResource("src/test/resources/ClassResourcesTest.java");
        spoon.addInputResource("src/main/java/fr/inria/spirals/asserts/log/Logger.java"); // adding the logger to the spoon model to compile and run it to fix assertions
        spoon.getEnvironment().setComplianceLevel(7);
        spoon.getEnvironment().setAutoImports(true);
        spoon.getEnvironment().setShouldCompile(true);
        spoon.run();
    }

    @Test
    public void testAddCommentWithOldAssertion() throws Exception {
        final String testCaseName = "testAssertionErrorBoolean";
        final String fullQualifiedName = "aPackage.ClassResourcesTest";

        assertTrue(spoon.getFactory().Class()
                .get(fullQualifiedName)
                .getMethodsByName(testCaseName)
                .get(0)
                .getBody()
                .getStatement(0)
                .getComments()
                .isEmpty()
        );

        List<Failure> failures = TestRunner.runTest(
                fullQualifiedName,
                testCaseName,
                new String[]{"spooned-classes/"});// 1st assert fail

        AssertFixer.fixAssert(spoon,
                fullQualifiedName,
                testCaseName,
                failures.get(0),
                "spooned-classes/");

        final List<CtComment> comments = spoon.getFactory().Class()
                .get(fullQualifiedName)
                .getMethodsByName(testCaseName)
                .get(0)
                .getBody()
                .getStatement(0)
                .getComments();

        assertFalse(comments.isEmpty());
        final String commentGenerated = "// AssertFixer: old assertion Assert.assertTrue(ClassResourcesTest.getFalse())";
        assertEquals(commentGenerated, comments.get(0).toString());
    }

}
