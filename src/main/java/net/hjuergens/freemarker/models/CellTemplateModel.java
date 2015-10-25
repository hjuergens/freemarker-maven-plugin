package net.hjuergens.freemarker.models;

import freemarker.template.*;
import org.apache.poi.ss.usermodel.Cell;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
class CellTemplateModel
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

    public Number getCellType() {
        return cell.getCellType();
    }

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        if("type".equals(s)) {
            return wrap( getCellType() );
        } else if("columnIndex".equals(s)) {
            return wrap( cell.getColumnIndex() );
        } else if("row".equals(s)) {
            return wrap( cell.getRow() );
        } else if("sheet".equals(s)) {
            return wrap( cell.getSheet() );
        } else if("value".equals(s)) {
            return wrap( getValue(cell) );
        } else return null;
    }

    public static Object getValue(Cell cell) {
        final int cellType;
        if(cell.getCellType() == Cell.CELL_TYPE_FORMULA)
            cellType = cell.getCachedFormulaResultType();
        else cellType = cell.getCellType();

        switch (cellType) {
            case Cell.CELL_TYPE_BOOLEAN:
                return ( cell.getBooleanCellValue() );
            case Cell.CELL_TYPE_NUMERIC:
                return ( cell.getNumericCellValue() );
            case Cell.CELL_TYPE_STRING:
                return ( cell.getStringCellValue() );
            case Cell.CELL_TYPE_BLANK:
                return ( "" );
            case Cell.CELL_TYPE_ERROR:
                return ( cell.getErrorCellValue() );
            case Cell.CELL_TYPE_FORMULA:
                System.out.println(cell.getCellFormula());
                return ( cell.getCellFormula() );
            default:
                return null;
        }
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return cell.getCellType() == Cell.CELL_TYPE_BLANK;
    }
}
