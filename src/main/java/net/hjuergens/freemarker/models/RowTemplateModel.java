package net.hjuergens.freemarker.models;

import freemarker.template.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
public class RowTemplateModel
        extends WrappingTemplateModel
        implements TemplateCollectionModel, AdapterTemplateModel {

    private final Row row;


    public RowTemplateModel(Row row, ObjectWrapper ow) {
        super(ow);  // coming from WrappingTemplateModel
        this.row = row;
    }

    @Override  // coming from AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return row;
    }

    @Override
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
}
