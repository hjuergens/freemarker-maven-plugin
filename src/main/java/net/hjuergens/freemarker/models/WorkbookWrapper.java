package net.hjuergens.freemarker.models;

import freemarker.template.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;

/**
 * created on 03.10.15
 *
 * @author juergens
 */
public class WorkbookWrapper 
        extends DefaultObjectWrapper
        implements ObjectWrapperAndUnwrapper {

    public WorkbookWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TemplateModel handleUnknownType(final Object obj)
            throws TemplateModelException {
        if (obj instanceof File) {
            return new POIFileTemplateModel((File) obj, this);
        } else if (obj instanceof Workbook) {
            return new WorkbookTemplateModel((Workbook) obj, this);
        } else if (obj instanceof Sheet) {
            return new SheetTemplateModel((Sheet) obj, this);
        } else if (obj instanceof Row) {
            return new RowTemplateModel((Row) obj, this);
        } else if (obj instanceof Cell) {
            return new CellTemplateModel((Cell) obj, this);
        } else if (obj instanceof Pair<?,?>) {
            return new ColumnTemplateModel((Pair<Cell, Cell>) obj, this);
        }
        return super.handleUnknownType(obj);
    }
}
