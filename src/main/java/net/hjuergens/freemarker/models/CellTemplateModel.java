package net.hjuergens.freemarker.models;

import freemarker.template.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
public class CellTemplateModel
        extends WrappingTemplateModel
        implements TemplateHashModel, AdapterTemplateModel {

    private final Cell cell;

    public CellTemplateModel(Cell cell, ObjectWrapper ow) {
        super(ow);  // coming from WrappingTemplateModel
        this.cell = cell;
    }


    @Override  // coming from AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return cell;
    }

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return wrap( cell.getBooleanCellValue() );
            case Cell.CELL_TYPE_NUMERIC:
                return wrap( cell.getNumericCellValue() );
            case Cell.CELL_TYPE_STRING:
                return wrap( cell.getStringCellValue() );
            case Cell.CELL_TYPE_BLANK:
                return wrap( "" );
            case Cell.CELL_TYPE_ERROR:
                return wrap( cell.getErrorCellValue() );
            case Cell.CELL_TYPE_FORMULA:
                System.out.println(cell.getCellFormula());
                return wrap( cell.getCellFormula() );
//                logger.trace(String.format("cell formula=%s", cell.getCellFormula()));
        }
        return wrap( cell );
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return cell.getCellType() == Cell.CELL_TYPE_BLANK;
    }
}
