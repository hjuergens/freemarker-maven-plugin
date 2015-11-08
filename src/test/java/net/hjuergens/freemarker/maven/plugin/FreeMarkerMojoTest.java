/*
 */
package net.hjuergens.freemarker.maven.plugin;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaDocSource;

import java.io.File;
import java.io.FileWriter;

/**
 * @author juergens
 */
public class FreeMarkerMojoTest
    extends AbstractMojoTestCase
{
    /**
     * @throws Exception if any
     */
    public void testProject01()
            throws Exception
    {
        File pom = getTestFile( "target/test-classes/project01/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        FreeMarkerMojo myMojo = (FreeMarkerMojo) lookupMojo( "process", pom );
        assertNotNull( myMojo );
        myMojo.execute();

        File srcFile = new File("target/generated-test-sources/project01/java/Database.java");
        final JavaClassSource javaClass =Roaster.parse(JavaClassSource.class, srcFile);
        assertEquals("Database", javaClass.getName());
        JavaDocSource javaDoc = javaClass.getJavaDoc();
        javaDoc.addTagValue("@author","FreeMarkerMojo");
        String formatted = Roaster.format( javaClass.toString() );
        FileWriter writer = new FileWriter(srcFile);
        writer.write(formatted);
        writer.close();
    }

    /**
     * @throws Exception if any
     */
    public void testProject02()
            throws Exception
    {
        File pom = getTestFile( "target/test-classes/project02/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        FreeMarkerMojo myMojo = (FreeMarkerMojo) lookupMojo( "process", pom );
        assertNotNull( myMojo );
        myMojo.execute();
    }
}