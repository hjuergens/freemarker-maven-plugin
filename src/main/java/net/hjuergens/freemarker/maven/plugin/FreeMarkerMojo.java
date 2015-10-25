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
import net.hjuergens.freemarker.models.WorkbookWrapper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Set;

import static java.nio.file.Files.getFileStore;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.*;

/**
 * Goal which uses FreeMarker
 *
 */
@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FreeMarkerMojo
        extends AbstractMojo {

    /**
     * Location of the template file.
     */
    @Parameter(property = "templateFile", required = false)
    private File templateFile;

    /**
     * Location of the template files.
     */
    @Parameter(property = "templateRootDirectory", required = false)
    private File templateRootDirectory;

    /**s
     * Location of the output directory.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-resources", property = "resultDirectory", required = true)
    private File resultDirectory;

    /**
     * Location of the Excel file containing the model.
     */
    @Parameter(property = "modelFile", required = true)
    private File modelFile;

    public void execute()
            throws MojoExecutionException {
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
            throw new MojoExecutionException("File name has to end with '.xlsx'.");
        }

        if(templateFile == null && templateRootDirectory == null) {
            throw new MojoExecutionException("Either the template file or the root directory must be declared.");
        }
        if (templateFile != null) {
            getLog().info(templateFile.toString());
            if (!templateFile.exists()) {
                throw new MojoExecutionException("Template path " + templateFile.toString() + " does not exists.");
            }
            if (!templateFile.isFile() || !templateFile.canRead()) {
                throw new MojoExecutionException("Template path has to be a readable file.");
            }
        }
        if (templateRootDirectory != null) {
            if (!templateRootDirectory.exists()) {
                throw new MojoExecutionException("Template path " + templateRootDirectory.toString() + " does not exists.");
            }
            if (!templateRootDirectory.isDirectory() || !templateRootDirectory.canRead()) {
                throw new MojoExecutionException("Template path has to be a readable directory.");
            }
        }

        if (templateFile != null) {
            try {
                processFile(templateFile.getParentFile(), templateFile.getName(), resultDirectory.toPath());
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(),e);
            } catch (TemplateException e) {
                throw new MojoExecutionException(e.getTemplateSourceName(),e);
            }
        }

        if (templateRootDirectory != null) {
            Path start = templateRootDirectory.toPath();
            Set<FileVisitOption> options = Collections.emptySet();
            int maxDepth = Integer.MAX_VALUE;
            FileVisitor<? super Path> visitor = new FileVisitor<Path>(){
                Path relativeOutputDirectory;
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    relativeOutputDirectory = templateRootDirectory.toPath().relativize(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        Path outputDirectory = resultDirectory.toPath().resolve(relativeOutputDirectory);
                        if(file.toString().endsWith(".ftl"))
                            processFile(file.getParent().toFile(), file.getFileName().toString(), outputDirectory);
                    } catch (TemplateException e) {
                        getLog().error(e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    getLog().error(exc);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    relativeOutputDirectory = null;
                    return FileVisitResult.CONTINUE;
                }
            };
            try {
                Files.walkFileTree(start, options, maxDepth, visitor);
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

        }
    }

    private void processDirectory(File[] templateDirectories, Path outputDirectory) throws IOException, TemplateException {
        for(File dir : templateDirectories) {
            File[] files = files(dir);
            for(File entry : files) {
                processFile(dir, entry.getName(), outputDirectory);
            }
            File[] directories = directories(dir);
            processDirectory(directories, outputDirectory);
        }
    }

    private void processFile(File templateDirectory, String templateFileName, Path outputDirectory) throws IOException, TemplateException {
        assert templateDirectory.isDirectory();

        Template temp = getTemplate(templateDirectory, templateFileName);

        processTemplate(templateFileName, temp, outputDirectory);
    }

    private static File[] files(File templateDir) {
        FileFilter filter = new FileFilter(){
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".ftl");
            }
        };
        return templateDir.listFiles(filter);
    }

    private static File[] directories(File templateDir) {
        FileFilter filter = new FileFilter(){
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        };
        return templateDir.listFiles(filter);
    }

    private void processTemplate(String templateFileName, Template temp, Path directory) throws TemplateException, IOException {
        final Charset charset = Charset.forName("UTF-8");

        BufferedWriter writer = null;
        try {
            Files.createDirectories(directory);
            getLog().info("ensure directory " + directory.toString() + " exists");
            final Path file = Paths.get(directory.toString(), templateFileName.replace(".ftl", ""));
            writer = newBufferedWriter(file, charset, TRUNCATE_EXISTING, CREATE, WRITE);
            temp.process(modelFile, writer);
        } catch (TemplateException ex) {
            getLog().error(ex.getBlamedExpressionString());
            getLog().error(ex.getFTLInstructionStack());
            getLog().error(ex.getTemplateSourceName());
            throw ex;
        } catch (IOException ex) {
            if (ex.getCause() != null) {
                getLog().error(ex.getCause().getMessage());
            }
            getLog().error(ex.getMessage());
            throw ex;
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

    private Template getTemplate(File templateDirectory, String templateFileName) throws IOException {
        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        try {
            cfg.setDirectoryForTemplateLoading(templateDirectory);
        } catch (IOException ex) {
            getLog().error(ex);
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setObjectWrapper(new WorkbookWrapper(cfg.getIncompatibleImprovements()));

        /*  next, get the Template  */
        Template temp;
        try {
            temp = cfg.getTemplate(templateFileName);
            getLog().info("find " + temp.getName());
        } catch (MalformedTemplateNameException ex) {
            getLog().error(ex.getMalformednessDescription());
            throw ex;
        } catch (ParseException ex) {
            getLog().error(ex.getEditorMessage());
            throw ex;
        } catch (IOException ex) {
            getLog().error(ex.getMessage());
            throw ex;
        }
        return temp;
    }
}
