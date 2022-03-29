package net.hjuergens.freemarker.models;

import freemarker.template.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

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

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        if("type".equals(s)) {
            return wrap( cell.getCellType() );
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
        final CellType cellType;
        if(cell.getCellType() == CellType.FORMULA)
            cellType = cell.getCachedFormulaResultType();
        else cellType = cell.getCellType();

        switch (cellType) {
            case BOOLEAN:
                return ( cell.getBooleanCellValue() );
            case NUMERIC:
                return ( cell.getNumericCellValue() );
            case STRING:
                return ( cell.getStringCellValue() );
            case BLANK:
                return ( "" );
            case ERROR:
                return ( cell.getErrorCellValue() );
            case FORMULA:
                System.out.println(cell.getCellFormula());
                return ( cell.getCellFormula() );
            default:
                return null;
        }
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return cell.getCellType() == CellType.BLANK;
    }
}
