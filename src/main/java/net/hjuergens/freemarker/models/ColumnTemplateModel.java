package net.hjuergens.freemarker.models;

import freemarker.template.*;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
public class ColumnTemplateModel
        extends WrappingTemplateModel
        implements TemplateHashModel, AdapterTemplateModel {

    private final Pair<Cell, Cell> nameAndType;

    public ColumnTemplateModel(Pair<Cell, Cell> column, ObjectWrapper ow) {
        super(ow);  // coming from WrappingTemplateModel
        this.nameAndType = column;
    }

    @Override  // coming from AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return Row.class;
    }

    public TemplateModel getName() {
        return new SimpleScalar( nameAndType.getKey().getStringCellValue().replace(' ', '_') );
    }

    private TemplateModel getTypeName() {
        return new SimpleScalar( nameAndType.getValue().getStringCellValue() );
    }

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        if(s.equals("name"))
            return getName();
        else if(s.equals("type"))
            return getTypeName();
        else return null;
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return false;
    }
}
