// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.oozie.scheduler.ui;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.oozie.scheduler.controller.SchedulingDialogController;
import org.talend.oozie.scheduler.i18n.Messages;

/**
 * Created by Marvin Wang on Mar.31, 2012 for
 */
public class SchedulingDialog extends Dialog {

    private Button startTimeBtn;

    private Button endTimeBtn;

    private Combo timeUnitCombo;

    private Text startTimeTxt;

    private Text endTimeTxt;

    private Date startDate;

    private Date endDate;

    private String frequencyValue;

    private String[] timeUnitItemValues;

    private SchedulingDialogController controller;

    private String selectedTimeUnit;

    private int selectedTimeUnitIndex;

    /**
     * @param parentShell
     */
    public SchedulingDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(this.getShellStyle() | SWT.RESIZE);

        controller = new SchedulingDialogController(this);
    }

    protected Control createDialogArea(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);

        GridLayout gridLayout = new GridLayout(4, false);
        comp.setLayout(gridLayout);

        // Frequency
        Label frequencyLbl = new Label(comp, SWT.NONE);
        frequencyLbl.setText(Messages.getString("Label_Frequency"));
        GridDataFactory.fillDefaults().grab(false, false).applyTo(frequencyLbl);

        Text frequencyTxt = new Text(comp, SWT.BORDER);
        frequencyTxt.setText(frequencyValue == null ? "1" : frequencyValue);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(frequencyTxt);

        // Time Unit
        Label timeUnitLbl = new Label(comp, SWT.NONE);
        timeUnitLbl.setText(Messages.getString("Label_TimeUnit"));
        GridDataFactory.fillDefaults().grab(false, false).applyTo(timeUnitLbl);

        timeUnitCombo = new Combo(comp, SWT.READ_ONLY);
        timeUnitCombo.setItems(timeUnitItemValues == null ? new String[] {} : timeUnitItemValues);
        timeUnitCombo.select(0);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(timeUnitCombo);

        // Start Time
        Label startTimeLbl = new Label(comp, SWT.NONE);
        startTimeLbl.setText(Messages.getString("Label_StartTime"));

        startTimeTxt = new Text(comp, SWT.BORDER);
        startTimeTxt.setEditable(false);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(startTimeTxt);

        startTimeBtn = new Button(comp, SWT.PUSH);
        startTimeBtn.setText(Messages.getString("Button_StartTime"));
        GridData startTimeBtnGD = new GridData();
        startTimeBtnGD.grabExcessHorizontalSpace = false;
        GridDataFactory.createFrom(startTimeBtnGD).applyTo(startTimeBtn);

        // End Time
        Label endTimeLbl = new Label(comp, SWT.NONE);
        endTimeLbl.setText(Messages.getString("Label_EndTime"));

        endTimeTxt = new Text(comp, SWT.BORDER);
        endTimeTxt.setEditable(false);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(endTimeTxt);

        endTimeBtn = new Button(comp, SWT.PUSH);
        endTimeBtn.setText(Messages.getString("Button_EndTime"));
        GridData endTimeBtnGD = new GridData();
        endTimeBtnGD.grabExcessHorizontalSpace = false;
        GridDataFactory.createFrom(endTimeBtnGD).grab(false, false).applyTo(endTimeBtn);

        // Registers the listener of widgets as required.
        registerAllListeners();
        return parent;
    }

    protected void registerAllListeners() {
        regTimeUnitComboListener();
        regStartTimeBtnListener();
        regEndTimeBtnListener();
    }

    /**
     * Registers a listener for the Combo widget that is used to store the time units.
     */
    private void regTimeUnitComboListener() {
        timeUnitCombo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                controller.doTimeUnitAction();
            }
        });
    }

    /**
     * Registers a listener for the "Start Time" button widget.
     */
    private void regStartTimeBtnListener() {
        startTimeBtn.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                controller.doStartTimeAction();
            }
        });
    }

    private void regEndTimeBtnListener() {
        endTimeBtn.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                controller.doEndTimeAction();
            }
        });
    }

    protected void okPressed() {
        doOkAction();
        super.okPressed();
    }

    protected void doOkAction() {
        controller.doOKAction();
    }

    protected void canclePressed() {
        doCancleAction();
        super.cancelPressed();
    }

    protected void doCancleAction() {
    }

    /**
     * Reset the dialog size.
     */
    protected Point getInitialSize() {
        Point result = super.getInitialSize();
        result.x = 500;
        result.y = 200;
        return result;
    }

    public String getFrequencyValue() {
        return this.frequencyValue;
    }

    public void setFrequencyValue(String frequencyValue) {
        this.frequencyValue = frequencyValue;
    }

    public String[] getTimeUnitItemValues() {
        return this.timeUnitItemValues;
    }

    public void setTimeUnitItemValues(String[] timeUnitItemValues) {
        this.timeUnitItemValues = timeUnitItemValues;
    }

    public Text getStartTimeTxt() {
        return this.startTimeTxt;
    }

    public void setStartTimeTxt(Text startTimeTxt) {
        this.startTimeTxt = startTimeTxt;
    }

    public Text getEndTimeTxt() {
        return this.endTimeTxt;
    }

    public void setEndTimeTxt(Text endTimeTxt) {
        this.endTimeTxt = endTimeTxt;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Combo getTimeUnitCombo() {
        return this.timeUnitCombo;
    }

    public void setTimeUnitCombo(Combo timeUnitCombo) {
        this.timeUnitCombo = timeUnitCombo;
    }

    public String getSelectedTimeUnit() {
        return this.selectedTimeUnit;
    }

    public void setSelectedTimeUnit(String selectedTimeUnit) {
        this.selectedTimeUnit = selectedTimeUnit;
    }

    public int getSelectedTimeUnitIndex() {
        return this.selectedTimeUnitIndex;
    }

    public void setSelectedTimeUnitIndex(int selectedTimeUnitIndex) {
        this.selectedTimeUnitIndex = selectedTimeUnitIndex;
    }

}
