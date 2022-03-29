package net.hjuergens.freemarker.models;

import freemarker.template.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
class POIFileTemplateModel
        extends WrappingTemplateModel
        implements TemplateCollectionModelEx, TemplateHashModel, AdapterTemplateModel {

    private final File filename;

    public POIFileTemplateModel(File workbookFile, ObjectWrapper ow) {
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
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(filename, s, true);
            } catch (IOException ex) {
                throw new TemplateModelException(ex);
            } catch (EncryptedDocumentException ex) {
                throw new TemplateModelException(ex);
            }
            return wrap(workbook);
        } else if (s.equals("name")) {
            return wrap(filename);
        } else {
            return null;//throw new TemplateModelException("unknown " + s);
        }
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return filename.toPath().getNameCount() > 0;
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
