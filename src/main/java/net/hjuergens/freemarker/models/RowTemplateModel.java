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
class RowTemplateModel
        extends WrappingTemplateModel
        implements TemplateSequenceModel, TemplateHashModel, AdapterTemplateModel {

    private final Row row;

    public RowTemplateModel(Row row, ObjectWrapper ow) {
        super(ow);  // coming from WrappingTemplateModel
        this.row = row;
    }

    @Override  // coming from AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return row;
    }

    public Number getRowNum() {
        return row.getRowNum();
    }
    public Number getSize() {
        return row.getLastCellNum() - row.getFirstCellNum();
    }

    //@Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new TemplateModelIterator() {
            private final Iterator<Cell> iterator = row.iterator();
            @Override
            public TemplateModel next() throws TemplateModelException {
                return wrap(iterator.next() );
            }

            @Override
            public boolean hasNext() throws TemplateModelException {
                return iterator.hasNext();
            }

            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

        };
    }

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        if (s.equals("number"))
            return wrap(getRowNum());
        else if (s.equals("size"))
            return wrap(getSize());
        else return null;
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return size() == 0;
    }

    @Override
    public TemplateModel get(int i) throws TemplateModelException {
        return wrap(row.getCell(i, Row.CREATE_NULL_AS_BLANK));
    }

    @Override
    public int size() throws TemplateModelException {
        return row.getLastCellNum() - row.getFirstCellNum();
    }
}
