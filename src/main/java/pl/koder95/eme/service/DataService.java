package pl.koder95.eme.service;

import pl.koder95.eme.core.spi.PersonalDataModel;

import java.util.Collection;
import java.util.function.Predicate;

public interface DataService {
    void load();
    void save();
    Collection<PersonalDataModel> findPeople(Predicate<PersonalDataModel> predicate);
    Collection<PersonalDataModel> findPeople(String userText, boolean cancelled);
}
