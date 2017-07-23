/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.u161203;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Kamil
 */
public class Mediator {
    
    private Index[] indices;
    private SearchingModeManager searchingModeManager;
    private JButton searchingButton;
    private SearchingModeButton personSMB, sexSMB, actSMB;
    private JTextField personSearchingField, sexSearchingField, actNumberField, yearField;
    private JRadioButton maleButton, femaleButton;
    private JLabel lastName, name, maleLastName, maleName, femaleLastName, femaleName, act, year;

    public void searchPerson() {
        Index found = SearchMachine.findPerson(personSearchingField.getText()).array(indices);
        setIndexViewValues(found);
    }

    public void searchSex() {
        Index found;
        if (maleButton.isSelected() && !femaleButton.isSelected())
            found = SearchMachine.findMale(sexSearchingField.getText()).array(indices);
        else found = SearchMachine.findFemale(sexSearchingField.getText()).array(indices);
        setIndexViewValues(found);
    }

    public void searchAct() {
        Index found = SearchMachine.findAct(actNumberField.getText() + "/" + yearField.getText()).array(indices);
        setIndexViewValues(found);
    }
    
    private void setIndexViewValues(Index i) {
        int actI = i.getSize()-2;
        int femaleLN = i.getSize() == 6? 2: 0;
        if (lastName!= null) lastName.setText(i.getValue(0));
        if (name!= null) name.setText(i.getValue(1));
        if (maleLastName!= null) maleLastName.setText(i.getValue(0));
        if (maleName!= null) maleName.setText(i.getValue(1));
        if (femaleLastName!= null) femaleLastName.setText(i.getValue(femaleLN));
        if (femaleName!= null) femaleName.setText(i.getValue(femaleLN+1));
        if (act!= null) act.setText(i.getValue(actI));
        if (year!= null) year.setText(i.getValue(actI+1));
    }

    public void setSearchingMode(int searchingMode) {
        disableSearchingComponents();
        searchingModeManager.setMode(searchingMode);
        boolean person = searchingMode == SearchingModeManager.PERSON_MODE;
        boolean sex = searchingMode == SearchingModeManager.SEX_MODE;
        boolean act = searchingMode == SearchingModeManager.ACT_MODE;
        setEnableSearchingComponents(person, sex, sex, sex, act, act);
    }
    
    public void disableSearchingComponents() {
        setEnableSearchingComponents(false, false, false, false, false, false);
    }
    
    public void setEnableSearchingComponents(boolean personSearchingField,
            boolean sexSearchingField, boolean maleButton, boolean femaleButton,
            boolean actNumberField, boolean yearField) {
        this.personSearchingField.setEnabled(personSearchingField);
        this.sexSearchingField.setEnabled(sexSearchingField);
        this.maleButton.setEnabled(maleButton);
        this.femaleButton.setEnabled(femaleButton);
        this.actNumberField.setEnabled(actNumberField);
        this.yearField.setEnabled(yearField);
    }

    public void search() {
        searchingModeManager.getCurrent().search();
    }

    public void registerSearchingModeManager(SearchingModeManager manager) {
        this.searchingModeManager = manager;
    }

    public void registerSearchingButton(JButton searchingButton) {
        this.searchingButton = searchingButton;
    }

    public void registerSearchingModeButtons(SearchingModeButton personSMB,
            SearchingModeButton sexSMB, SearchingModeButton actSMB) {
        this.personSMB = personSMB;
        this.sexSMB = sexSMB;
        this.actSMB = actSMB;
    }

    public void registerPersonSearchingField(JTextField personSearchingField) {
        this.personSearchingField = personSearchingField;
    }

    public void registerSexSearchingFields(JTextField sexSearchingField,
            JRadioButton maleButton, JRadioButton femaleButton) {
        this.sexSearchingField = sexSearchingField;
        this.maleButton = maleButton;
        this.femaleButton = femaleButton;
    }

    public void registerActSearchingFields(JTextField actNumberField,
            JTextField yearField) {
        this.actNumberField = actNumberField;
        this.yearField = yearField;
    }

    public void registerIndexArray(Index[] indices) {
        this.indices = indices;
    }

    public void registerIndexView(JLabel maleLastName, JLabel maleName,
            JLabel femaleLastName, JLabel femaleName, JLabel act, JLabel year) {
        this.maleLastName = maleLastName;
        this.maleName = maleName;
        this.femaleLastName = femaleLastName;
        this.femaleName = femaleName;
        registerIndexView(act, year);
    }

    public void registerIndexView(JLabel lastName, JLabel name, JLabel act, JLabel year) {
        this.lastName = lastName;
        this.name = name;
        registerIndexView(act, year);
    }

    public void registerIndexView(JLabel act, JLabel year) {
        this.act = act;
        this.year = year;
    }
    
}
