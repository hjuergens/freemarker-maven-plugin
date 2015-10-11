/*
 */
package net.hjuergens.freemarker.maven.plugin;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 *     <directory>${project.basedir}/target</directory>
    <outputDirectory>${project.build.directory}/classes</outputDirectory>
    <finalName>${project.artifactId}-${project.version}</finalName>
    <testOutputDirectory>${project.build.directory}/test-classes</testOutputDirectory>
    <sourceDirectory>${project.basedir}/src/main/java</sourceDirectory>
    <scriptSourceDirectory>src/main/scripts</scriptSourceDirectory>
    <testSourceDirectory>${project.basedir}/src/test/java</testSourceDirectory>
    <resources>
      <resource>
        <directory>${project.basedir}/src/main/resources</directory>
      </resource>
    </resources>
    <testResources>
      <testResource>
        <directory>${project.basedir}/src/test/resources</directory>
      </testResource>
    </testResources>
    * 
 * @author juergens
 */
public class FreeMarkerMojoTest
    extends AbstractMojoTestCase
{
    /** {@inheritDoc} */
    @Override
    protected void setUp()
        throws Exception
    {
        // required
        super.setUp();
    }

    /** {@inheritDoc} */
    @Override
    protected void tearDown()
        throws Exception
    {
        // required
        super.tearDown();
    }

    /**
     * @throws Exception if any
     */
    public void testSomething2()
        throws Exception
    {
        File pom = getTestFile( "target/test-classes/project/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        FreeMarkerMojo myMojo = (FreeMarkerMojo) lookupMojo( "process", pom );
        assertNotNull( myMojo );
        myMojo.execute();
    }
}