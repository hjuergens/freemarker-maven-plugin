package net.hjuergens.freemarker.models;

import fj.F2;
import freemarker.template.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
public class SheetTemplateModel
        extends WrappingTemplateModel
        implements TemplateSequenceModel, TemplateHashModel, AdapterTemplateModel {

    private final Sheet sheet;

    public SheetTemplateModel(Sheet sheet, ObjectWrapper ow) {
        super(ow);  // coming from WrappingTemplateModel
        this.sheet = sheet;
    }

    /**
     * TODO Override  // coming from TemplateSequenceModel
     *
     * @return
     * @throws TemplateModelException
     */
    public int size() throws TemplateModelException {
        return sheet.getPhysicalNumberOfRows();
    }

    /**
     * TODO Override  // coming from TemplateSequenceModel
     *
     * @param index
     * @return
     * @throws TemplateModelException
     */
    public TemplateModel get(int index) throws TemplateModelException {
        return wrap(sheet.getRow(index));
    }

    /**
     * TODO Override  // coming from AdapterTemplateModel
     *
     * @param hint
     * @return
     */
    public Object getAdaptedObject(Class hint) {
        return sheet;
    }

    public TemplateModel getName() {
        return new SimpleScalar(sheet.getSheetName().replace(' ', '_'));
    }

    public TemplateModel getColumns() {
        final Row firstRow = sheet.getRow(sheet.getFirstRowNum());
        final Row secondRow = sheet.getRow(sheet.getFirstRowNum() + 1);

        final fj.data.List<Cell> nameList = fj.data.List.list(firstRow.cellIterator());
        final fj.data.List<Cell> typeList = fj.data.List.list(secondRow.cellIterator());

        F2<Cell, Cell, Pair<Cell, Cell>> f = new F2<Cell, Cell, Pair<Cell, Cell>>() {
            /**
             * TODO override
             * @param c1
             * @param c2
             * @return
             */
            public Pair<Cell, Cell> f(Cell c1, Cell c2) {
                return Pair.of(c1, c2);
            }
        };
        return new SimpleSequence(nameList.zipWith(typeList, f).toCollection(), getObjectWrapper());
    }

    /**
     * TODO override
     *
     * @param s
     * @return
     * @throws TemplateModelException
     */
    public TemplateModel get(String s) throws TemplateModelException {
        if (s.equals("name"))
            return getName();
        else if (s.equals("columns"))
            return getColumns();
        else return null;
    }

    /**
     * TODO override
     *
     * @return
     * @throws TemplateModelException
     */
    public boolean isEmpty() throws TemplateModelException {
        return size() > 0;
    }
}
