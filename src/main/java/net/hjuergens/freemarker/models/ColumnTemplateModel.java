package net.hjuergens.freemarker.models;

import freemarker.template.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
class ColumnTemplateModel
        extends WrappingTemplateModel
        implements TemplateHashModel, AdapterTemplateModel {

    private final Pair<Cell, Cell> nameAndType;

    public ColumnTemplateModel(Pair<Cell, Cell> column, ObjectWrapper ow) {
        super(ow);  // coming from WrappingTemplateModel
        this.nameAndType = column;
    }

    /**
     * TODO override
     * @param hint a class
     * @return
     */
    public Object getAdaptedObject(Class hint) {
        return Row.class;
    }

    public TemplateModel getName() {
        return new SimpleScalar( nameAndType.getKey().getStringCellValue().replace(' ', '_') );
    }

    private TemplateModel getTypeName() {
        return new SimpleScalar( nameAndType.getValue().getStringCellValue() );
    }

    /**
     * TODO override
     * @param s a key
     * @return
     * @throws TemplateModelException
     */
    public TemplateModel get(String s) throws TemplateModelException {
        if(s.equals("name"))
            return getName();
        else if(s.equals("typeName"))
            return getTypeName();
        else if(s.equals("type")) {
            final Cell cell = nameAndType.getValue();
            final int cellType;
            if(cell.getCellType() == Cell.CELL_TYPE_FORMULA)
                cellType = cell.getCachedFormulaResultType();
            else cellType = cell.getCellType();
            return wrap(cellType);
        }
        else return null;
    }

    /**
     * TODO override
     * @return
     * @throws TemplateModelException
     */
    public boolean isEmpty() throws TemplateModelException {
        return false;
    }
}
