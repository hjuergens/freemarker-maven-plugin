package net.hjuergens.freemarker.models;

import freemarker.template.*;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * created on 29.09.15
 *
 * @author juergens
 */
class WorkbookTemplateModel
        extends WrappingTemplateModel
        implements TemplateSequenceModel, TemplateHashModel, AdapterTemplateModel  {

    private final Workbook workbook;

    public WorkbookTemplateModel(Workbook workbook, ObjectWrapper ow) {
        super(ow);  // coming from WrappingTemplateModel
        this.workbook = workbook;
    }

    @Override  // coming from TemplateSequenceModel
    public int size() throws TemplateModelException {
        return workbook.getNumberOfSheets();
    }

    @Override  // coming from TemplateSequenceModel
    public TemplateModel get(int index) throws TemplateModelException {
        return wrap( workbook.getSheetAt(index) );
    }

    @Override  // coming from AdapterTemplateModel
    public Object getAdaptedObject(Class hint) {
        return workbook;
    }

    @Override
    public TemplateModel get(String s) throws TemplateModelException {
        if(s.equals("workbook"))
            return wrap(workbook);
        else
            return wrap( workbook.getSheet(s) );
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return size() > 0;
    }
}
