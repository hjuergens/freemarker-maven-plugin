package net.hjuergens.freemarker.models;

import fj.F;
import fj.F2;
import freemarker.template.*;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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

    @Override  // coming from TemplateSequenceModel
    public int size() throws TemplateModelException {
        return sheet.getPhysicalNumberOfRows();
    }

    @Override  // coming from TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        return wrap( sheet.getRow(index) );
    }

    @Override  // coming from AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return sheet;
    }

    public TemplateModel getName() {
        return new SimpleScalar( sheet.getSheetName().replace(' ', '_') );
    }

    public TemplateModel getColumns() {
        final Row firstRow = sheet.getRow(sheet.getFirstRowNum());
        final Row secondRow = sheet.getRow(sheet.getFirstRowNum()+1);

        final fj.data.List<Cell> nameList = fj.data.List.list(firstRow.cellIterator());
        final fj.data.List<Cell> typeList = fj.data.List.list(secondRow.cellIterator());

        //F<Cell, F<Object, Object>> f = null;

//        F<Cell, F<Cell, ? extends Object>> f = new F<Cell, F<Cell, ? extends Object>>() {
//            @Override
//            public F<Cell, ? extends Object> f(Cell cell) {
//                return new F<>() {
//                    @Override
//                    public Object f(Object o) {
//                        return null;
//                    }
//                };
//            }
//        };
        F2<Cell, Cell, Pair<Cell,Cell>> f = new F2<Cell, Cell, Pair<Cell,Cell>>() {
            @Override
            public Pair<Cell,Cell> f(Cell c1, Cell c2) {
                return new Pair<Cell,Cell>(c1, c2);
            }
        };
        ;
        //return new ColumnTemplateModel( firstRow, getObjectWrapper() );

//        return new TemplateSequenceModel() {
//            @Override
//            public TemplateModel get(int i) throws TemplateModelException {
//                return wrap(new Pair<>(firstRow.getCell(i), secondRow.getCell(i)));
//            }
//
//            @Override
//            public int size() throws TemplateModelException {
//                return Math.min(firstRow.getLastCellNum(),secondRow.getLastCellNum()) -
//                        Math.max(firstRow.getFirstCellNum(), secondRow.getFirstCellNum());
//            }
//        };
        return new SimpleSequence(nameList.zipWith(typeList, f).toCollection(), getObjectWrapper());
    }

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        if(s.equals("name"))
            return getName();
        else if(s.equals("columns"))
            return getColumns();
        else return null;
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return size() > 0;
    }
}
