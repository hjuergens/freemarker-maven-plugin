package net.hjuergens.freemarker.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import freemarker.core.ParseException;
import freemarker.template.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import static java.nio.file.Files.newBufferedWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hjuergens.freemarker.models.WorkbookWrapper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Goal which uses FreeMarker
 *
 */
@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FreeMarkerMojo
        extends AbstractMojo {

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)    
    private File outputDirectory;

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources/ftl/database.ftl", property = "templateFile", required = true)
    private File templateFile;// = "src/main/resources/ftl/database.ftl";

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-resources/mdo", property = "resultDirectory", required = true)
    private File resultDirectory;// = "target/generated-resources/mdo/exceldata.mdo";

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources/Indexes.xlsx", property = "modelFile", required = true)
    private File modelFile;// = "target/generated-resources/mdo/exceldata.mdo";

    public void execute()
            throws MojoExecutionException {
        if (!templateFile.exists()) {
            throw new MojoExecutionException("Template path " + templateFile.toString() + " does not exists.");
        }
        if (!templateFile.isFile() || !templateFile.canRead()) {
            throw new MojoExecutionException("Template path has to be a readable file.");
        }

        final File templateDirectory = templateFile.getParentFile();
        assert templateDirectory.equals(templateFile.toPath().getParent().toFile());
        final String templateFileName = templateFile.getName();
        assert templateFileName.equals(templateFile.toPath().getFileName().toString());
        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        {
            try {
                cfg.setDirectoryForTemplateLoading(templateDirectory);  
            } catch (IOException ex) {
                throw new MojoExecutionException("An IO exception on template directory raised.", ex);
            }
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setObjectWrapper(new WorkbookWrapper(cfg.getIncompatibleImprovements()));
        }

        /*  next, get the Template  */
        Template temp;
        try {
            temp = cfg.getTemplate(templateFileName);
        } catch (MalformedTemplateNameException ex) {
            getLog().error(ex.getMalformednessDescription());
            throw new MojoExecutionException("MalformedTemplateNameException", ex);
        } catch (ParseException ex) {
            getLog().error(ex.getEditorMessage());
            throw new MojoExecutionException("ParseException", ex);
        } catch (IOException ex) {
            getLog().error(ex.getMessage());
            throw new MojoExecutionException("An IO exception on template file raised.", ex);
        }

        //Object dataModel = null;
        //try {
            if (modelFile == null) {
                throw new MojoExecutionException("Model file has to be specified.");
            }
            if (!modelFile.exists()) {
                throw new MojoExecutionException("Model file " + modelFile.toString() + " does not exists.");
            }
            if (!modelFile.isFile() || !modelFile.canRead()) {
                throw new MojoExecutionException("Model path has to be a readable file.");
            }
            if (!(modelFile.toString().toLowerCase().endsWith(".xlsx") || modelFile.toString().toLowerCase().endsWith(".xlsm"))) {
                throw new MojoExecutionException("File name has to end with '.xlsx'..");
            }
            //dataModel = new XSSFWorkbook(modelFile);
        //} catch (IOException ex) {
         //   getLog().error(ex.getMessage());
        //    throw new MojoExecutionException("An IO exception on template file raised.", ex);
        //} catch (InvalidFormatException ex) {
        //    getLog().error(ex.getMessage());
        //    throw new MojoExecutionException("InvalidFormatException", ex);
        //}

        final Charset charset = Charset.forName("UTF-8");
        
        BufferedWriter writer = null;
        try {
            final Path directory = resultDirectory.toPath();
            //logger.info("file:"+ file.toFile().getAbsolutePath());
            Files.createDirectories(directory);
            getLog().info("ensure directory " + directory.toString() + " exists");
            final Path file = Paths.get(resultDirectory.toString(), templateFileName.replace(".ftl", ".mdo"));
            writer = newBufferedWriter(file, charset, TRUNCATE_EXISTING, CREATE, WRITE);
            temp.process(modelFile, writer);
        } catch (TemplateException ex) {
            getLog().error(ex.getBlamedExpressionString());
            getLog().error(ex.getFTLInstructionStack());
            getLog().error(ex.getTemplateSourceName());
            throw new MojoExecutionException("An IO exception on template file raised.", ex);
        } catch (IOException ex) {
            if (ex.getCause() != null) {
                getLog().error(ex.getCause().getMessage());
            }
            getLog().error(ex.getMessage());
            throw new MojoExecutionException("An IO exception", ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    getLog().warn(e);
                }
            }
        }
    }
}
