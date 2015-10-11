package net.hjuergens.freemarker.models;

import freemarker.template.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
public class WorkbookPOIFileTemplateModel
        extends WrappingTemplateModel
        implements TemplateCollectionModelEx, TemplateHashModel, AdapterTemplateModel {

    private final File filename;

    public WorkbookPOIFileTemplateModel(File workbookFile, ObjectWrapper ow) {
        super(ow);  // coming from WrappingTemplateModel
        this.filename = workbookFile;
    }

    @Override  // coming from AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return filename;
    }

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        if (s.equals("workbook")) {
            try {
                return wrap(new XSSFWorkbook(filename));
            } catch (IOException ex) {
                throw new TemplateModelException(ex);
            } catch (InvalidFormatException ex) {
                throw new TemplateModelException(ex);
            }
        } else if (s.equals("name")) {
            return wrap(filename);
        } else {
            throw new TemplateModelException("unknown " + s);
        }
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return filename.toPath().getNameCount() > 0;
    }

    @Override
    public boolean contains(TemplateModel tm) throws TemplateModelException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        final Iterator<Path> entries = filename.toPath().iterator();
        return new TemplateModelIterator() {
            public TemplateModel next() throws TemplateModelException {
                return wrap(entries.next());
            }

            public boolean hasNext() throws TemplateModelException {
                return entries.hasNext();
            }
        };
    }

    @Override
    public int size() throws TemplateModelException {
        return filename.toPath().getNameCount();
    }
}
